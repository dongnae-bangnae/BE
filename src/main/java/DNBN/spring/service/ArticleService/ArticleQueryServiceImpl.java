package DNBN.spring.service.ArticleService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;

import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.aws.s3.AmazonS3Manager;
import DNBN.spring.converter.ArticleConverter;
import DNBN.spring.domain.*;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;

import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Region;

import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepositoryCustom;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;

import DNBN.spring.web.dto.ArticleResponseDTO;

import DNBN.spring.web.dto.response.PostResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryServiceImpl implements ArticleQueryService {
    private final ArticleRepository articleRepository;
    private final LikeRegionRepository likeRegionRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final ArticlePhotoRepository articlePhotoRepository;
    private final ArticleRepositoryCustom articleRepositoryCustom;
    private final MemberRepository memberRepository;

    @Override
    public Page<Article> getArticleListByRegion(Long memberId, Integer page) {
        List<Long> regionIds = likeRegionRepository.findRegionIdsByMemberId(memberId);

        if (regionIds.isEmpty()) {
            throw new IllegalArgumentException("관심 지역이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return articleRepository.findAllByRegion_IdIn(regionIds, pageable);
    }

    @Override
    public PostResponseDTO.PostPreViewDTO getTopChallengeArticle() {
        String requiredTag = "8월 챌린지";
        Article topArticle = articleRepository.findTopByHashtagOrderByLikesCountDescCreatedAtAsc(requiredTag)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_CHALLENGE_NOT_FOUND));
        return ArticleConverter.articlePreViewDTO(topArticle);
    }

    @Override
    public ArticleResponseDTO.ArticleListDTO getArticlesByCategory(Long categoryId, Long memberId, Long cursor, Long limit) {
        // 1. 사용자 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 카테고리 소유 확인
        Category category = categoryRepository.findByCategoryIdAndMemberAndDeletedAtIsNull(categoryId, member)
                .orElseThrow(() -> new CategoryHandler(ErrorStatus._FORBIDDEN));

        // 3. 게시물 조회 (limit + 1 조회로 hasNext 판별)
        List<Article> articles = articleRepositoryCustom.findArticlesByCategoryWithCursor(categoryId, cursor, limit + 1);

        boolean hasNext = articles.size() > limit;
        if (hasNext) articles.remove(articles.size() - 1);

        // 4. 변환
        List<ArticleResponseDTO.ArticlePreviewDTO> previews = articles.stream()
                .map(article -> {
                    String mainImage = articlePhotoRepository.findAllByArticle(article).stream()
                            .filter(ArticlePhoto::getIsMain)
                            .findFirst()
                            .map(photo -> photo.getFileKey())
                            .orElse("기본 이미지 URL");

                    return ArticleConverter.toPreviewDTO(article, mainImage);
                }).toList();

        return ArticleConverter.toListDTO(previews, limit, hasNext);
    }

    @Override
    public ArticleResponseDTO.ArticleDetailDTO getArticleDetail(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
        List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(article);
        return ArticleConverter.toArticleDetailDTO(article, photos);
    }
}
package DNBN.spring.service.ArticleService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.converter.ArticleConverter;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticleLikeId;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.domain.ArticleSpamId;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepositoryCustom;
import DNBN.spring.repository.ArticleSpamRepository.ArticleSpamRepository;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.web.dto.response.ArticleResponseDTO;
import DNBN.spring.web.dto.response.PostResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryServiceImpl implements ArticleQueryService {

    @Value("${article.default-image-uuid}")
    private String defaultImageUuid;

    private static final long DEFAULT_LIMIT = 10L;

    private final ArticleRepository articleRepository;
    private final LikeRegionRepository likeRegionRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final ArticlePhotoRepository articlePhotoRepository;
    private final ArticleRepositoryCustom articleRepositoryCustom;
    private final MemberRepository memberRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleSpamRepository articleSpamRepository;
    private final PlaceRepository placeRepository;

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
        long effectiveLimit = (limit != null) ? limit : DEFAULT_LIMIT;

        if (cursor == null || cursor == -1L) {
            cursor = null;
        }

        // 1. 사용자 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 카테고리 소유 확인
        Category category = categoryRepository.findByCategoryIdAndMemberAndDeletedAtIsNull(categoryId, member)
                .orElseThrow(() -> new CategoryHandler(ErrorStatus._FORBIDDEN));

        // 3. 게시물 조회 (limit + 1 조회로 hasNext 판별)
        List<Article> articles = articleRepositoryCustom.findArticlesByCategoryWithCursor(categoryId, cursor, effectiveLimit + 1);

        boolean hasNext = articles.size() > effectiveLimit;
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
        if (article.getDeletedAt() != null) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_ALREADY_DELETED);
        }
        List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(article);
        return ArticleConverter.toArticleDetailDTO(article, photos);
    }

    // V1: 단일 커서(Long) 방식
    @Override
    public List<ArticleResponseDTO.ArticleListItemDTO> getArticleListV1(Long memberId, Long placeId, Long cursor, Long limit) {
        long effectiveLimit = (limit != null) ? limit : DEFAULT_LIMIT; // limit가 null인 경우 기본값 설정

        if (cursor == null || cursor == -1L) {
            cursor = null;
        }

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Place place = placeRepository.findPlaceByPlaceId(placeId)
            .orElseThrow(() -> new ArticleHandler(ErrorStatus.PLACE_NOT_FOUND));
        List<Article> articles = articleRepositoryCustom.findArticlesByPlaceWithCursorV1(placeId, cursor, effectiveLimit + 1);

        boolean hasNext = articles.size() > effectiveLimit;
        if (hasNext) articles.remove(articles.size() - 1);

        return articles.stream()
            .map(article -> {
                Long articleId = article.getArticleId();
                String mainImageUuid = getMainImageUuid(article);
                boolean isLiked = articleLikeRepository.existsById(
                    new ArticleLikeId(articleId, memberId)
                );
                boolean isSpammed = articleSpamRepository.existsById(
                    new ArticleSpamId(articleId, memberId)
                );
                boolean isMine = memberId.equals(article.getMember().getId());

                log.debug("articleId: {}, isLiked: {}, isSpammed: {}, isMine: {}", articleId, isLiked, isSpammed, isMine);
                return ArticleConverter.toArticleListItemDTO(article, mainImageUuid, isLiked, isSpammed, isMine);
            })
            .toList();
    }

    // V2: 복합 커서(LocalDateTime, Long) 방식
    @Override
    public List<ArticleResponseDTO.ArticleListItemDTO> getArticleListV2(Long memberId, Long placeId, LocalDateTime cursorCreatedAt, Long cursorArticleId, Long limit) {
        long effectiveLimit = (limit != null) ? limit : DEFAULT_LIMIT;
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Place place = placeRepository.findPlaceByPlaceId(placeId)
            .orElseThrow(() -> new ArticleHandler(ErrorStatus.PLACE_NOT_FOUND));
        List<Article> articles = articleRepositoryCustom.findArticlesByPlaceWithCursorV2(placeId, cursorCreatedAt, cursorArticleId, effectiveLimit + 1);

        boolean hasNext = articles.size() > effectiveLimit;
        if (hasNext) articles.remove(articles.size() - 1);

        return articles.stream()
            .map(article -> {
                Long articleId = article.getArticleId();
                String mainImageUuid = getMainImageUuid(article);
                boolean isLiked = articleLikeRepository.existsById(
                    new ArticleLikeId(articleId, memberId)
                );
                boolean isSpammed = articleSpamRepository.existsById(
                    new ArticleSpamId(articleId, memberId)
                );
                boolean isMine = memberId.equals(article.getMember().getId());

                log.debug("articleId: {}, isLiked: {}, isSpammed: {}, isMine: {}", articleId, isLiked, isSpammed, isMine);
                return ArticleConverter.toArticleListItemDTO(article, mainImageUuid, isLiked, isSpammed, isMine);
            })
            .toList();
    }

    /**
     * 게시글의 대표 이미지 UUID 반환
     * 대표 이미지가 없는 경우 기본 이미지 UUID 반환
     */
    private String getMainImageUuid(Article article) {
        return articlePhotoRepository.findFirstByArticleAndIsMainTrue(article)
            .map(ArticlePhoto::getFileKey)
            .orElseGet(() -> defaultImageUuid);
    }
}
package DNBN.spring.service.ArticleService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.apiPayload.exception.handler.RegionHandler;
import DNBN.spring.aws.s3.AmazonS3Manager;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.ArticleRequestDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCommandServiceImpl implements ArticleCommandService {
    private final ArticleRepository articleRepository;
    private final ArticlePhotoRepository articlePhotoRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final AmazonS3Manager s3Manager;

    @Override
    @Transactional
    public ArticleWithPhotos createArticle(Long memberId, ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND));

        // TODO: 추가적인 규칙 검증
        
        Article article = Article.builder()
                .member(member)
                .category(category)
                .place(place)
                .region(region)
                .title(request.title())
                .date(request.date())
                .content(request.content())
                .likesCount(0L)
                .spamCount(0L)
                .build();
        articleRepository.save(article);

        List<ArticlePhoto> photos = new ArrayList<>();
        // 대표 이미지 S3 업로드 및 UUID 저장
        if (mainImage != null && !mainImage.isEmpty()) {
            String uuid = java.util.UUID.randomUUID().toString();
            String mainImageKey = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), mainImage);
            ArticlePhoto mainPhoto = ArticlePhoto.builder()
                    .article(article)
                    .place(place)
                    .region(region)
                    .fileKey(mainImageKey)
                    .orderIndex(0)
                    .isMain(true) // 대표 이미지로 설정
                    .build();
            photos.add(articlePhotoRepository.save(mainPhoto));
        }
        // 추가 이미지 S3 업로드 및 UUID 저장
        if (imageFiles != null) {
            int idx = 1;
            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    String uuid = java.util.UUID.randomUUID().toString();
                    String imageKey = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), file);
                    ArticlePhoto photo = ArticlePhoto.builder()
                            .article(article)
                            .place(place)
                            .region(region)
                            .fileKey(imageKey)
                            .orderIndex(idx++)
                            .isMain(false) // 일반 이미지로 설정
                            .build();
                    photos.add(articlePhotoRepository.save(photo));
                }
            }
        }
        return new ArticleWithPhotos(article, photos);
    }
}

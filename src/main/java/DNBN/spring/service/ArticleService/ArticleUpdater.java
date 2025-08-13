package DNBN.spring.service.ArticleService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.apiPayload.exception.handler.RegionHandler;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.validation.validator.ContentLengthValidator;
import DNBN.spring.validation.validator.TitleLengthValidator;
import DNBN.spring.web.dto.request.ArticleUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUpdater {
    private final TitleLengthValidator titleLengthValidator;
    private final ContentLengthValidator contentLengthValidator;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;

    public void updateArticleEntity(Article article, ArticleUpdateRequestDTO request) {
        if (request.title() != null) {
            titleLengthValidator.validateArticleTitle(request.title());
            article.setTitle(request.title());
        }
        if (request.content() != null) {
            contentLengthValidator.validateArticleContent(request.content());
            article.setContent(request.content());
        }
        if (request.date() != null) {
            article.setDate(request.date());
        }
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
            article.setCategory(category);
        }
        if (request.regionId() != null) {
            Region region = regionRepository.findById(request.regionId())
                    .orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND));
            article.setRegion(region);
        }
        if (request.placeId() != null) {
            Place place = placeRepository.findById(request.placeId())
                    .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
            article.setPlace(place);
        }
    }
}


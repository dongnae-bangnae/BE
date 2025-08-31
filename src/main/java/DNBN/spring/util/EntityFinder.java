package DNBN.spring.util;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.apiPayload.exception.handler.RegionHandler;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;

public class EntityFinder {
    private EntityFinder() {}

    public static Member getMemberOrThrow(MemberRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }
    public static Category getCategoryOrThrow(CategoryRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
    }
    public static Place getPlaceOrThrow(PlaceRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
    }
    public static Region getRegionOrThrow(RegionRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND));
    }
    public static Article getArticleOrThrow(ArticleRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
    }
}


package DNBN.spring.converter;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.web.dto.ArticleResponseDTO;

import java.util.List;

public class ArticleConverter {
    public static ArticleResponseDTO toArticleResponseDTO(Article article, List<ArticlePhoto> photos) {
        return ArticleResponseDTO.builder()
                .articleId(article.getArticleId())
                .memberId(article.getMember().getId())
                .categoryId(article.getCategory().getCategoryId())
                .placeId(article.getPlace().getPlaceId())
                .regionId(article.getRegion().getId())
                .title(article.getTitle())
                .date(article.getDate())
                .content(article.getContent())
                .mainImageUuid(ArticlePhotoConverter.extractMainImageUuid(photos))
                .imageUuids(ArticlePhotoConverter.extractImageUuids(photos))
                .likeCount(article.getLikesCount())
                .spamCount(article.getSpamCount())
                .createdAt(article.getCreatedAt().toString())
                .updatedAt(article.getUpdatedAt().toString())
                .build();
    }
}

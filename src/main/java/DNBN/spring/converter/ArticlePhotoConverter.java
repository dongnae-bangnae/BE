package DNBN.spring.converter;

import DNBN.spring.domain.ArticlePhoto;
import java.util.List;
import java.util.stream.Collectors;

public class ArticlePhotoConverter {
    public static String extractMainImageUuid(List<ArticlePhoto> photos) {
        return photos.stream()
                .filter(ArticlePhoto::getIsMain)
                .findFirst()
                .map(ArticlePhoto::getFileKey)
                .orElse(null);
    }

    public static List<String> extractImageUuids(List<ArticlePhoto> photos) {
        return photos.stream()
                .map(ArticlePhoto::getFileKey)
                .collect(Collectors.toList());
    }
}


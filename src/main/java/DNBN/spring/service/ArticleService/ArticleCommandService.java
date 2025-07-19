package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.web.dto.ArticleRequestDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleCommandService {
    ArticleWithPhotos createArticle(Long memberId, ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles);

    @AllArgsConstructor
    class ArticleWithPhotos {
        public final Article article;
        public final List<ArticlePhoto> photos;
    }
}

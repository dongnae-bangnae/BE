package DNBN.spring.converter;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.web.dto.ArticleResponseDTO;
import DNBN.spring.web.dto.response.PostResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

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

    public static PostResponseDTO.PostPreViewDTO articlePreViewDTO(Article article) {
        return PostResponseDTO.PostPreViewDTO.builder()
                .articleId(article.getArticleId())
                .memberId(article.getMember().getId())
                .placeId(article.getPlace().getPlaceId())
                .regionId(article.getRegion().getId())
                .title(article.getTitle())
                .content(article.getContent())
                .likeCount(article.getLikesCount())
                .spamCount(article.getSpamCount())
                .createdAt(article.getCreatedAt())
                .build();
    }

    public static PostResponseDTO.PostPreViewListDTO articlePreViewListDTO(Page<Article> articleList) {
        List<PostResponseDTO.PostPreViewDTO> articleDTOList = articleList.stream()
                .map(ArticleConverter::articlePreViewDTO).collect(Collectors.toList());

        return PostResponseDTO.PostPreViewListDTO.builder()
                .isLast(articleList.isLast())
                .isFirst(articleList.isFirst())
                .totalPage(articleList.getTotalPages())
                .totalElements(articleList.getTotalElements())
                .listSize(articleDTOList.size())
                .postList(articleDTOList)
                .build();
    }

    public static ArticleResponseDTO.ArticlePreviewDTO toPreviewDTO(
            Article article, String imageUrl) {
        return ArticleResponseDTO.ArticlePreviewDTO.builder()
                .articleId(article.getArticleId())
                .pinCategory(article.getPlace().getPinCategory().name())
                .imageUrl(imageUrl)
                .title(article.getTitle())
                .likes(article.getLikesCount())
                .spam(article.getSpamCount())
                .comments(article.getCommentCount())
                .build();
    }

    public static ArticleResponseDTO.ArticleListDTO toListDTO(
            List<ArticleResponseDTO.ArticlePreviewDTO> previews, Long limit, boolean hasNext) {

        Long nextCursor = previews.isEmpty() ? null : previews.get(previews.size() - 1).getArticleId();

        return ArticleResponseDTO.ArticleListDTO.builder()
                .articles(previews)
                .cursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}

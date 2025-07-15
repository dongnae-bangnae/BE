package DNBN.spring.web.dto;

import DNBN.spring.domain.Article;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDTO {
    private Long articleId;
    private long likesCount;
    private long spamCount;

    public static LikeResponseDTO of(Long articleId, long likesCount, long spamCount) {
        return LikeResponseDTO.builder()
                .articleId(articleId)
                .likesCount(likesCount)
                .spamCount(spamCount)
                .build();
    }
}

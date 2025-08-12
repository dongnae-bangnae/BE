package DNBN.spring.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDTO {
    private Long articleId;
    private long likesCount;

    public static LikeResponseDTO of(Long articleId, long likesCount) {
        return LikeResponseDTO.builder()
                .articleId(articleId)
                .likesCount(likesCount)
                .build();
    }
}

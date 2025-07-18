package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpamResponseDTO {
    private Long articleId;
    private long spamsCount;

    public static SpamResponseDTO of(Long articleId, long spamsCount) {
        return SpamResponseDTO.builder()
                .articleId(articleId)
                .spamsCount(spamsCount)
                .build();
    }
}
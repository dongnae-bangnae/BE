package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpamResponseDTO {
    private Long articleId;
    private long spamsCount;
    private long spamCount;

    public static SpamResponseDTO of(Long articleId, long spamsCount, long spamCount) {
        return SpamResponseDTO.builder()
                .articleId(articleId)
                .spamsCount(spamsCount)
                .spamCount(spamCount)
                .build();
    }
}
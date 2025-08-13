package DNBN.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpamStatusResponseDTO {
    private Long articleId;
    private long spamsCount;
}
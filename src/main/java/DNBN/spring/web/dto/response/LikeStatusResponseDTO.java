package DNBN.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeStatusResponseDTO {
    private Long articleId;
    private long likesCount;
}
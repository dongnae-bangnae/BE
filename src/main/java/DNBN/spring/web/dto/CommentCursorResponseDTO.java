package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CommentCursorResponseDTO {
    private List<CommentResponseDTO> comments;
    private Long nextCursor;
    private boolean hasNext;
}

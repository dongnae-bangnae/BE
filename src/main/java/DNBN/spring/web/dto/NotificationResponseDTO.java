package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NotificationResponseDTO {

    @Getter
    @Builder
    public static class Response {
        private Long notificationId;
        private Long articleId;
        private String articleTitle;
        private Long commentId;
        private String commentContent;
        private String commenterNickname;
    }

    @Getter
    @Builder
    public static class ListDTO {
        private List<Response> notifications;
        private Long cursor;
        private Long limit;
        private boolean hasNext;
    }
}

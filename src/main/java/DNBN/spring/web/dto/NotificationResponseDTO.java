package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NotificationResponseDTO {

    @Getter
    @Builder
    public static class commentResponse {
        private Long notificationId;
        private Long articleId;
        private String articleTitle;
        private Long commentId;
        private String commentContent;
        private String commenterNickname;
    }

    @Getter
    @Builder
    public static class commentListDTO {
        private List<commentResponse> notifications;
        private Long cursor;
        private Long limit;
        private boolean hasNext;
    }

    @Getter
    @Builder
    public static class SpamResponse {
        private Long notificationId;
        private Long articleId;
        private String articleTitle;
        private Long spamCount;
    }

    @Getter
    @Builder
    public static class SpamListDTO {
        private List<SpamResponse> notifications;
        private Long cursor;
        private Long limit;
        private boolean hasNext;
    }
}

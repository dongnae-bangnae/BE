package DNBN.spring.converter;

import DNBN.spring.domain.Notification;
import DNBN.spring.web.dto.NotificationResponseDTO;

public class NotificationConverter {
    public static NotificationResponseDTO.Response toResponseDto(Notification notification) {
        return NotificationResponseDTO.Response.builder()
                .notificationId(notification.getId())
                .commentId(notification.getComment().getCommentId())
                .articleId(notification.getComment().getArticle().getArticleId())
                .articleTitle(notification.getComment().getArticle().getTitle())
                .commentContent(notification.getComment().getContent())
                .commenterNickname(notification.getComment().getMember().getNickname())
                .build();
    }
}


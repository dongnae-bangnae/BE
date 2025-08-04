package DNBN.spring.converter;

import DNBN.spring.domain.Notification;
import DNBN.spring.web.dto.NotificationResponseDTO;

public class NotificationConverter {
    public static NotificationResponseDTO.commentResponse toCommentDto(Notification notification) {
        return NotificationResponseDTO.commentResponse.builder()
                .notificationId(notification.getId())
                .commentId(notification.getComment().getCommentId())
                .articleId(notification.getComment().getArticle().getArticleId())
                .articleTitle(notification.getComment().getArticle().getTitle())
                .commentContent(notification.getComment().getContent())
                .commenterNickname(notification.getComment().getMember().getNickname())
                .build();
    }

    public static NotificationResponseDTO.SpamResponse toSpamDto(Notification n) {
        return NotificationResponseDTO.SpamResponse.builder()
                .notificationId(n.getId())
                .articleId      (n.getArticle().getArticleId())
                .articleTitle   (n.getArticle().getTitle())
                .spamCount      (n.getArticle().getSpamCount())
                .build();
    }
}


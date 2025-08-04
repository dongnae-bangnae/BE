package DNBN.spring.service.NotificationService;

import DNBN.spring.web.dto.NotificationResponseDTO;

public interface NotificationQueryService {
    NotificationResponseDTO.commentListDTO getCommentNotifications(Long memberId, Long cursor, Long limit);
    NotificationResponseDTO.SpamListDTO getSpamNotifications(Long memberId, Long cursor, Long limit);
}

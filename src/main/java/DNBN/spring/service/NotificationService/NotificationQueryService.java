package DNBN.spring.service.NotificationService;

import DNBN.spring.web.dto.NotificationResponseDTO;

public interface NotificationQueryService {
    NotificationResponseDTO.ListDTO getNotifications(Long memberId, Long cursor, Long limit);
}

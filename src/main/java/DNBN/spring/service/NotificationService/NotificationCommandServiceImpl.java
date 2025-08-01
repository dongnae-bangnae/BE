package DNBN.spring.service.NotificationService;

import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.domain.Notification;
import DNBN.spring.repository.NotificationRepository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository repo;

    @Override
    public void hideNotification(Long memberId, Long notificationId) {
        Notification n = repo.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if (!n.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED);
        }

        if (n.isHidden()) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_ALREADY_HIDDEN);
        }

        n.setHidden(true);
    }
}

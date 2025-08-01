package DNBN.spring.repository.NotificationRepository;

import DNBN.spring.domain.Notification;

import java.util.List;

public interface NotificationRepositoryCustom {
    List<Notification> findByMemberAndHiddenFalseWithCursor(Long memberId, Long cursor, Long limit);
}

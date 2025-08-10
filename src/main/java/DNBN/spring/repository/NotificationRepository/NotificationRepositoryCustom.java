package DNBN.spring.repository.NotificationRepository;

import DNBN.spring.domain.Notification;

import java.util.List;

public interface NotificationRepositoryCustom {
    List<Notification> findCommentByMemberAndHiddenFalseWithCursor(Long memberId, Long cursor, Long limit);
    List<Notification> findSpamByMemberAndHiddenFalseWithCursor(Long memberId, Long cursor, Long limit);
}

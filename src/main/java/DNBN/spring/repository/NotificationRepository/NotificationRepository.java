package DNBN.spring.repository.NotificationRepository;

import DNBN.spring.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    List<Notification> findByComment_CommentId(Long commentId);
}

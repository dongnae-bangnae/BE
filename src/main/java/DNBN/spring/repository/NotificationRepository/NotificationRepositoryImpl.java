package DNBN.spring.repository.NotificationRepository;

import DNBN.spring.domain.Notification;
import DNBN.spring.domain.QNotification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {
    private final JPAQueryFactory query;
    private final QNotification notif = QNotification.notification;

    @Override
    public List<Notification> findByMemberAndHiddenFalseWithCursor(Long memberId, Long cursor, Long limit) {
        BooleanBuilder b = new BooleanBuilder()
                .and(notif.member.id.eq(memberId))
                .and(notif.hidden.isFalse());
        if (cursor != null) {
            b.and(notif.id.lt(cursor));
        }
        return query.selectFrom(notif)
                .where(b)
                .orderBy(notif.id.desc())
                .limit(limit + 1)
                .fetch();
    }
}

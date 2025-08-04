package DNBN.spring.service.NotificationService;

import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.converter.NotificationConverter;
import DNBN.spring.domain.Notification;
import DNBN.spring.repository.NotificationRepository.NotificationRepository;
import DNBN.spring.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository repo;

    @Override
    public NotificationResponseDTO.commentListDTO getCommentNotifications(Long memberId, Long cursor, Long limit) {
        // 1) Custom Repo 로 페치 (limit+1)
        List<Notification> list = repo.findCommentByMemberAndHiddenFalseWithCursor(memberId, cursor, limit);

        // 2) hasNext 판별 & 마지막 한 건 제거
        boolean hasNext = list.size() > limit;
        if (hasNext) list.remove(list.size() - 1);

        // 3) DTO 변환
        List<NotificationResponseDTO.commentResponse> dtos = list.stream()
                .map(NotificationConverter::toCommentDto)
                .toList();

        // 4) nextCursor 계산
        Long nextCursor = dtos.isEmpty() ? null : dtos.get(dtos.size() - 1).getNotificationId();

        // 5) ListDTO 빌드
        return NotificationResponseDTO.commentListDTO.builder()
                .notifications(dtos)
                .cursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public NotificationResponseDTO.SpamListDTO getSpamNotifications(Long memberId, Long cursor, Long limit) {
        List<Notification> list = repo.findSpamByMemberAndHiddenFalseWithCursor(memberId, cursor, limit);
        boolean hasNext = list.size() > limit;
        if (hasNext) list.remove(list.size() - 1);

        var dtos = list.stream()
                .map(NotificationConverter::toSpamDto)
                .toList();

        Long nextCursor = dtos.isEmpty() ? null : dtos.get(dtos.size()-1).getNotificationId();

        return NotificationResponseDTO.SpamListDTO.builder()
                .notifications(dtos)
                .cursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}

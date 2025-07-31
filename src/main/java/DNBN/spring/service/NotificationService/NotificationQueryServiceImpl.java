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
    public NotificationResponseDTO.ListDTO getNotifications(Long memberId, Long cursor, Long limit) {
        // 1) Custom Repo 로 페치 (limit+1)
        List<Notification> list = repo
                .findByMemberAndHiddenFalseWithCursor(memberId, cursor, limit);

        // 2) hasNext 판별 & 마지막 한 건 제거
        boolean hasNext = list.size() > limit;
        if (hasNext) {
            list.remove(list.size() - 1);
        }

        // 3) DTO 변환
        List<NotificationResponseDTO.Response> dtos = list.stream()
                .map(NotificationConverter::toResponseDto)
                .toList();

        // 4) nextCursor 계산
        Long nextCursor = dtos.isEmpty()
                ? null
                : dtos.get(dtos.size() - 1).getNotificationId();

        // 5) ListDTO 빌드
        return NotificationResponseDTO.ListDTO.builder()
                .notifications(dtos)
                .cursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}

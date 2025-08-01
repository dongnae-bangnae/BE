package DNBN.spring.scheduler;

import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.CurationService.CurationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurationScheduler {

    private final CurationCommandService curationCommandService;

    // 매주 월요일 오전 9시 실행
    @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
    public void generateWeeklyCurations() {
        log.info("📆 [큐레이션 자동 생성] 시작");

        try {
            curationCommandService.generateCurations();
            log.info("✅ 큐레이션 생성 성공");
        } catch (Exception e) {
            log.warn("❌ 큐레이션 생성 실패: {}", e.getMessage());
        }

        log.info("✅ [큐레이션 자동 생성] 완료");
    }
}

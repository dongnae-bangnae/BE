package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.CurationService.CurationCommandService;
import DNBN.spring.web.dto.response.CurationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/curation")
public class CurationController {
    private final CurationCommandService curationCommandService;
    private final MemberRepository memberRepository;

    @PostMapping("/generate")
    @Operation(
            summary = "오늘의 큐레이션 생성 API",
            description = "관심 지역 기반으로 큐레이션을 생성합니다."
    )
    public ApiResponse<CurationResponseDTO> generateCuration(@AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails == null) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
        }

        Member member = memberDetails.getMember(); // 또는 memberDetails.getId() 후 memberRepository 조회
        CurationResponseDTO response = curationCommandService.generateCuration(member);
        return ApiResponse.onSuccess(response);
    }
}

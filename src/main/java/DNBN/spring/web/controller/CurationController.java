package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.service.CurationService.CurationCommandService;
import DNBN.spring.service.CurationService.CurationQueryService;
import DNBN.spring.web.dto.response.CurationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/curations")
public class CurationController {
    private final CurationCommandService curationCommandService;
    private final CurationQueryService curationQueryService;

    @PostMapping("/generate")
    @Operation(
            summary = "이번주의 큐레이션 생성 API",
            description = "관심 지역 기반으로 큐레이션을 자동 생성합니다. 매주 월요일 아침 9시에 자동 갱신됩니다."
    )
    public ApiResponse<List<CurationResponseDTO>> generateCuration() {
        List<CurationResponseDTO> response = curationCommandService.generateCurations();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("")
    @Operation(
            summary = "큐레이션 리스트 조회 API",
            description = "큐레이션 전체 리스트를 보여줍니다. - JWT 인증 필수"
    )
    public ApiResponse<List<CurationResponseDTO>> getCurations(@AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        Member member = memberDetails.getMember();
        List<CurationResponseDTO> response = curationQueryService.getCurationsByMember(member.getId());

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("")
    @Operation(
            summary = "큐레이션 리스트 조회 API",
            description = "큐레이션 전체 리스트를 보여줍니다. - JWT 인증 필수"
    )
    public ApiResponse<List<CurationResponseDTO>> getCurations(@AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        Member member = memberDetails.getMember();
        List<CurationResponseDTO> response = curationQueryService.getCurationsByMember(member.getId());
        return ApiResponse.onSuccess(response);
    }
}

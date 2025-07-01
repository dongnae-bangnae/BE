package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.service.MemberService.MemberQueryService;
import DNBN.spring.web.dto.MemberRequestDTO;
import DNBN.spring.web.dto.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberRestController {

    private final MemberQueryService memberQueryService;

    @PostMapping("/onboarding")
    @Operation(summary = "유저 온보딩 API",description = "소셜 로그인 이후, 닉네임/프로필/선호 지역을 등록하는 온보딩 API입니다.")
    public ApiResponse<MemberResponseDTO.OnboardingResultDTO> onboard(@RequestBody @Valid MemberRequestDTO.OnboardingDTO request) {
        return ApiResponse.onSuccess(memberCommandService.loginMember(request));
    }

    @GetMapping("/info")
    @Operation(summary = "유저 내 정보 조회 API - 인증 필요",
            description = "유저가 내 정보를 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") } // ‘내 정보 조회’는 로그인한 사용자만이 접근할 수 있는 API여야 함 --> Swagger 어노테이션인 @Operation 어노테이션에 security 필드를 추가해서 token이 요청 필수값임을 명시
    )
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> getMyInfo(HttpServletRequest request) {
        return ApiResponse.onSuccess(memberQueryService.getMemberInfo(request));
    }
}

package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.converter.MemberConverter;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.MemberService.MemberCommandService;
import DNBN.spring.service.MemberService.MemberQueryService;
import DNBN.spring.web.dto.MemberRequestDTO;
import DNBN.spring.web.dto.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberRestController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

//    @PostMapping("/onboarding")
//    @Operation(
//            summary = "회원 초기 정보 등록 (온보딩) API - JWT 인증 필요",
//            description = "JWT 인증된 멤버가 닉네임, 프로필 이미지, 선호 지역을 등록하는 API입니다.",
//            security = @SecurityRequirement(name = "JWT TOKEN")
//    )
//    public ApiResponse<MemberResponseDTO.OnboardingResultDTO> onboard(@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody @Valid MemberRequestDTO.OnboardingDTO request) {
//        Long memberId = memberDetails.getMember().getId();
//        return ApiResponse.onSuccess(memberCommandService.onboardingMember(memberId, request));
//    }
    @PostMapping(
            value = "/onboarding",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Operation(
            summary = "회원 초기 정보 등록 (온보딩) API - JWT 인증 필요",
            description = "JWT 인증된 멤버가 닉네임, 프로필 이미지, 선호 지역을 등록하는 API입니다.",
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ApiResponse<MemberResponseDTO.OnboardingResultDTO> onboard(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestPart("request") @Valid MemberRequestDTO.OnboardingDTO request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Long memberId = memberDetails.getMember().getId();
        Member member = memberCommandService.onboardingMember(memberId, request, profileImage);
        return ApiResponse.onSuccess(MemberConverter.toOnboardingResponseDTO(member));
    }

    @GetMapping("/info")
    @Operation(summary = "내 정보 조회 API - JWT 인증 필요",
            description = "JWT 인증된 멤버가 자신의 정보를 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") } // ‘내 정보 조회’는 로그인한 사용자만이 접근할 수 있는 API여야 함 --> Swagger 어노테이션인 @Operation 어노테이션에 security 필드를 추가해서 token이 요청 필수값임을 명시
    )
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> getMyInfo(HttpServletRequest request) {
        return ApiResponse.onSuccess(memberQueryService.getMemberInfo(request));
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴 API - JWT 인증 필요",
            description = "JWT 인증된 멤버가 자신의 계정을 탈퇴(삭제)하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ApiResponse<Void> deleteMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.deleteMember(memberDetails.getMember().getMemberId());
        return ApiResponse.onSuccess(null);
    }

}

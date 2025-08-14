package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.converter.MemberConverter;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.MemberService.MemberCommandService;
import DNBN.spring.service.MemberService.MemberQueryService;
import DNBN.spring.web.dto.request.MemberRequestDTO;
import DNBN.spring.web.dto.response.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import DNBN.spring.apiPayload.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member")
public class MemberRestController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @PostMapping(value = "/onboarding")
    @Operation(
            summary = "회원 초기 정보 등록 (온보딩) API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 멤버가 닉네임, 선호 지역을 등록하는 API입니다.",
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.OnboardingResultDTO>> onboard(@AuthenticationPrincipal MemberDetails memberDetails) {
        Long memberId = memberDetails.getMember().getId();
        Member member = memberCommandService.onboardingMember(memberId);
        return ResponseEntity.status(SuccessStatus.MEMBER_ONBOARDING_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MEMBER_ONBOARDING_SUCCESS, MemberConverter.toOnboardingResponseDTO(member)));
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회 API - JWT AccessToken 인증 필요",
            description = "JWT 인증된 멤버가 자신의 정보를 조회하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberInfoDTO>> getMyInfo(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        MemberResponseDTO.MemberInfoDTO info = memberQueryService.getMemberInfo(memberId);
        return ResponseEntity.status(SuccessStatus.MEMBER_INFO_READ_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MEMBER_INFO_READ_SUCCESS, info));
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴 API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 멤버가 자신의 계정을 탈퇴(삭제)하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ResponseEntity<ApiResponse<Void>> deleteMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.deleteMember(memberDetails.getMember().getId());
        return ResponseEntity.status(SuccessStatus.MEMBER_DELETE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MEMBER_DELETE_SUCCESS, null));
    }

    @PatchMapping("/nickname")
    @Operation(summary = "회원 닉네임 등록 및 변경 API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 멤버가 자신의 닉네임을 등록 및 수정하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.NicknameUpdateResultDTO>> updateNickname(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody @Valid MemberRequestDTO.NicknameUpdateDTO nicknameUpdate
    ) {
        Long memberId = memberDetails.getMember().getId();
        MemberResponseDTO.NicknameUpdateResultDTO response = memberCommandService.updateMemberNickname(memberId, nicknameUpdate.getNickname());
        return ResponseEntity.status(SuccessStatus.MEMBER_NICKNAME_UPDATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MEMBER_NICKNAME_UPDATE_SUCCESS, response));
    }

    @PatchMapping("/regions")
    @Operation(
            summary = "관심 동네 등록 및 변경 API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 멤버가 자신의 관심 동네를 등록 및 수정하는 API입니다.",
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.ChosenRegionsDTO>> updateRegions(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody @Valid MemberRequestDTO.RegionUpdateDTO request) {

        Long memberId = memberDetails.getMember().getId();
        MemberResponseDTO.ChosenRegionsDTO response = memberCommandService.updateRegions(memberId, request.getRegionIds());
        return ResponseEntity.status(SuccessStatus.MEMBER_REGION_UPDATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MEMBER_REGION_UPDATE_SUCCESS, response));
    }

    @PatchMapping(
            value = "/profile-image",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Operation(
            summary = "프로필 이미지 등록 및 변경 API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 사용자가 프로필 이미지를 등록 및 수정하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.ProfileImageUpdateResultDTO>> updateProfileImage(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Long memberId = memberDetails.getMember().getId();
        MemberResponseDTO.ProfileImageUpdateResultDTO result = memberCommandService.updateProfileImage(memberId, profileImage);
        return ResponseEntity.status(SuccessStatus.MEMBER_PROFILE_IMAGE_UPDATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MEMBER_PROFILE_IMAGE_UPDATE_SUCCESS, result));
    }

}

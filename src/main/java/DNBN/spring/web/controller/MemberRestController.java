package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member")
public class MemberRestController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @PostMapping(
            value = "/onboarding",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Operation(
            summary = "회원 초기 정보 등록 (온보딩) API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 멤버가 닉네임, 프로필 이미지, 선호 지역을 등록하는 API입니다.",
            security = @SecurityRequirement(name = "JWT TOKEN")
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.OnboardingResultDTO>> onboard(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestPart("request") @Valid MemberRequestDTO.OnboardingDTO request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Long memberId = memberDetails.getMember().getId();
        Member member = memberCommandService.onboardingMember(memberId, request, profileImage);
        return ResponseEntity.status(SuccessStatus.MEMBER_ONBOARDING_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.MEMBER_ONBOARDING_SUCCESS, MemberConverter.toOnboardingResponseDTO(member)));
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
        return ResponseEntity.status(SuccessStatus.MEMBER_INFO_RETRIEVED.getHttpStatus()).body(ApiResponse.of(SuccessStatus.MEMBER_INFO_RETRIEVED, info));
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴 API - JWT AccessToken + CSRF 토큰 인증 필요",
            description = "JWT 인증된 멤버가 자신의 계정을 탈퇴(삭제)하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ResponseEntity<ApiResponse<Void>> deleteMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.deleteMember(memberDetails.getMember().getId());
        return ResponseEntity.status(SuccessStatus.MEMBER_DELETE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.MEMBER_DELETE_SUCCESS, null));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody @Valid MemberRequestDTO.NicknameUpdateDTO nicknameUpdate
    ) {
        Long memberId = memberDetails.getMember().getId();
        memberCommandService.changeMemberNickname(memberId, nicknameUpdate.getNickname());
        return ResponseEntity.status(SuccessStatus.MEMBER_NICKNAME_UPDATE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.MEMBER_NICKNAME_UPDATE_SUCCESS, null));
    }

    @PatchMapping("/regions")
    public ResponseEntity<ApiResponse<MemberResponseDTO.ChosenRegionsDTO>> updateRegions(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody @Valid MemberRequestDTO.RegionUpdateDTO request) {
        Long memberId = memberDetails.getMember().getId();
        MemberResponseDTO.ChosenRegionsDTO response = memberCommandService.updateRegions(memberId, request.getRegionIds());
        return ResponseEntity.status(SuccessStatus.MEMBER_REGION_UPDATE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.MEMBER_REGION_UPDATE_SUCCESS, response));
    }

    @PatchMapping(
            value = "/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<MemberResponseDTO.ProfileImageUpdateResultDTO>> updateProfileImage(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestPart("profileImage") MultipartFile profileImage
    ) {
        Long memberId = memberDetails.getMember().getId();
        MemberResponseDTO.ProfileImageUpdateResultDTO result = memberCommandService.updateProfileImage(memberId, profileImage);
        return ResponseEntity.status(SuccessStatus.MEMBER_PROFILE_IMAGE_UPDATE_SUCCESS.getHttpStatus()).body(ApiResponse.of(SuccessStatus.MEMBER_PROFILE_IMAGE_UPDATE_SUCCESS, result));
    }

}

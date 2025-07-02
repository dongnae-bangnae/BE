package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.MemberService.MemberCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRestController {

    private final MemberCommandService memberCommandService;

    @PostMapping("/logout")
    @Operation(summary = "유저 로그아웃 API",
            description = "멤버가 로그아웃하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ApiResponse<Void> logout(@AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.logout(memberDetails.getMember().getId());
        return ApiResponse.onSuccess(null);
    }
}

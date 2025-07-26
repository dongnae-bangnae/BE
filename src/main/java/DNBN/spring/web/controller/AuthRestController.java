package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.MemberDetails;
import DNBN.spring.service.AuthService.AuthCommandService;
import DNBN.spring.service.MemberService.MemberCommandService;
import DNBN.spring.web.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthRestController {

    private final MemberCommandService memberCommandService;
    private final AuthCommandService authCommandService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/logout")
    @Operation(summary = "회원 로그아웃 API - JWT 인증 필요",
            description = "JWT 인증된 멤버가 로그아웃하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ApiResponse<Void> logout(@AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.logout(memberDetails.getMember().getId());
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급 API",
            description = "RefreshToken으로 AccessToken을 재발급받는 API입니다."
    )
    public ApiResponse<AuthResponseDTO.ReissueTokenResponseDTO> reissueAccessToken(HttpServletRequest request) {
        String refreshToken = JwtTokenProvider.resolveToken(request); // Authorization 헤더에서 Bearer 토큰을 추출
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) { // refreshToken == null은 !StringUtils.hasText(refreshToken)로 체크 가능
            throw new MemberHandler(ErrorStatus.INVALID_JWT_REFRESH_TOKEN); // TOKEN4002
        }

        AuthResponseDTO.ReissueTokenResponseDTO response = authCommandService.reissue(refreshToken);
        return ApiResponse.onSuccess(response);
    }
}

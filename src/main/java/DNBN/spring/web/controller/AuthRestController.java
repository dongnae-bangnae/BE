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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
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
    @Operation(summary = "회원 로그아웃 API - JWT AccessToken 인증 필요, CSRF 인증은 요구되지 않습니다.",
            description = "JWT 인증된 멤버가 로그아웃하는 API입니다.",
            security = { @SecurityRequirement(name = "JWT TOKEN") }
    )
    public ApiResponse<Void> logout(@AuthenticationPrincipal MemberDetails memberDetails, HttpServletResponse response) {
        memberCommandService.logout(response, memberDetails.getMember().getId());
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급 API - JWT RefreshToken 인증 필요, CSRF 인증은 요구되지 않습니다.",
            description = "RefreshToken으로 AccessToken과 CSRF 토큰을 재발급받는 API입니다. AccessToken은 HttpOnly 쿠키로, CSRF 토큰은 일반 쿠키로 내려줍니다."
    )
//    public ApiResponse<AuthResponseDTO.ReissueTokenResponseDTO> reissueAccessToken(HttpServletRequest request) {
//        String refreshToken = JwtTokenProvider.resolveToken(request); // Authorization 헤더에서 Bearer 토큰을 추출
    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
//    public ApiResponse<AuthResponseDTO.ReissueTokenResponseDTO> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

            String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) { // refreshToken == null은 !StringUtils.hasText(refreshToken)로 체크 가능
            throw new MemberHandler(ErrorStatus.INVALID_JWT_REFRESH_TOKEN); // TOKEN4002
        }

        AuthResponseDTO.ReissueTokenResponseDTO tokens = authCommandService.reissue(refreshToken);

        // 어세스 토큰 재발급
        addCookie(response, "accessToken", tokens.getAccessToken(), true, 60 * 60 * 4); // 4시간

        // csrf 토큰 재발급
        CsrfToken csrfToken = authCommandService.generateCsrfToken(request, response);
        ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
                .httpOnly(false)    // JS에서 읽을 수 있어야 함
                .secure(true)
                .path("/")
                .domain("dnbn.site") // 테스트 시 주석처리
                .maxAge(60 * 60 * 4) // 4시간
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", csrfCookie.toString());

        // 응답 바디 없이 204 No Content
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);

//        return ApiResponse.onSuccess(tokens); // 테스트용

//        return ApiResponse.onSuccess(response);
    }

    private void addCookie(HttpServletResponse response, String name, String value, boolean httpOnly, int maxAgeInSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(true)
                .path("/")
                .domain("dnbn.site")
                .maxAge(maxAgeInSeconds)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}

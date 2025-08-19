package DNBN.spring.service.OAuth2;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.response.AuthResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
//    private final CookieCsrfTokenRepository csrfTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("✅ OAuth2 로그인 성공 핸들러 진입");
        log.info("🔐 authentication.getPrincipal() 타입: {}", authentication.getPrincipal().getClass().getName());

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Member member = oAuth2User.getMember();
        log.info("🙋‍♂️ 로그인한 유저 ID: {}, 온보딩 여부: {}", member.getId(), member.isOnboardingCompleted());

        // JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(authentication); // kakao_12345
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getSocialId());

        log.info("🔑 AccessToken: {}, RefreshToken: {}", accessToken, refreshToken);

        /*
        // 리다이렉트 + 쿼리파라미터 방식: 프론트엔드가 토큰 읽을 수 있도록 전달 -> url에 토큰 노출
        String redirectUri = member.isOnboardingCompleted()
                ? "https://dnbn.com/home"
                : "https://dnbn.com/onboarding";

        redirectUri += "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUri);
        */

        /*
        // JSON 응답 방식 (SPA 등 API 호출용)
        AuthResponseDTO.LoginResultDTO result = AuthResponseDTO.LoginResultDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .isOnboardingCompleted(member.isOnboardingCompleted())
                .build();

        ApiResponse<AuthResponseDTO.LoginResultDTO> apiResponse;
        if (member.isOnboardingCompleted()) {
            apiResponse = ApiResponse.of(SuccessStatus.MEMBER_ALREADY_LOGIN, result);
        } else {
            apiResponse = ApiResponse.of(SuccessStatus.MEMBER_NEEDS_ONBOARDING, result);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        */

        // 쿠키로 프론트에게 내려주기
        boolean isOnboardingCompleted = member.isOnboardingCompleted();
        SuccessStatus status = isOnboardingCompleted
                ? SuccessStatus.MEMBER_ALREADY_ONBOARDING_COMPLETED
                : SuccessStatus.MEMBER_NEEDS_ONBOARDING;

        // 1. 민감 정보: HttpOnly + Secure 쿠키
        addCookie(response, "accessToken", accessToken, true, 60 * 60 * 4); // 4시간
        addCookie(response, "refreshToken", refreshToken, true, 60 * 60 * 24 * 7); // 7일
        addCookie(response, "memberId", String.valueOf(member.getId()), true, 60 * 60 * 4);
        addCookie(response, "isOnboardingCompleted", String.valueOf(isOnboardingCompleted), false, 60 * 60 * 4);

        // 2. 상태 정보: HttpOnly = false (JS에서 읽게)
        addCookie(response, "isSuccess", "true", false, 60);
        addCookie(response, "code", status.getCode(), false, 60);
        addCookie(response, "message", URLEncoder.encode(status.getMessage(), StandardCharsets.UTF_8), false, 60);

        /*
        // 3. CSRF 토큰 발급 + 쿠키로 내려주기
        CsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, response);
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);

        // 프론트에서 JS로 읽을 수 있게 HttpOnly = false
        ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
                .httpOnly(false)
                .secure(true) // 테스트 시 false로
                .path("/")
                .domain("dnbn.site") // 로컬 테스트 시 주석 처리해야 함
                .maxAge(60 * 60 * 4)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", csrfCookie.toString());

        addCookie(response, "XSRF-TOKEN", csrfToken.getToken(), false, 60 * 60 * 4);
        */

        clearJsessionCookie(response);

        // 4. 리다이렉트 (브릿지 페이지)
        response.sendRedirect("https://www.dnbn.site/oauth-redirect");
    }

    private void addCookie(HttpServletResponse response, String name, String value, boolean httpOnly, int maxAgeInSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(true) // 운영환경에서는 true (HTTPS)
                .path("/")
                .domain("dnbn.site")
                .maxAge(maxAgeInSeconds)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearJsessionCookie(HttpServletResponse response) {
        ResponseCookie deleteJsessionCookie = ResponseCookie.from("JSESSIONID", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 쿠키 즉시 만료
                .build();
        response.addHeader("Set-Cookie", deleteJsessionCookie.toString());
        log.info("JSESSIONID 쿠키 삭제 완료");
    }
}
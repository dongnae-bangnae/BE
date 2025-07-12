package DNBN.spring.service.OAuth2;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.status.SuccessStatus;
import DNBN.spring.config.security.jwt.JwtTokenProvider;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.AuthResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("✅ OAuth2 로그인 성공 핸들러 진입");
        log.info("🔐 authentication.getPrincipal() 타입: {}", authentication.getPrincipal().getClass().getName());

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//        Object principal = authentication.getPrincipal();

        // ✅ CustomOAuth2User 타입이 아니면 예외 던지기
//        if (!(principal instanceof CustomOAuth2User)) {
//            throw new OAuth2AuthenticationException(
//                    "Expected CustomOAuth2User but got " + principal.getClass().getName()
//            );
//        }
//
//        CustomOAuth2User oAuth2User = (CustomOAuth2User) principal; // 이제 안전하게 다운캐스팅

        Member member = oAuth2User.getMember();
        log.info("🙋‍♂️ 로그인한 유저 ID: {}, 온보딩 여부: {}", member.getId(), member.isOnboardingCompleted());

        // JWT 발급
//        String accessToken = jwtTokenProvider.generateAccessToken(member.getSocialId()); // kakao_12345
        String accessToken = jwtTokenProvider.generateAccessToken(authentication); // subject = socialId
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getSocialId());

        AuthResponseDTO.LoginResultDTO result = AuthResponseDTO.LoginResultDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .isOnboardingCompleted(member.isOnboardingCompleted())
                .build();

//        String redirectUri = member.isOnboardingCompleted()
//                ? "https://dnbn.com/home"
//                : "https://dnbn.com/onboarding";

        ApiResponse<AuthResponseDTO.LoginResultDTO> apiResponse;
        if (member.isOnboardingCompleted()) {
            apiResponse = ApiResponse.of(SuccessStatus.MEMBER_ALREADY_LOGIN, result);
        } else {
            apiResponse = ApiResponse.of(SuccessStatus.MEMBER_NEEDS_ONBOARDING, result);
        }

        // JSON 응답 방식 (SPA 등 API 호출용)
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        // 리다이렉트 + 쿼리파라미터 방식: 프론트엔드가 토큰 읽을 수 있도록 전달 -> url에 토큰 노출
//        redirectUri += "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
//                + "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
//
//        response.sendRedirect(redirectUri);
    }
}

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
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("✅ OAuth2 로그인 성공!");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Member member = oAuth2User.getMember();

        // JWT 발급
//        String accessToken = jwtTokenProvider.generateAccessToken(member.getSocialId()); // kakao_12345
        String accessToken = jwtTokenProvider.generateAccessToken(authentication); // subject = socialId
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getSocialId());

        AuthResponseDTO.LoginResultDTO result = AuthResponseDTO.LoginResultDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .isOnboardingCompleted(member.isOnboardingCompleted())
                .build();

//        String redirectUri = member.isOnboardingCompleted()
//                ? "https://dnbn.com/home"
//                : "https://dnbn.com/onboarding";

        ApiResponse<AuthResponseDTO.LoginResultDTO> apiResponse;
        if (member.isOnboardingCompleted()) {
            apiResponse = ApiResponse.of(SuccessStatus.MEMBER_LOGIN_SUCCESS, result);
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

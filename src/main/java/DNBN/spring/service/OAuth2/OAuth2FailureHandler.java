package DNBN.spring.service.OAuth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("❌ OAuth2 로그인 실패: {}", exception.getMessage());

        // 1. 리다이렉트 방식 (쿼리 파라미터에 에러 메시지 포함)
//        String targetUrl = UriComponentsBuilder.fromUriString("https://dnbn.com/login-fail")
//                .queryParam("error", exception.getMessage())
//                .build().toUriString();
//
//        response.sendRedirect(targetUrl);

        // 2. JSON 응답 방식
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"success\": false, \"message\": \"" + exception.getMessage() + "\"}");
    }
}

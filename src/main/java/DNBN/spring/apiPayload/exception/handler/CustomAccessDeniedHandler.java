package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.ErrorReasonDTO;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler { // 인증은 되었으나 권한이 없을 때의 403 에러를 반환
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorReasonDTO reason;

        if (accessDeniedException instanceof InvalidCsrfTokenException || accessDeniedException instanceof MissingCsrfTokenException) {
            reason = ErrorReasonDTO.builder()
                    .httpStatus(ErrorStatus.INVALID_CSRF_TOKEN.getHttpStatus())
                    .isSuccess(false)
                    .code(ErrorStatus.INVALID_CSRF_TOKEN.getCode())
                    .message(ErrorStatus.INVALID_CSRF_TOKEN.getMessage())
                    .build();
        } else {
            reason = ErrorReasonDTO.builder()
                    .httpStatus(ErrorStatus.ACCESS_DENIED.getHttpStatus())
                    .isSuccess(false)
                    .code(ErrorStatus.ACCESS_DENIED.getCode())
                    .message(ErrorStatus.ACCESS_DENIED.getMessage())
                    .build();
        }

        response.getWriter().write(new ObjectMapper().writeValueAsString(
                ApiResponse.onFailure(reason.getCode(), reason.getMessage(), reason)
        ));
    }
}

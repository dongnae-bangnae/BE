package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.apiPayload.code.ErrorReasonDTO;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint { // 인증 실패 시 401 에러를 반환
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorReasonDTO reason = ErrorReasonDTO.builder()
                .httpStatus(ErrorStatus.INVALID_JWT_ACCESS_TOKEN.getHttpStatus())
                .isSuccess(false)
                .code(ErrorStatus.INVALID_JWT_ACCESS_TOKEN.getCode())
                .message(ErrorStatus.INVALID_JWT_ACCESS_TOKEN.getMessage())
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(
                ApiResponse.onFailure(reason.getCode(), reason.getMessage(), reason)
        ));
    }
}

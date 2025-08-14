package DNBN.spring.service.AuthService;

import DNBN.spring.web.dto.AuthResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

public interface AuthCommandService {
    AuthResponseDTO.ReissueTokenResponseDTO reissue(String refreshToken);
    CsrfToken generateCsrfToken(HttpServletRequest request, HttpServletResponse response);
}

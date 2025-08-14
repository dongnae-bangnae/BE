package DNBN.spring.service.AuthService;

import DNBN.spring.web.dto.response.AuthResponseDTO;

public interface AuthCommandService {
    AuthResponseDTO.ReissueTokenResponseDTO reissue(String refreshToken);
}

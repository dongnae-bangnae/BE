package DNBN.spring.service.AuthService;

import DNBN.spring.web.dto.AuthResponseDTO;

public interface AuthCommandService {
    AuthResponseDTO.ReissueTokenResponseDTO reissue(String refreshToken);
}

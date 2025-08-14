package DNBN.spring.converter;

import DNBN.spring.web.dto.AuthResponseDTO;

public class AuthConverter {
    public static AuthResponseDTO.ReissueTokenResponseDTO toReissueTokenResponseDTO(String accessToken) {
        return AuthResponseDTO.ReissueTokenResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}

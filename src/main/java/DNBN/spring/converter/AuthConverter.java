package DNBN.spring.converter;

import DNBN.spring.web.dto.response.AuthResponseDTO;

public class AuthConverter {
    public static AuthResponseDTO.ReissueTokenResponseDTO toReissueTokenResponseDTO(String accessToken) {
        return AuthResponseDTO.ReissueTokenResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}

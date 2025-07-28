package DNBN.spring.converter;

import DNBN.spring.domain.Challenge;
import DNBN.spring.web.dto.response.ChallengeResponseDTO;

public class ChallengeConverter {
    public static ChallengeResponseDTO.ChallengeDetailDTO challengeDetailDTO(Challenge challenge) {
        return new ChallengeResponseDTO.ChallengeDetailDTO(
                challenge.getChallengeId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getStartDate(),
                challenge.getEndDate()
        );
    }
}

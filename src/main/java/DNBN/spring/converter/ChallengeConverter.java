package DNBN.spring.converter;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.Challenge;
import DNBN.spring.web.dto.response.ChallengeResponseDTO;
import DNBN.spring.web.dto.response.PostResponseDTO;

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

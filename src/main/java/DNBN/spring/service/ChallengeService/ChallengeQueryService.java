package DNBN.spring.service.ChallengeService;

import DNBN.spring.web.dto.response.ChallengeResponseDTO;

public interface ChallengeQueryService {
    ChallengeResponseDTO.ChallengeDetailDTO getChallengeDetail(Long challengeId);
}

package DNBN.spring.service.ChallengeService;

import DNBN.spring.converter.ChallengeConverter;
import DNBN.spring.domain.Challenge;
import DNBN.spring.repository.ChallengeRepository.ChallengeRepository;
import DNBN.spring.web.dto.response.ChallengeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeQueryServiceImpl implements ChallengeQueryService {
    private final ChallengeRepository challengeRepository;

    @Override
    public ChallengeResponseDTO.ChallengeDetailDTO getChallengeDetail(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));
        return ChallengeConverter.challengeDetailDTO(challenge);
    }
}

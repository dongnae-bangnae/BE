package DNBN.spring.repository.ChallengeRepository;

import DNBN.spring.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}

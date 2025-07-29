package DNBN.spring.repository.CurationRepository;

import DNBN.spring.domain.Curation;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface CurationRepository extends JpaRepository<Curation, Long> {
    Optional<Curation> findByMemberAndRegionAndCreatedAtBetween(
            Member member,
            Region region,
            LocalDate startOfWeek,
            LocalDate endOfWeek
    );

}

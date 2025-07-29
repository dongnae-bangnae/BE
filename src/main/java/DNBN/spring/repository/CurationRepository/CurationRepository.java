package DNBN.spring.repository.CurationRepository;

import DNBN.spring.domain.Curation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationRepository extends JpaRepository<Curation, Long> {
}

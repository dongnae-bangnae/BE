package DNBN.spring.repository.CurationPlaceRepository;

import DNBN.spring.domain.Curation;
import DNBN.spring.domain.mapping.CurationPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurationPlaceRepository extends JpaRepository<CurationPlace, Long> {
}

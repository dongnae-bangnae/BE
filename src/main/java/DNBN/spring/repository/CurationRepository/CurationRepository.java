package DNBN.spring.repository.CurationRepository;

import DNBN.spring.domain.Curation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CurationRepository extends JpaRepository<Curation, Long> {
    @Query("SELECT c FROM Curation c LEFT JOIN FETCH c.curationPlaces cp LEFT JOIN FETCH cp.place WHERE c.id = :id")
    Optional<Curation> findByIdWithPlaces(@Param("id") Long id);
}

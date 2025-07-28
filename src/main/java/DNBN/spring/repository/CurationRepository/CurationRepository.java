package DNBN.spring.repository.CurationRepository;

import DNBN.spring.domain.Curation;
import DNBN.spring.domain.Member;

import java.time.LocalDate;
import java.util.Optional;

public interface CurationRepository {
    Optional<Curation> findByMemberAndCreatedDate(Member member, LocalDate date);
}

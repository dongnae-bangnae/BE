package DNBN.spring.repository.MemberRepository;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndProvider(String socialId, Provider provider);
}

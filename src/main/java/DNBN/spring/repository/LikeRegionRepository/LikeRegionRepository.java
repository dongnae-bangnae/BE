package DNBN.spring.repository.LikeRegionRepository;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.mapping.LikeRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRegionRepository extends JpaRepository<LikeRegion, Long> {
    List<LikeRegion> findAllByMember(Member member);
    List<LikeRegion> findAllByRegion(Region region);
}

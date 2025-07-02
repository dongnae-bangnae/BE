package DNBN.spring.repository.LikePlaceRepository;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.mapping.LikePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikePlaceRepository extends JpaRepository<LikePlace, Long> {
    List<LikePlace> findAllByMember(Member member);
    List<LikePlace> findAllByRegion(Region region);
}

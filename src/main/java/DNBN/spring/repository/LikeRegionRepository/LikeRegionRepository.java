package DNBN.spring.repository.LikeRegionRepository;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.mapping.LikeRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRegionRepository extends JpaRepository<LikeRegion, Long> {
    List<LikeRegion> findAllByMember(Member member);
    List<LikeRegion> findAllByRegion(Region region);
    void deleteByMember(Member member);

    @Query("SELECT lr.region.id FROM LikeRegion lr WHERE lr.member.id = :memberId")
    List<Long> findRegionIdsByMemberId(@Param("memberId") Long memberId);

    List<LikeRegion> findByMember(Member member);
}

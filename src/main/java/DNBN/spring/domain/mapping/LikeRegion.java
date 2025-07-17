package DNBN.spring.domain.mapping;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "like_region")
public class LikeRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    public static LikeRegion of(Member member, Region region) {
        return new LikeRegion(member, region);
    }

    private LikeRegion(Member member, Region region) {
        this.member = member;
        this.region = region;
    }
}

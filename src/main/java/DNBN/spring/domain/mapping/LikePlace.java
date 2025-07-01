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
public class LikePlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    public static LikePlace of(Member member, Region region) {
        return new LikePlace(member, region);
    }

    protected LikePlace() {} // JPA 기본 생성자

    private LikePlace(Member member, Region region) {
        this.member = member;
        this.region = region;
    }
}

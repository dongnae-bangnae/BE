package DNBN.spring.domain.mapping;

import DNBN.spring.domain.Curation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationLikeRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Curation curation;

    @ManyToOne(fetch = FetchType.LAZY)
    private LikeRegion likeRegion;
}

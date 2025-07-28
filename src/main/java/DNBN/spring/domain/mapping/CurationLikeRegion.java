package DNBN.spring.domain.mapping;

import DNBN.spring.domain.Curation;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CurationLikeRegion {
    @Id
    @GeneratedValue
    private Long curationLikeRegionId;

    @ManyToOne
    private Curation curation;

    @ManyToOne
    private LikeRegion likeRegion;
}

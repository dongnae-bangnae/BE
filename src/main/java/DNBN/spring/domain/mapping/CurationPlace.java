package DNBN.spring.domain.mapping;

import DNBN.spring.domain.Curation;
import DNBN.spring.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Curation curation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;
}

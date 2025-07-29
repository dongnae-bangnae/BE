package DNBN.spring.domain.mapping;

import DNBN.spring.domain.Curation;
import DNBN.spring.domain.Place;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CurationPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Curation curation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;
}

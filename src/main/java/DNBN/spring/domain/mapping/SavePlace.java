package DNBN.spring.domain.mapping;

import DNBN.spring.domain.Category;
import DNBN.spring.domain.Place;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SavePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public static SavePlace of(Place place, Category category) {
        return new SavePlace(place, category);
    }

    private SavePlace(Place place, Category category) {
        this.place = place;
        this.category = category;
    }
}
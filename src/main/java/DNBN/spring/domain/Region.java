package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import DNBN.spring.domain.mapping.LikePlace;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String province; // ex: 서울

    @Column(length = 20, nullable = false)
    private String city; // ex: 강남구

    @Column(length = 50, nullable = false)
    private String district; // ex: 개포1동

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<LikePlace> likePlaceList = new ArrayList<>();

    public String getFullName() {
        return province + " " + city + " " + district;
    }
}


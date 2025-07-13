package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.domain.mapping.SavePlace;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Place extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long placeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;

  private Long latitude;

  private Long longitude;

  @Column(nullable = false, length = 50)
  private String title;

  @Enumerated(EnumType.STRING)
  private PinCategory pinCategory;

  @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SavePlace> savedPlaces = new ArrayList<>();
}

package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.domain.mapping.SavePlace;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

  @Column(nullable = false, columnDefinition = "DECIMAL(8,5)")
  private Double latitude;

  @Column(nullable = false, columnDefinition = "DECIMAL(8,5)")
  private Double longitude;

  // 저장 전/수정 전마다 소수점 5자리로 반올림
  @PrePersist
  @PreUpdate
  private void normalizeCoordinates() {
    if (latitude != null) {
      latitude = BigDecimal.valueOf(latitude)
              .setScale(5, RoundingMode.HALF_UP)
              .doubleValue();
    }
    if (longitude != null) {
      longitude = BigDecimal.valueOf(longitude)
              .setScale(5, RoundingMode.HALF_UP)
              .doubleValue();
    }
  }

  @Column(nullable = false, length = 50)
  private String title;

  @Enumerated(EnumType.STRING)
  private PinCategory pinCategory;

  @Column(nullable = false, length = 255)
  private String address;

  @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SavePlace> savedPlaces = new ArrayList<>();

  public void updatePinCategory(PinCategory newCategory) {
    this.pinCategory = newCategory;
  }

  public void updateTitle(String newTitle) {
    this.title = newTitle;
  }
}

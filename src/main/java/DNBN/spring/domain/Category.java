package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import DNBN.spring.domain.enums.Color;
import DNBN.spring.domain.mapping.SavePlace;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "name"})
)
public class Category extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long categoryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Color color;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = true)
  private Region region;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id", nullable = true)
  private Place place;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SavePlace> savedPlaces = new ArrayList<>();

  public void update(String name, Color color) {
    this.name = name;
    this.color = color;
  }
  @PreRemove
  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }
}

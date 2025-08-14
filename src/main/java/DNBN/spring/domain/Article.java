package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class Article extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long articleId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id", nullable = false)
  private Place place;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long likesCount = 0L;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long spamCount = 0L;

  @Column(name = "comment_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long commentCount;

  private LocalDateTime deletedAt;

  private String hashtag;
  
  public void increaseLikeCount() {
    this.likesCount++;
  }

  public void decreaseLikeCount() {
    this.likesCount = Math.max(0, this.likesCount - 1);
  }

  public void increaseSpamCount() {
    this.spamCount++;
  }

  public void decreaseSpamCount() {
    this.spamCount = Math.max(0, this.spamCount - 1);
  }
 
  @Column(nullable = false)
  private LocalDate date;

  public void delete() {
        this.deletedAt = java.time.LocalDateTime.now();
    }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public void setPlace(Place place) {
    this.place = place;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challengeId")
  private Challenge challenge;
}

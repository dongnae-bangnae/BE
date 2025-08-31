package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.code.status.ErrorStatus;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
 
  public void increaseCommentCount() {
    this.commentCount = this.commentCount == null ? 1 : this.commentCount + 1;
  }

  public void decreaseCommentCount() {
    this.commentCount = (this.commentCount == null || this.commentCount <= 0) ? 0 : this.commentCount - 1;
  }

  @Column(nullable = false)
  private LocalDate date;

  public void delete() {
        this.deletedAt = java.time.LocalDateTime.now();
    }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  public boolean isActive() {
    return !isDeleted();
  }

  public boolean isNotDeleted() {
    return !isDeleted();
  }

  public void updateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new ArticleHandler(ErrorStatus.ARTICLE_TITLE_NULL_ERROR);
    }
    this.title = title;
  }

  public void updateContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      throw new ArticleHandler(ErrorStatus.ARTICLE_CONTENT_NULL_ERROR);
    }
    this.content = content;
  }

  public void updateDate(LocalDate date) {
    if (date == null) {
      throw new ArticleHandler(ErrorStatus.ARTICLE_DATE_NULL_ERROR);
    }
    this.date = date;
  }

  public void updateCategory(Category category) {
    if (category == null) {
      throw new ArticleHandler(ErrorStatus.ARTICLE_CATEGORY_NULL_ERROR);
    }
    this.category = category;
  }

  public void updatePlace(Place place) {
    if (place == null) {
      throw new ArticleHandler(ErrorStatus.ARTICLE_PLACE_NULL_ERROR);
    }
    this.place = place;
  }

  public void updateRegion(Region region) {
    if (region == null) {
      throw new ArticleHandler(ErrorStatus.ARTICLE_REGION_NULL_ERROR);
    }
    this.region = region;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challengeId")
  private Challenge challenge;

    @Builder
    public Article(Long articleId, Member member, Category category, Place place, Region region, String title, String content, Long likesCount, Long spamCount, Long commentCount, LocalDateTime deletedAt, String hashtag, LocalDate date, Challenge challenge) {
        if (title == null || title.trim().isEmpty()) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_TITLE_NULL_ERROR);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_CONTENT_NULL_ERROR);
        }
        if (date == null) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_DATE_NULL_ERROR);
        }
        this.articleId = articleId;
        this.member = member;
        this.category = category;
        this.place = place;
        this.region = region;
        this.title = title;
        this.content = content;
        this.likesCount = likesCount == null ? 0L : likesCount;
        this.spamCount = spamCount == null ? 0L : spamCount;
        this.commentCount = commentCount;
        this.deletedAt = deletedAt;
        this.hashtag = hashtag;
        this.date = date;
        this.challenge = challenge;
    }
}

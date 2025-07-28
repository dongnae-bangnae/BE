package DNBN.spring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_spam")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleSpam {

    @EmbeddedId
    private ArticleSpamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null && this.article != null && this.member != null) {
            this.id = new ArticleSpamId(article.getArticleId(), member.getId());
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static ArticleSpam of(Article article, Member member) {
        return ArticleSpam.builder()
                .article(article)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
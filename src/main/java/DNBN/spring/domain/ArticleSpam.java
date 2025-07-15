package DNBN.spring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_spam", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"article_id", "member_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleSpam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
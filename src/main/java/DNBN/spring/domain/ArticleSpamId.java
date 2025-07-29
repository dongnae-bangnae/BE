package DNBN.spring.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArticleSpamId implements Serializable {
    private Long articleId;
    private Long memberId;
}

package DNBN.spring.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSpamId implements Serializable {
    private Long articleId;
    private Long memberId;
}

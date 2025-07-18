package DNBN.spring.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArticleLikeId implements Serializable {
    private Long articleId;
    private Long memberId;
}

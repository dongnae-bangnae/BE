package DNBN.spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Curation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationId;

    private String title;

    private Long likeCount;

    private Long commentCount;

    private LocalDate createdAt;


}

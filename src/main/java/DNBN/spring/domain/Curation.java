package DNBN.spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Curation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationId;

    private String title;

    private Long likeCount;

    private Long commentCount;

    private LocalDate createdAt;


}

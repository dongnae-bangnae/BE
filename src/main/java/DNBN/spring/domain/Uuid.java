package DNBN.spring.domain;

import jakarta.persistence.*;
import lombok.*;
import DNBN.spring.domain.common.BaseEntity;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uuid extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuidId;

    @Column(unique = true)
    private String uuid;
}
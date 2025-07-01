package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicInsert // insert 시 null인 필드를 제외하고 실제 값이 있는 컬럼만 포함하여 INSERT 문을 생성
@DynamicUpdate // update 시 변경된 컬럼만 포함해서 SQL UPDATE 문을 동적으로 생성
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; // KAKAO, NAVER, GOOGLE 등
    private String nickname;
    private String profileImage;

    private boolean isOnboarded;

    public enum Provider {
        KAKAO,
        GOOGLE,
        NAVER
    }
}

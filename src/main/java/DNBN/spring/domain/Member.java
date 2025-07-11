package DNBN.spring.domain;

import DNBN.spring.domain.common.BaseEntity;
import DNBN.spring.domain.enums.Provider;
import DNBN.spring.domain.mapping.LikePlace;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicInsert // insert 시 null인 필드를 제외하고 실제 값이 있는 컬럼만 포함하여 INSERT 문을 생성
@DynamicUpdate // update 시 변경된 컬럼만 포함해서 SQL UPDATE 문을 동적으로 생성
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING) // 자바 enum 값을 문자열(String) 형태로 DB에 저장
    @Column(nullable = false) // null 불가 컬럼
    private Provider provider; // KAKAO, NAVER, GOOGLE

    private String nickname;

//    private String profileImage;
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // 연관된 자식 엔티티가 부모에서 제거되었을 때, DB에서도 자동 삭제되도록
    private List<LikePlace> likePlaceList = new ArrayList<>();

    private boolean isOnboardingCompleted;

    public void updateNicknameByOnboarding(String nickname) {
        this.nickname = nickname;
//        this.profileImage = profileImage;
//        this.isOnboardingCompleted = true;
    }
}

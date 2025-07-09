package DNBN.spring.apiPayload.code.status;

import DNBN.spring.apiPayload.code.BaseCode;
import DNBN.spring.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    
    // 멤버 관련 응답
    MEMBER_LOGIN_SUCCESS(HttpStatus.OK, "MEMBER2001", "이미 등록된 유저입니다."),
    MEMBER_NEEDS_ONBOARDING(HttpStatus.CREATED, "MEMBER2002", "신규 유저입니다. 온보딩이 필요합니다."),
    MEMBER_ONBOARDING_SUCCESS(HttpStatus.OK, "MEMBER2003", "온보딩 정보를 저장했습니다."),
    MEMBER_INFO_RETRIEVED(HttpStatus.OK, "MEMBER2004", "유저 정보를 성공적으로 조회했습니다."),
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER2005", "로그아웃이 완료되었습니다."),
    MEMBER_DELETE_SUCCESS(HttpStatus.OK, "MEMBER2006", "회원 탈퇴가 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}

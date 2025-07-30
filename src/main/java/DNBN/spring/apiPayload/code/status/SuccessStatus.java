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
    _OK(HttpStatus.OK, "COMMON2000", "성공입니다."),
    
    // 멤버 관련 응답
    MEMBER_NEEDS_ONBOARDING(HttpStatus.CREATED, "MEMBER2001", "신규 유저입니다. 온보딩이 필요합니다."),
    MEMBER_ALREADY_LOGIN(HttpStatus.OK, "MEMBER2002", "이미 등록된 유저입니다."),
    MEMBER_ONBOARDING_SUCCESS(HttpStatus.OK, "MEMBER2003", "온보딩 정보를 저장했습니다."),
    MEMBER_INFO_RETRIEVED(HttpStatus.OK, "MEMBER2004", "유저 정보를 성공적으로 조회했습니다."),
    MEMBER_LOGOUT_SUCCESS(HttpStatus.OK, "MEMBER2005", "로그아웃이 완료되었습니다."),
    MEMBER_DELETE_SUCCESS(HttpStatus.OK, "MEMBER2006", "회원 탈퇴가 완료되었습니다."),
    MEMBER_PROFILE_IMAGE_UPDATED(HttpStatus.OK, "MEMBER2007", "프로필 이미지가 성공적으로 변경되었습니다."),

    // 장소 저장 관련 응답
    SAVED_PLACE_CREATE_SUCCESS(HttpStatus.OK, "SAVE_PLACE2001", "장소가 카테고리에 성공적으로 저장되었습니다."),

    // 댓글
    COMMENT_CREATE_SUCCESS(HttpStatus.OK, "COMMENT_CREATE_SUCCESS", "댓글이 정상적으로 작성되었습니다."),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "COMMENT_DELETE_SUCCESS", "댓글이 정상적으로 삭제되었습니다."),

    // 동네 검색
    REGION_SEARCH_SUCCESS(HttpStatus.OK, "REGION2001", "지역 검색 결과입니다.")
    ;

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

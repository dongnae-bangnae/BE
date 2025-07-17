package DNBN.spring.apiPayload.code.status;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON4000","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON4001","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "금지된 요청입니다."),

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수입니다."),
    ONBOARDING_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "MEMBER4003", "이미 존재하는 사용자입니다. 온보딩을 마치지 않았습니다."),
    INVALID_REGION_COUNT(HttpStatus.BAD_REQUEST, "MEMBER4004", "좋아하는 동네는 최소 1개, 최대 3개까지 선택할 수 있습니다."),
    SOCIALID_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4005", "socialId가 없습니다."),
    
    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION4001", "해당 지역이 존재하지 않습니다."),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4001", "가게가 없습니다."),

    PAGE_NOT_VALID(HttpStatus.BAD_REQUEST, "PAGE4001", "페이지는 1 이상이어야 합니다."),

    //jwt 토큰
    INVALID_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "유효하지 않은 AccessToken입니다."),
    INVALID_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4002", "유효하지 않은 RefreshToken입니다."),

    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4003", "유효하지 않은 SocialToken입니다."),

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "PW40001", "잘못된 비밀번호입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}

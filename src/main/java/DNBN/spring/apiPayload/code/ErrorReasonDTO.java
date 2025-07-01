package DNBN.spring.apiPayload.code;

import org.springframework.http.HttpStatus;

public class ErrorReasonDTO {
    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;

    public boolean getIsSuccess(){return isSuccess;}
}

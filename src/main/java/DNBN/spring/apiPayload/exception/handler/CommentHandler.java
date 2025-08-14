package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class CommentHandler extends GeneralException {
    public CommentHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class ArticleHandler extends GeneralException {
    public ArticleHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

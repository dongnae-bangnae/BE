package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class CategoryHandler extends GeneralException {
    public CategoryHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
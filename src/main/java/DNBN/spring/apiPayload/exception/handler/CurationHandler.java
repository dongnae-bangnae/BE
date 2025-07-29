package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class CurationHandler extends GeneralException {
    public CurationHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

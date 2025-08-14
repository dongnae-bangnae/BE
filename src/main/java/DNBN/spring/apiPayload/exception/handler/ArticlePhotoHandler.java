package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class ArticlePhotoHandler extends GeneralException {
    public ArticlePhotoHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

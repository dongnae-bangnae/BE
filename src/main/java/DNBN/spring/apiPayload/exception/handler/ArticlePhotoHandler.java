package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.status.ErrorStatus;

public class ArticlePhotoHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public ArticlePhotoHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}


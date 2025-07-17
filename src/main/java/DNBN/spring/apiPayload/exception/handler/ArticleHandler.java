package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.status.ErrorStatus;

public class ArticleHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public ArticleHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}


package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.status.ErrorStatus;

public class PlaceHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public PlaceHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}


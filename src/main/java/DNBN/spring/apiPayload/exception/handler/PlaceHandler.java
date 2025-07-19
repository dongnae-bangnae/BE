package DNBN.spring.apiPayload.exception.handler;
import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class PlaceHandler extends GeneralException {
    public PlaceHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

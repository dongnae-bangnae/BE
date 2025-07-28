package DNBN.spring.apiPayload.exception.handler;

import DNBN.spring.apiPayload.code.BaseErrorCode;
import DNBN.spring.apiPayload.exception.GeneralException;

public class ChallengeHandler extends GeneralException {
  public ChallengeHandler(BaseErrorCode errorCode) {
    super(errorCode);
  }
}

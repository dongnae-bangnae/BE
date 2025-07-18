package DNBN.spring.apiPayload.exception;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException() {
        super("이미 존재하는 카테고리 이름입니다.");
    }
}

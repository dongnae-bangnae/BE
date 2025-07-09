package DNBN.spring.domain.enums;

public enum Provider {
    KAKAO, NAVER, GOOGLE;

    public static Provider from(String value) {
        return switch (value.toLowerCase()) {
            case "kakao" -> KAKAO;
            case "google" -> GOOGLE;
            case "naver" -> NAVER;
            default -> throw new IllegalArgumentException("지원하지 않는 provider입니다: " + value);
        };
    }
}

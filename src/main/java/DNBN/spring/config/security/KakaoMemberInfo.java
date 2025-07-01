//package DNBN.spring.config.security;
//
//import java.util.Map;
//
//public class KakaoMemberInfo extends OAuth2MemberInfo {
//    private final Map<String, Object> attributes;
//
//    public KakaoUserInfo(Map<String, Object> attributes) {
//        this.attributes = attributes;
//    }
//
//    public String getSocialId() {
//        return (String) attributes.get("id");
//    }
//
//    public String getNickname() {
//        return ((Map<String, Object>) attributes.get("properties")).get("nickname").toString();
//    }
//
//    public String getProfileImage() {
//        return ((Map<String, Object>) attributes.get("properties")).get("profile_image").toString();
//    }
//}

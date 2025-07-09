package DNBN.spring.service.OAuth2;

import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {
    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getNickname() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("nickname");
    }
//    private final Map<String, Object> attributes;
//
//    public KakaoMemberInfo(Map<String, Object> attributes) {
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
}

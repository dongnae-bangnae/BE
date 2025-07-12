package DNBN.spring.service.OAuth2;

import java.util.Map;

public class NaverUserInfo extends OAuth2UserInfo {
    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() {
        return attributes.get("id").toString();
    }
}

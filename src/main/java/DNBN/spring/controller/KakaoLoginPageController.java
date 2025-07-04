//package DNBN.spring.controller;
//
//import lombok.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/login")
//public class KakaoLoginPageController {
//    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
//    private String client_id;
//
//    @Value("${kakao.redirect_uri}")
//    private String redirect_uri;
//
//    @GetMapping("/page")
//    public String loginPage(Model model) {
//        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
//        model.addAttribute("location", location);
//
//        return "login";
//    }
//}

package DNBN.spring.web.controller;

import DNBN.spring.web.dto.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberRestController {
    @GetMapping("/info")
    @Operation(summary = "유저 내 정보 조회 API - 인증 필요",
            description = "유저가 내 정보를 조회하는 API입니다."
//            security = { @SecurityRequirement(name = "JWT TOKEN") } // ‘내 정보 조회’는 로그인한 사용자만이 접근할 수 있는 API여야 함 --> Swagger 어노테이션인 @Operation 어노테이션에 security 필드를 추가해서 token이 요청 필수값임을 명시
    )
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> getMyInfo(HttpServletRequest request) {
        return ApiResponse.onSuccess(memberQueryService.getMemberInfo(request));
    }
}

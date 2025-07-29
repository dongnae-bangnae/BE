package DNBN.spring.web.controller;

import DNBN.spring.apiPayload.ApiResponse;
import DNBN.spring.service.RegionService.RegionQueryService;
import DNBN.spring.web.dto.RegionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionRestController {

    private final RegionQueryService regionQueryService;

    @GetMapping("/search")
    @Operation(summary = "동네 검색", description = "키워드로 관심 동네를 검색합니다.")
    public ResponseEntity<ApiResponse<RegionResponseDTO.SearchRegionResult>> searchRegionList(
            @Parameter(name = "keyword", description = "검색 키워드", required = true)
            @RequestParam String keyword,

            @Parameter(name = "cursor", description = "다음 페이지 요청 시 기준이 되는 regionId (default: null)", example = "0")
            @RequestParam(required = false) Long cursor,

            @Parameter(name = "limit", description = "최대 응답 개수 (default: 20)", example = "20")
            @RequestParam(defaultValue = "20") int limit
    ) {
        RegionResponseDTO.SearchRegionResult result = regionQueryService.searchRegion(keyword, cursor, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}

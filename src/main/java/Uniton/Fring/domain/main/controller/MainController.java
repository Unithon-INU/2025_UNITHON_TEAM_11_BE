package Uniton.Fring.domain.main.controller;

import Uniton.Fring.domain.main.dto.res.MainProductResponseDto;
import Uniton.Fring.domain.main.dto.res.MainRecipeResponseDto;
import Uniton.Fring.domain.main.dto.res.MainResponseDto;
import Uniton.Fring.domain.main.dto.res.SearchAllResponseDto;
import Uniton.Fring.domain.main.service.MainService;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController implements MainApiSpecification{

    private final MainService mainService;

    // 메인 페이지
    @GetMapping
    public ResponseEntity<MainResponseDto> mainInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mainService.mainInfo(userDetails));
    }

    // 메인 페이지 전체 검색
    @GetMapping("/search")
    public ResponseEntity<SearchAllResponseDto> searchAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(mainService.searchAll(userDetails, keyword, page));
    }

    // 장터 메인 페이지
    @GetMapping("/product")
    public ResponseEntity<MainProductResponseDto> mainProductInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mainService.mainProductInfo(userDetails));
    }

    // 레시피 메인 페이지
    @GetMapping("/recipe")
    public ResponseEntity<MainRecipeResponseDto> mainRecipeInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mainService.mainRecipeInfo(userDetails));
    }
}

package Uniton.Fring.domain.main.controller;

import Uniton.Fring.domain.main.dto.res.MainProductResponseDto;
import Uniton.Fring.domain.main.dto.res.MainRecipeResponseDto;
import Uniton.Fring.domain.main.dto.res.MainResponseDto;
import Uniton.Fring.domain.main.dto.res.SearchAllResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Main", description = "메인 페이지 정보 API")
public interface MainApiSpecification {

    @Operation(
            summary = "메인 페이지",
            description = "메인 페이지의 상품과 레시피 정보들을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메인 페이지 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MainResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<MainResponseDto> mainInfo(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "메인 페이지 전체 검색",
            description = "메인 페이지의 검색 기능 (각각 3개 씩)<br><br>키워드를 포함한 회원, 농수산품, 레시피 정보들을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "전체 검색 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SearchAllResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<SearchAllResponseDto> searchAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "장터 메인 페이지",
            description = "장터 메인 페이지의 상품 정보들을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장터 메인 페이지 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MainProductResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<MainProductResponseDto> mainProductInfo(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "레시피 메인 페이지",
            description = "레시피 메인 페이지의 레시피 정보들을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 메인 페이지 응답 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MainRecipeResponseDto.class)
                            )
                    )
            }
    )
    ResponseEntity<MainRecipeResponseDto> mainRecipeInfo(@AuthenticationPrincipal UserDetailsImpl userDetails);
}
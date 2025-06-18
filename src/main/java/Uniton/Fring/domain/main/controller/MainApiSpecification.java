package Uniton.Fring.domain.main.controller;

import Uniton.Fring.domain.main.dto.MainProductResponseDto;
import Uniton.Fring.domain.main.dto.MainRecipeResponseDto;
import Uniton.Fring.domain.main.dto.MainResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
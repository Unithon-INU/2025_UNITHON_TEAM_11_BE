package Uniton.Fring.domain.client;

import Uniton.Fring.domain.client.dto.req.TitleSuggestionRequestDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "AiClient", description = "AI 관련 API")
public interface AiClientApiSpecification {

    @Operation(
            summary = "연관 농수산품 목록 조회",
            description = "AI 서버에 연관된 농수산품들을 조회를 요청합니다. (실패 시 빈 문자열 반환)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "연관 농수산품 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleProductResponseDto.class, type = "array")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다.")

            }
    )
    ResponseEntity<List<SimpleProductResponseDto>> relatedProducts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @PathVariable Long id);

    @Operation(
            summary = "농수산품 제목 추천",
            description = "AI 서버에 농수산품에 대한 제목 추천을 요청합니다. (실패 시 null 반환)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "농수산품 제목 추천",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    )

            }
    )
    ResponseEntity<String> suggestTitle(@RequestBody @Valid TitleSuggestionRequestDto dto);
}

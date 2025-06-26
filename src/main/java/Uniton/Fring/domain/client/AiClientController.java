package Uniton.Fring.domain.client;

import Uniton.Fring.domain.client.dto.req.TitleSuggestionRequestDto;
import Uniton.Fring.domain.product.dto.res.SimpleProductResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiClientController implements AiClientApiSpecification{

    private final AiClientService aiClientService;

    // 연관 상품
    @GetMapping("/products/{id}/related")
    public ResponseEntity<List<SimpleProductResponseDto>> relatedProducts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                          @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(aiClientService.relatedProducts(userDetails, id));
    }

    // 제목 추천
    @PostMapping("/products/title-suggest")
    public ResponseEntity<String> suggestTitle(@RequestBody @Valid TitleSuggestionRequestDto titleSuggestionRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(aiClientService.suggestTitle(titleSuggestionRequestDto));
    }
}

package Uniton.Fring.domain.recipe.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Schema(description = "레시피 단계 DTO")
public class RecipeStepRequestDto {

    @Schema(description = "단계 번호", example = "1")
    private int stepOrder;

    @Schema(description = "조리 이미지", type = "string", format = "binary")
    private MultipartFile stepImage;

    @Schema(description = "단계 설명", example = "감자를 깍둑 썰기")
    private String description;
}

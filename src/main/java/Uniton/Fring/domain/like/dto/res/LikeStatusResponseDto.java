package Uniton.Fring.domain.like.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "좋아요 응답 DTO")
public class LikeStatusResponseDto {

    @Schema(description = "좋아요 대상 이름", example = "두옹균 / 계란 / 프렌치토스트 / 잘 먹었습니다.")
    private String name;

    @Schema(description = "좋아요 여부", example = "true")
    private boolean isLiked;

    @Schema(description = "좋아요 수", example = "123")
    private Integer likeCount;

    @Builder
    private LikeStatusResponseDto(String name, boolean isLiked, Integer likeCount) {
        this.name = name;
        this.isLiked = isLiked;
        this.likeCount = likeCount;
    }
}

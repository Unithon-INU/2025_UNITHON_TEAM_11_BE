package Uniton.Fring.domain.like.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "찜 목록 조회 응답 DTO")
public class LikedItemsResponseDto<T> {

    @Schema(description = "찜한 총 개수", example = "23")
    private final int totalCount;

    @Schema(description = "찜한 항목 리스트")
    private final List<T> items;

    @Builder
    public LikedItemsResponseDto(int totalCount, List<T> items) {
        this.totalCount = totalCount;
        this.items = items;
    }
}

package Uniton.Fring.domain.review.dto.res;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.review.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "댓글 정보 응답 DTO")
public class CommentResponseDto {

    @Schema(description = "회원", example = "dor****")
    private final MemberInfoResponseDto memberInfo;

    @Schema(description = "댓글 내용", example = "여기 계란 먹어보신 분 있나요??")
    private final String content;

    @Schema(description = "댓글 작성 일자", example = "2025-01-23")
    private final LocalDate createdAt;

    @Schema(description = "댓글 수정 일자", example = "2025-02-23")
    private final LocalDate updatedAt;

    @Builder
    private CommentResponseDto(MemberInfoResponseDto memberInfo, Comment comment) {
        this.memberInfo = memberInfo;
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}

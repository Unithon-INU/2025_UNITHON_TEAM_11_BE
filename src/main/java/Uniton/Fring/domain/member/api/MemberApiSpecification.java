package Uniton.Fring.domain.member.api;

import Uniton.Fring.domain.member.dto.res.MemberInfoResponseDto;
import Uniton.Fring.domain.member.dto.res.SimpleMemberResponseDto;
import Uniton.Fring.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Member", description = "회원 관련 API")
public interface MemberApiSpecification {

    @Operation(
            summary = "레시피 유저 검색",
            description = "검색어(keyword)를 이용해 레시피 유저를 페이징 형태로 검색합니다. (기본 size = 10)",
            parameters = {
                    @Parameter(name = "keyword", description = "검색어", required = true),
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 유저 검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleMemberResponseDto.class, type = "array")
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터 (예: keyword 누락, 잘못된 page/size 형식 등)")
            }
    )
    ResponseEntity<List<SimpleMemberResponseDto>> searchRecipeMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "판매자 유저 검색",
            description = "검색어(keyword)를 이용해 판매자 유저를 페이징 형태로 검색합니다. (기본 size = 10)",
            parameters = {
                    @Parameter(name = "keyword", description = "검색어", required = true),
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "판매자 유저 검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleMemberResponseDto.class, type = "array")
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터 (예: keyword 누락, 잘못된 page/size 형식 등)")
            }
    )
    ResponseEntity<List<SimpleMemberResponseDto>> searchFarmerMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "레시피 유저 랭킹 조회",
            description = "레시피 작성 유저를 좋아요 수 기준으로 상위 8위까지 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "레시피 유저 랭킹 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleMemberResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleMemberResponseDto>> getRankingRecipeMember(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "판매자 유저 랭킹 조회",
            description = "판매자 유저를 좋아요 수 기준으로 상위 8위까지 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "판매자 유저 랭킹 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleMemberResponseDto.class, type = "array")
                            )
                    )
            }
    )
    ResponseEntity<List<SimpleMemberResponseDto>> getRankingFarmer(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "유저 정보 조회",
            description = "유저의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "유저 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberInfoResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "회원을 찾을 수 없습니다."),
            }
    )
    ResponseEntity<MemberInfoResponseDto> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @PathVariable Long memberId,
                                                        @RequestParam(defaultValue = "0") int page);

    @Operation(
            summary = "회원 탈퇴",
            description = "회원 탈퇴를 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
                    @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다.")
            }
    )
    ResponseEntity<Void> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails);

}

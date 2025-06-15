package Uniton.Fring.domain.member.api;

import Uniton.Fring.domain.member.dto.req.EmailCheckDto;
import Uniton.Fring.domain.member.dto.req.EmailRequestDto;
import Uniton.Fring.domain.member.dto.res.EmailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Mail", description = "이메일 인증 관련 API")
public interface MailApiSpecification {

    @Operation(
            summary = "메일 전송",
            description = "이메일로 인증 메일 전송",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메일 전송 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmailResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
            }
    )
    ResponseEntity<EmailResponseDto> sendMail(@RequestBody @Valid EmailRequestDto emailRequestDto);

    @Operation(summary = "메일 인증",
            description = "이메일을 기준으로 코드 인증",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "메일 인증 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmailResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400",description = "인증 번호가 유효하지 않습니다."),
                    @ApiResponse(responseCode = "400", description = "이메일이 일치하지 않습니다.")
            }
    )
    ResponseEntity<EmailResponseDto> verifyMail(@RequestBody @Valid EmailCheckDto emailCheckDto);
}
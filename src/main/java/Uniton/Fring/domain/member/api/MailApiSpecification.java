package Uniton.Fring.domain.member.api;

import Uniton.Fring.domain.member.dto.req.EmailCheckDto;
import Uniton.Fring.domain.member.dto.req.EmailRequestDto;
import Uniton.Fring.domain.member.dto.res.EmailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Mail", description = "이메일 인증 관련 API")
public interface MailApiSpecification {

    @Operation(summary = "메일 전송", description = "학교 이메일으로 인증 메일 전송")
    ResponseEntity<EmailResponseDto> sendMail(@RequestBody @Valid EmailRequestDto emailRequestDto);

    @Operation(summary = "메일 인증", description = "학교 이메일을 기준으로 코드 인증")
    ResponseEntity<EmailResponseDto> verifyMail(@RequestBody @Valid EmailCheckDto emailCheckDto);
}
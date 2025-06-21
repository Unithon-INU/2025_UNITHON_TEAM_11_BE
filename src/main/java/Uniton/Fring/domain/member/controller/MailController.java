package Uniton.Fring.domain.member.controller;

import Uniton.Fring.domain.member.api.MailApiSpecification;
import Uniton.Fring.domain.member.dto.req.EmailCheckDto;
import Uniton.Fring.domain.member.dto.req.EmailRequestDto;
import Uniton.Fring.domain.member.dto.res.EmailResponseDto;
import Uniton.Fring.domain.member.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mails")
public class MailController implements MailApiSpecification {

    private final MailService mailService;

    // 이메일 전송
    @PostMapping
    public ResponseEntity<EmailResponseDto> sendMail(@RequestBody @Valid EmailRequestDto emailRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(mailService.sendMail(emailRequestDto.getEmail()));
    }

    // 이메일 인증
    @PostMapping("/verify")
    public ResponseEntity<EmailResponseDto> verifyMail(@RequestBody @Valid EmailCheckDto emailCheckDto){
        return ResponseEntity.status(HttpStatus.OK).body(mailService.verifyMail(emailCheckDto.getEmail(), emailCheckDto.getAuthNumber()));
    }
}
package Uniton.Fring.domain.member.service;

import Uniton.Fring.domain.member.dto.res.EmailResponseDto;
import Uniton.Fring.domain.member.util.RedisUtil;
import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisUtil redisUtil;
    private int authNumber;

    @Value("${mail.username}")
    private String fromEmail;

    public void makeRandomNumber() {
        Random random = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(random.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    public EmailResponseDto verifyMail(String email, String authNum) {
        EmailResponseDto emailResponseDto = EmailResponseDto.builder()
                .email(email)
                .authNumber(Integer.toString(authNumber))
                .build();

        if (redisUtil.getData(authNum) == null){
            throw new CustomException(ErrorCode.EMAIL_AUTH_NUMBER_NOT_FOUND);
        }

        if (!redisUtil.getData(authNum).equals(email)){
            throw new CustomException(ErrorCode.EMAIL_MISMATCH);
        }

        return emailResponseDto;
    }

    public EmailResponseDto sendMail(String email){
        makeRandomNumber();
        String setFrom = fromEmail;
        String toMail = email;
        String title = "프링 인증 이메일 입니다.";
        String content =
                "<h1>[Fring] 프링 계정 인증 요청</h1>" +
                        "<br><br>" +
                        "<p>안녕하세요, 프링 회원님,</p>" +
                        "<p>Fring 서비스를 이용해 주셔서 감사합니다.</p>" +
                        "<br><br>" +
                        "<p>아래 인증 번호를 확인하여 이메일 인증을 완료해 주세요.</p>" +
                        "<br><br>" +
                        "<p><strong>인증 번호:</strong> <span style='font-size: 18px; font-weight: bold; color: #007BFF;'>" + authNumber + "</span></p>" +
                        "<br><br>" +
                        "<ul>" +
                        "<li>이 코드는 10분 동안 유효합니다.</li>" +
                        "<li>코드를 정확히 입력하지 않을 경우, 인증이 완료되지 않습니다.</li>" +
                        "</ul>" +
                        "<br><br>" +
                        "<p>인증 완료 후, Fring 서비스를 정상적으로 이용하실 수 있습니다.</p>" +
                        "<p>문의사항이 있으시면 아래 연락처로 연락주세요.</p>" +
                        "<br><br>" +
                        "<p>감사합니다.</p>" +
                        "<p>- Fring 운영팀</p>" +
                        "<p>- 문의: <a href='mailto:Fring-support@inu.ac.kr'>Fring-support@inu.ac.kr</a></p>";

        mailSend(setFrom, toMail, title, content);

        return EmailResponseDto.builder()
                .email(email)
                .authNumber(Integer.toString(authNumber))
                .build();
    }

    private void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(setFrom); //이메일의 발신자 주소 설정
            helper.setTo(toMail); //이메일의 수신자 주소 설정
            helper.setSubject(title); //이메일의 제목을 설정
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

        // 5분 동안 인증번호가 생존
        redisUtil.setDataExpire(Integer.toString(authNumber), toMail, 60 * 10L);
    }
}
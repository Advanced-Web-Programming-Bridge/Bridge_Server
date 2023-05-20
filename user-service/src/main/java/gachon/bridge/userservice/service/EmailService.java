package gachon.bridge.userservice.service;

import gachon.bridge.userservice.base.BaseErrorCode;
import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.EmailConfirmToken;
import gachon.bridge.userservice.dto.SendLinkResponseDTO;
import gachon.bridge.userservice.repository.ConfirmEmailTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import static gachon.bridge.userservice.utils.EmailValidator.validEmailFormat;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final ConfirmEmailTokenRepository confirmationTokenRepository;
    private final EmailSenderService emailSenderService;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);


    /***
     * 사용자의 이메일에 인증 링크 보내기
     *
     * @param receiverEmail 사용자의 이메일
     * @return 이메일을 보낸 날짜
     * @throws BaseException 사용자의 이메일이 올바른 형식이 아닌 경우
     */
    public SendLinkResponseDTO sendToken(String receiverEmail) throws BaseException {
        if (!validEmailFormat(receiverEmail)) {
            log.warn("Invalid email address: {}", receiverEmail);
            throw new BaseException(BaseErrorCode.INVALID_EMAIL);
        }

        EmailConfirmToken token = EmailConfirmToken.createEmailConfirmationToken();
        confirmationTokenRepository.save(token);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("회원가입 이메일 인증");
        mailMessage.setText("http://localhost:8090/confirm-email?token=" + token.getId());
        emailSenderService.sendEmail(mailMessage);

        return new SendLinkResponseDTO();
    }

}

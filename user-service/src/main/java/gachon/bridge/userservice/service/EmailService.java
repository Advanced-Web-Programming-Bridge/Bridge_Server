package gachon.bridge.userservice.service;

import gachon.bridge.userservice.base.BaseErrorCode;
import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.EmailConfirmToken;
import gachon.bridge.userservice.dto.EmailVerificationResponseDto;
import gachon.bridge.userservice.dto.SendLinkResponseDTO;
import gachon.bridge.userservice.repository.EmailTokenRepository;
import gachon.bridge.userservice.utils.EmailTokenValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import static gachon.bridge.userservice.utils.EmailValidator.validEmailFormat;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final EmailTokenRepository emailTokenRepository;
    private final EmailSenderService emailSenderService;
    private final EmailTokenValidator emailTokenValidator;
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
        emailTokenRepository.save(token);

        String link = "http://localhost:8081/api/auths/email/confirm-email?token=" + token.getId();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("회원가입 이메일 인증");
        mailMessage.setText(link);
        emailSenderService.sendEmail(mailMessage);

        return new SendLinkResponseDTO();
    }

    /***
     * 해당 이메일이 유효한 이메일인지 인증
     *
     * @param tokenId 이메일 인증할 때 사용될 토큰
     * @return 인증을 성공한 날짜
     * @throws BaseException 토큰이 없거나, 유효하지 않거나, 이미 인증하는데 사용된 경우
     */
    public EmailVerificationResponseDto confirmEmail(String tokenId) throws BaseException {
        emailTokenValidator.checkEmailToken(tokenId);

        // 토큰 만료시키기
        EmailConfirmToken token = emailTokenRepository.findById(tokenId).get();
        token.expireToken();

        return new EmailVerificationResponseDto();
    }
}

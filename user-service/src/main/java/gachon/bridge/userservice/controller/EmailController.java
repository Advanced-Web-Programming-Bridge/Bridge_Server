package gachon.bridge.userservice.controller;

import gachon.bridge.userservice.base.BaseErrorCode;
import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.base.BaseResponse;
import gachon.bridge.userservice.dto.SendLinkRequestDTO;
import gachon.bridge.userservice.dto.SendLinkResponseDTO;
import gachon.bridge.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auths/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final Logger log = LoggerFactory.getLogger(EmailController.class);

    // 이메일에 인증 코드 보내기
    @PostMapping("/send-code")
    public BaseResponse<SendLinkResponseDTO> sendVerificationCode(@RequestBody SendLinkRequestDTO dto) {
        try {
            SendLinkResponseDTO data = emailService.sendToken(dto.getEmailAddress());

            if (data != null) {
                log.info("Verification link sent successfully to: {}", dto.getEmailAddress());
                return new BaseResponse<>(data);

            } else {
                throw new BaseException(BaseErrorCode.EMAIL_SEND_ERROR);
            }

        } catch (BaseException e) {
            log.error("Failed to send link to: {}", dto.getEmailAddress());
            return new BaseResponse<>(e);
        }
    }

}

package gachon.bridge.userservice.utils;

import gachon.bridge.userservice.base.BaseErrorCode;
import gachon.bridge.userservice.base.BaseException;
import gachon.bridge.userservice.domain.EmailConfirmToken;
import gachon.bridge.userservice.repository.ConfirmEmailTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EmailTokenValidator {

    private final ConfirmEmailTokenRepository confirmationTokenRepository;

    private static final String EMAIL_TOKEN_REGEX =
            "^[\\da-fA-F]{8}-[\\da-fA-F]{4}-[\\da-fA-F]{4}-[\\da-fA-F]{4}-[\\da-fA-F]{12}$";

    private static final Pattern EMAIL_TOKEN_PATTERN = Pattern.compile(EMAIL_TOKEN_REGEX);

    public boolean isValidEmailToken(String tokenId) throws BaseException {
        if (!EMAIL_TOKEN_PATTERN.matcher(tokenId).matches())
            throw new BaseException(BaseErrorCode.INVALID_UUID_FORMAT);

        Optional<EmailConfirmToken> token = confirmationTokenRepository.findById(tokenId);

        // 토큰이 없는 경우 (발급한 토큰이 아닌 경우)
        if (token.isEmpty())
            throw new BaseException(BaseErrorCode.TOKEN_NOT_EXIST);

        // 이미 인증하는데 사용된 토큰이거나 유효 기한이 지난 경우
        if (token.get().isExpired())
            throw new BaseException(BaseErrorCode.EXPIRED_TOKEN);

        // 유효 기간이 지난 경우
        if (token.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            token.get().expireToken();
            throw new BaseException(BaseErrorCode.EXPIRED_TOKEN);
        }

        return true;
    }
}

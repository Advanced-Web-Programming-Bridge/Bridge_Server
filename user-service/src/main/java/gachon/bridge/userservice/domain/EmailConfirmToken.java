package gachon.bridge.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailConfirmToken {

    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 5L; // 토큰 만료 시간

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @Column
    private LocalDateTime expirationDate;

    @Column
    private boolean expired;

    // 이메일 인증 토큰 생성
    public static EmailConfirmToken createEmailConfirmationToken() {
        EmailConfirmToken confirmationToken = new EmailConfirmToken();
        confirmationToken.createDate = LocalDateTime.now();
        confirmationToken.expirationDate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE); // 5분 후 만료
        confirmationToken.expired = false;

        return confirmationToken;
    }

    // 토큰 사용 혹은 유효기간 만료로 소멸
    public void expireToken() {
        expired = true;
    }
}
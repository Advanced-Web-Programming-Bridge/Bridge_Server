package gachon.bridge.userservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createRefreshToken(UUID userIdx) {
        return createJwtToken(userIdx, 1000L * 60 * 60 * 24 * 7); // 일주일
    }

    public String createAccessToken(UUID userIdx) {
        return createJwtToken(userIdx, 1000L * 60 * 60 * 24); // 1일
    }

    private String createJwtToken(UUID userIdx, Long time) {
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .claim("userIdx", userIdx)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .signWith(key)
                .compact();
    }

    public Boolean expired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    public UUID getUserIdx(String token) {
        String userIdx = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().get("userIdx", String.class);

        return UUID.fromString(userIdx);
    }
}

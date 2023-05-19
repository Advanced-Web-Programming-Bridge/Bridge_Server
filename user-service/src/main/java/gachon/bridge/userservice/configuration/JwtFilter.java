package gachon.bridge.userservice.configuration;

import gachon.bridge.userservice.service.UserService;
import gachon.bridge.userservice.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);

        // Token을 안 보낸 경우
        if (authorization == null) {
            log.error("authentication이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Token을 잘못 보낸 경우
        if (!authorization.startsWith("Bearer ")) {
            log.error("authentication을 잘못 보냈습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Token 꺼내기
        String accessToken = authorization.split(" ")[1];

        // Access token expired 여부
        if (jwtTokenProvider.expired(accessToken)) {
            log.error("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Token에서 user index 꺼내기
        UUID userIdx = jwtTokenProvider.getUserIdx(accessToken);
        log.info("user index: {}", userIdx);

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userIdx, null, List.of(new SimpleGrantedAuthority("USER")));

        // Detail 추가
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}

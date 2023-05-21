package gachon.bridge.userservice.configuration;

import gachon.bridge.userservice.service.UserService;
import gachon.bridge.userservice.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/auths/login").permitAll()
                        .requestMatchers("/api/auths/join").permitAll()
                        .requestMatchers("/api/auths/email/send-code").permitAll()
                        .requestMatchers("/api/auths/email/confirm-email").permitAll()
                        .anyRequest().authenticated()
                ) // join과 login은 상시 이용 가능
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 사용하는 경우 사용
                .and()
                .addFilterBefore(new JwtFilter(userService, jwtTokenProvider, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

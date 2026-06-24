package com.terry.backend.web.security.config;

import com.terry.backend.thirdparty.JWT.filter.JwtAuthenticationFilter;
import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.web.security.RoleType;
import com.terry.backend.web.security.WebLoginEntryPoint;
import com.terry.backend.web.security.service.UserDetailService;
import com.terry.backend.web.security.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final boolean isDebug;

    private final UserDetailService userDetailService;
    private final TokenProvider tokenProvider;

    private final WebLoginEntryPoint loginEntryPoint;


    public SecurityConfig(
            @Value("${debug}") boolean isDebug
            , UserDetailService userDetailService
            , TokenProvider tokenProvider
            , WebLoginEntryPoint loginEntryPoint
    ) {
        this.isDebug = isDebug;
        this.userDetailService = userDetailService;
        this.tokenProvider = tokenProvider;
        this.loginEntryPoint = loginEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        //NOTE: JWT 사용 시
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headerConfig -> headerConfig
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        if (isDebug) {
            http.authorizeHttpRequests(authorizeRequest -> authorizeRequest
                    .requestMatchers("/**").permitAll());
        }

        http
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(loginEntryPoint)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(userDetailService, tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers("/", "/login", "/signup", "/logout", "/token/refresh").permitAll()
                        .requestMatchers("/admin/**").hasAuthority(RoleType.ADMIN.name())
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        PasswordUtils.setPasswordEncoder(passwordEncoder);
        if (log.isDebugEnabled()) {
            log.debug("Temporal password for `test123' is " + passwordEncoder.encode("test123"));
        }
        return passwordEncoder;
    }

}

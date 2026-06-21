package com.terry.backend.thirdparty.JWT.filter;

import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.web.security.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailService userDetailService;
    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilter(
            UserDetailService userDetailService,
            TokenProvider tokenProvider
    ) {
        this.userDetailService = userDetailService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader =request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                if(tokenProvider.validateToken(token)){
                    String userId = tokenProvider.getUserId(token);
                    String clientIP = getClientIP(request);

                    try {
                        UserDetails userDetails = userDetailService.loadUserByUsername(userId);

                        if(userDetails != null){
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                            log.debug("JWT authentication successful for user: {}, IP: {}", userId, clientIP);
                        }
                    } catch (UsernameNotFoundException e) {
                        log.warn("JWT token contains invalid user ID: {}, IP: {}", userId, clientIP);
                    }
                } else {
                    log.debug("Invalid JWT token from IP: {}", getClientIP(request));
                }
            } catch (Exception e) {
                log.warn("JWT authentication error from IP: {}, error: {}", getClientIP(request), e.getMessage());
            }
        }
        filterChain.doFilter(request,response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}

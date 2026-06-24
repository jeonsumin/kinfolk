package com.terry.backend.web.security.controller;

import com.terry.backend.core.messages.handler.ResponseMessages;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.web.security.LoginRequest;
import com.terry.backend.web.security.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Slf4j
@RestController
@Validated
@Tag(name = "Authentication", description = "인증 관련 API")
public class WebLoginController {

    private final SecurityService service;

    public WebLoginController(SecurityService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인")
    public ResponseEntity<ResponseMessages> login(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        ResponseMessages result = service.login(loginRequest);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token을 재발급한다.")
    public ResponseMessages reissue(
            @RequestHeader("Refresh-Token") @NotBlank(message = "Refresh Token이 필요합니다.") String refreshToken
    ) throws Exception {
        return service.reissue(refreshToken.trim());
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "Refresh Token을 폐기하고 로그아웃한다.")
    public ResponseMessages logout() throws Exception {
        String userId = SessionUtils.getUserId();
        if (userId != null && !userId.equals("anonymousUser")) {
            service.revokeRefreshToken(userId);
        }
        return ResponseMessages.success();
    }

}

package com.terry.backend.web.security.controller;

import com.terry.backend.api.admin.system.member.dto.MemberDTO;
import com.terry.backend.api.admin.system.member.exception.MemberLoginIdAlreadyExists;
import com.terry.backend.api.admin.system.member.service.AdminMemberService;
import com.terry.backend.core.messages.handler.ResponseMessages;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.web.security.LoginRequest;
import com.terry.backend.web.security.SignupRequest;
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
    private final AdminMemberService adminMemberService;

    public WebLoginController(SecurityService service, AdminMemberService adminMemberService) {
        this.service = service;
        this.adminMemberService = adminMemberService;
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

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "로그인 아이디, 비밀번호, 이름으로 계정을 생성한다.")
    public ResponseEntity<ResponseMessages> signup(@Valid @RequestBody SignupRequest request) {
        try {
            adminMemberService.save(null, MemberDTO.builder()
                    .loginId(request.getUsername().trim())
                    .password(request.getPassword())
                    .name(request.getName().trim())
                    .use("Y")
                    .lock("N")
                    .build());
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMessages.success());
        } catch (MemberLoginIdAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseMessages.fail("이미 사용 중인 아이디입니다."));
        } catch (Exception e) {
            log.error("Signup failed", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseMessages.fail("회원가입에 실패했습니다."));
        }
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

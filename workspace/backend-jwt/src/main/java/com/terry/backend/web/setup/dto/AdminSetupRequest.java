package com.terry.backend.web.setup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "관리자 계정 생성 요청")
public class AdminSetupRequest {

    @Schema(description = "로그인 아이디", example = "admin")
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 4-20자 사이여야 합니다.")
    private String loginId;

    @Schema(description = "사용자 이름", example = "관리자")
    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(max = 50, message = "사용자 이름은 50자를 초과할 수 없습니다.")
    private String userName;

    @Schema(description = "비밀번호", example = "admin123!@#")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8-100자 사이여야 합니다.")
    private String password;

    @Schema(description = "이메일 주소", example = "admin@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
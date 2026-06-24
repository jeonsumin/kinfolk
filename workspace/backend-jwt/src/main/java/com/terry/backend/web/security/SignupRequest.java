package com.terry.backend.web.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 3, max = 50, message = "아이디는 3-50자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "아이디는 영문, 숫자, 점, 언더스코어, 하이픈만 사용 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(max = 100, message = "비밀번호가 너무 깁니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;
}

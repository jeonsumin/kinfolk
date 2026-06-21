package com.terry.backend.web.security;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginRequest 입력 검증 테스트
 * - Bean Validation 어노테이션 동작 검증
 * - 보안 패턴 및 길이 제한 테스트
 * - 악의적인 입력에 대한 방어 테스트
 */
class LoginRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // -----------------------------------------------------------------------
    // 정상 케이스 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 로그인 요청은 검증을 통과해야 한다")
    void validLoginRequest_shouldPassValidation() {
        // given
        LoginRequest request = new LoginRequest("testuser", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("최소 길이 사용자명도 검증을 통과해야 한다")
    void minimumLengthUsername_shouldPassValidation() {
        // given: 3자 사용자명
        LoginRequest request = new LoginRequest("abc", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("허용된 특수문자를 포함한 사용자명도 검증을 통과해야 한다")
    void usernameWithAllowedSpecialChars_shouldPassValidation() {
        // given
        LoginRequest request = new LoginRequest("test.user_name-123", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    // -----------------------------------------------------------------------
    // 사용자명 검증 실패 케이스
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("빈 사용자명은 검증에 실패해야 한다")
    void emptyUsername_shouldFailValidation() {
        // given
        LoginRequest request = new LoginRequest("", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("아이디를 입력해주세요");
    }

    @Test
    @DisplayName("null 사용자명은 검증에 실패해야 한다")
    void nullUsername_shouldFailValidation() {
        // given
        LoginRequest request = new LoginRequest(null, "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("아이디를 입력해주세요");
    }

    @Test
    @DisplayName("너무 짧은 사용자명은 검증에 실패해야 한다")
    void tooShortUsername_shouldFailValidation() {
        // given: 2자 사용자명
        LoginRequest request = new LoginRequest("ab", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("3-50자 사이여야 합니다");
    }

    @Test
    @DisplayName("너무 긴 사용자명은 검증에 실패해야 한다")
    void tooLongUsername_shouldFailValidation() {
        // given: 51자 사용자명
        StringBuilder longUsername = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            longUsername.append("a");
        }
        LoginRequest request = new LoginRequest(longUsername.toString(), "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("3-50자 사이여야 합니다");
    }

    @Test
    @DisplayName("허용되지 않은 특수문자가 포함된 사용자명은 검증에 실패해야 한다")
    void usernameWithInvalidSpecialChars_shouldFailValidation() {
        // given: 금지된 특수문자 포함
        String[] invalidUsernames = {
            "test@user",     // @ 문자
            "test user",     // 공백
            "test#user",     // # 문자
            "test$user",     // $ 문자
            "test%user",     // % 문자
            "test&user",     // & 문자
            "test*user",     // * 문자
            "test+user",     // + 문자
            "test=user",     // = 문자
            "test?user",     // ? 문자
            "test!user",     // ! 문자
            "test/user",     // / 문자
            "test\\user",    // \ 문자
            "test|user",     // | 문자
            "test<user",     // < 문자
            "test>user",     // > 문자
            "test(user)",    // 괄호
            "test[user]",    // 대괄호
            "test{user}",    // 중괄호
            "test'user",     // 작은따옴표
            "test\"user",    // 큰따옴표
            "test`user",     // 백틱
            "test~user",     // 물결표
            "test^user"      // 캐럿
        };

        for (String username : invalidUsernames) {
            // when
            LoginRequest request = new LoginRequest(username, "password123");
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertThat(violation.getMessage()).contains("영문, 숫자, 점, 언더스코어, 하이픈만 사용 가능합니다");
        }
    }

    // -----------------------------------------------------------------------
    // 비밀번호 검증 실패 케이스
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("빈 비밀번호는 검증에 실패해야 한다")
    void emptyPassword_shouldFailValidation() {
        // given
        LoginRequest request = new LoginRequest("testuser", "");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("비밀번호를 입력해주세요");
    }

    @Test
    @DisplayName("null 비밀번호는 검증에 실패해야 한다")
    void nullPassword_shouldFailValidation() {
        // given
        LoginRequest request = new LoginRequest("testuser", null);

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("비밀번호를 입력해주세요");
    }

    @Test
    @DisplayName("너무 긴 비밀번호는 검증에 실패해야 한다")
    void tooLongPassword_shouldFailValidation() {
        // given: 101자 비밀번호 생성
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longPassword.append("a");
        }
        LoginRequest request = new LoginRequest("testuser", longPassword.toString());

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("비밀번호가 너무 깁니다");
    }

    // -----------------------------------------------------------------------
    // 보안 공격 패턴 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("SQL Injection 패턴이 포함된 사용자명은 검증에 실패해야 한다")
    void sqlInjectionAttempt_shouldFailValidation() {
        // given: SQL Injection 시도
        String[] sqlInjectionPatterns = {
            "admin' OR '1'='1",
            "user'; DROP TABLE users;--",
            "admin' UNION SELECT * FROM passwords--",
            "'; SELECT * FROM users WHERE ''='"
        };

        for (String pattern : sqlInjectionPatterns) {
            // when
            LoginRequest request = new LoginRequest(pattern, "password");
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then: 패턴 검증으로 인해 실패해야 함
            assertThat(violations).isNotEmpty();
        }
    }

    @Test
    @DisplayName("XSS 공격 패턴이 포함된 사용자명은 검증에 실패해야 한다")
    void xssAttempt_shouldFailValidation() {
        // given: XSS 공격 시도
        String[] xssPatterns = {
            "<script>alert('xss')</script>",
            "javascript:alert(1)",
            "<img src=x onerror=alert(1)>",
            "<svg onload=alert(1)>",
            "';alert('xss');//"
        };

        for (String pattern : xssPatterns) {
            // when
            LoginRequest request = new LoginRequest(pattern, "password");
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then: 패턴 검증으로 인해 실패해야 함
            assertThat(violations).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Path Traversal 공격 패턴이 포함된 사용자명은 검증에 실패해야 한다")
    void pathTraversalAttempt_shouldFailValidation() {
        // given: Path Traversal 공격 시도
        String[] pathTraversalPatterns = {
            "../../../etc/passwd",
            "..\\..\\..\\windows\\system32",
            "%2e%2e%2f%2e%2e%2f%2e%2e%2f",
            "....//....//....//etc//passwd"
        };

        for (String pattern : pathTraversalPatterns) {
            // when
            LoginRequest request = new LoginRequest(pattern, "password");
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then: 패턴 검증으로 인해 실패해야 함
            assertThat(violations).isNotEmpty();
        }
    }

    // -----------------------------------------------------------------------
    // 다중 검증 오류 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("사용자명과 비밀번호 모두 잘못된 경우 다중 오류 반환")
    void multipleValidationErrors_shouldReturnAllErrors() {
        // given: 사용자명과 비밀번호 모두 잘못됨
        LoginRequest request = new LoginRequest("a", ""); // 너무 짧은 사용자명 + 빈 비밀번호

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then: 2개의 오류 발생
        assertThat(violations).hasSize(2);

        boolean hasUsernameError = violations.stream()
            .anyMatch(v -> v.getMessage().contains("3-50자 사이여야 합니다"));
        boolean hasPasswordError = violations.stream()
            .anyMatch(v -> v.getMessage().contains("비밀번호를 입력해주세요"));

        assertThat(hasUsernameError).isTrue();
        assertThat(hasPasswordError).isTrue();
    }
}
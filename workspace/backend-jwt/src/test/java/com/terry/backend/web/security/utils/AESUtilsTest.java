package com.terry.backend.web.security.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AESUtils 단위 테스트
 * - Spring Context 없이 순수 암/복호화 로직만 검증
 * - 개인정보(이름, 이메일, 전화번호, 주민번호 등) 암/복호화 시나리오 포함
 */
class AESUtilsTest {

    private static final String SECRET_KEY = "my-secret-key-for-personal-info";
    private static final String WRONG_KEY  = "wrong-key-for-testing-failure";

    // -----------------------------------------------------------------------
    // 기본 암/복호화
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("encrypt()")
    class Encrypt {

        @Test
        @DisplayName("암호화 결과는 원문과 달라야 한다")
        void encrypt_resultShouldDifferFromPlaintext() throws Exception {
            String plaintext = "홍길동";

            String encrypted = AESUtils.encrypt(plaintext, SECRET_KEY);

            System.out.println(encrypted);
            assertThat(encrypted).isNotEqualTo(plaintext);
        }

        @Test
        @DisplayName("같은 평문을 암호화해도 매번 결과가 달라야 한다 (랜덤 Salt/IV)")
        void encrypt_sameInputProducesDifferentCiphertext() throws Exception {
            String plaintext = "010-1234-5678";

            String first  = AESUtils.encrypt(plaintext, SECRET_KEY);
            String second = AESUtils.encrypt(plaintext, SECRET_KEY);

            assertThat(first).isNotEqualTo(second);
        }

        @Test
        @DisplayName("암호화 결과는 Base64 형식의 문자열이어야 한다")
        void encrypt_resultIsBase64Encoded() throws Exception {
            String encrypted = AESUtils.encrypt("test@example.com", SECRET_KEY);

            // Base64 문자셋 검증 (표준 Base64: A-Z, a-z, 0-9, +, /, =)
            assertThat(encrypted).matches("^[A-Za-z0-9+/=]+$");
        }
    }

    // -----------------------------------------------------------------------
    // 복호화 정확성
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("decrypt()")
    class Decrypt {

        @Test
        @DisplayName("암호화 후 복호화하면 원문이 복원되어야 한다")
        void encryptThenDecrypt_returnsOriginal() throws Exception {
            String plaintext = "홍길동";

            String encrypted = AESUtils.encrypt(plaintext, SECRET_KEY);
            String decrypted = AESUtils.decrypt(encrypted, SECRET_KEY);

            assertThat(decrypted).isEqualTo(plaintext);
        }

        @Test
        @DisplayName("잘못된 키로 복호화하면 예외가 발생해야 한다")
        void decrypt_wrongKey_throwsException() throws Exception {
            String encrypted = AESUtils.encrypt("비밀 데이터", SECRET_KEY);

            assertThatThrownBy(() -> AESUtils.decrypt(encrypted, WRONG_KEY))
                    .isInstanceOf(Exception.class);
        }
    }

    // -----------------------------------------------------------------------
    // 개인정보 유형별 시나리오
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("개인정보 암/복호화")
    class PersonalInfo {

        @Test
        @DisplayName("이름(한글) 암/복호화")
        void name_encryptAndDecrypt() throws Exception {
            String name = "김철수";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(name, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(name);
        }

        @Test
        @DisplayName("이메일 암/복호화")
        void email_encryptAndDecrypt() throws Exception {
            String email = "user@example.com";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(email, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(email);
        }

        @Test
        @DisplayName("전화번호 암/복호화")
        void phoneNumber_encryptAndDecrypt() throws Exception {
            String phone = "010-9876-5432";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(phone, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(phone);
        }

        @Test
        @DisplayName("주민등록번호 암/복호화")
        void residentRegistrationNumber_encryptAndDecrypt() throws Exception {
            String rrn = "900101-1234567";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(rrn, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(rrn);
        }

        @Test
        @DisplayName("주소(긴 문자열) 암/복호화")
        void address_longString_encryptAndDecrypt() throws Exception {
            String address = "서울특별시 강남구 테헤란로 123길 45, 67호 (역삼동, 행복아파트)";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(address, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(address);
        }

        @Test
        @DisplayName("특수문자 포함 데이터 암/복호화")
        void specialCharacters_encryptAndDecrypt() throws Exception {
            String data = "P@ssw0rd!#$%^&*()";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(data, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(data);
        }
    }

    // -----------------------------------------------------------------------
    // 경계값 / 엣지 케이스
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("빈 문자열 암/복호화")
        void emptyString_encryptAndDecrypt() throws Exception {
            String plaintext = "";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(plaintext, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(plaintext);
        }

        @Test
        @DisplayName("공백만 있는 문자열 암/복호화")
        void whitespaceOnly_encryptAndDecrypt() throws Exception {
            String plaintext = "   ";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(plaintext, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(plaintext);
        }

        @Test
        @DisplayName("영문·숫자·한글 혼합 암/복호화")
        void mixedCharacters_encryptAndDecrypt() throws Exception {
            String plaintext = "John홍길동123!@#";

            String decrypted = AESUtils.decrypt(AESUtils.encrypt(plaintext, SECRET_KEY), SECRET_KEY);

            assertThat(decrypted).isEqualTo(plaintext);
        }
    }
}

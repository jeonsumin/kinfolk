package com.terry.backend.web.security.service;

import com.terry.backend.core.messages.handler.ResponseMessages;
import com.terry.backend.core.security.dto.AuthorityDTO;
import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.thirdparty.JWT.DTO.TokenDTO;
import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.thirdparty.JWT.service.TokenBlacklistService;
import com.terry.backend.web.security.dto.RefreshTokenDTO;
import com.terry.backend.web.security.mapper.SecurityMapper;
import com.terry.backend.web.security.ratelimit.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;

/**
 * SecurityService.reissue() 단위 테스트
 * - SecurityMapper는 Mockito로 Mock 처리
 * - TokenProvider는 실제 구현체 사용 (테스트용 키)
 * - Spring Context 없이 순수 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceReissueTest {

    private static final String TEST_SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZS1vbmx5LTEyMzQ1Njc4OTAxMjM0NTY=";

    @Mock
    private SecurityMapper mapper;

    @Mock
    private TokenBlacklistService mockBlacklistService;

    @Mock
    private RateLimitService mockRateLimitService;

    private TokenProvider tokenProvider;
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider(TEST_SECRET, 1800L, 604800L, mockBlacklistService);

        // Rate limit service는 기본적으로 허용하도록 설정
        when(mockRateLimitService.isRefreshAllowed(anyString())).thenReturn(true);

        // isDebug = false, passwordEncoder는 reissue()에서 사용하지 않으므로 BCrypt 기본값으로 설정
        securityService = new SecurityService(
                mapper,
                tokenProvider,
                new BCryptPasswordEncoder(),
                mockRateLimitService,
                false
        );
    }

    private UserDTO buildTestUser() {
        AuthorityDTO authority = AuthorityDTO.builder()
                .authorityCode("ROLE_USER")
                .authority("ROLE_USER")
                .build();

        return UserDTO.builder()
                .id("user-001")
                .loginId("testuser")
                .password("encoded-password")
                .name("테스트 사용자")
                .use(true)
                .lock(false)
                .authorities(List.of(authority))
                .build();
    }

    // -----------------------------------------------------------------------
    // 정상 케이스
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("reissue: 유효한 Refresh Token이면 새 토큰 쌍을 반환하고 DB를 갱신해야 한다")
    void reissue_validRefreshToken_returnsNewTokenPair() throws Exception {
        // given
        UserDTO user = buildTestUser();
        TokenDTO originalTokenDTO = tokenProvider.generateTokenPair(user);
        String refreshToken = originalTokenDTO.getRefreshToken();

        RefreshTokenDTO storedToken = RefreshTokenDTO.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .expireDt(new Date(System.currentTimeMillis() + 604800000L))
                .build();

        when(mapper.findRefreshTokenByUserId(user.getId())).thenReturn(Optional.of(storedToken));
        when(mapper.findByUserId(user.getId())).thenReturn(Optional.of(user));
        when(mapper.selectUserAuthorities(user.getId())).thenReturn(user.getAuthorities());

        // when
        ResponseMessages result = securityService.reissue(refreshToken);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData()).containsKey("accessToken");
        assertThat(result.getData()).containsKey("refreshToken");
        assertThat(result.getData().get("accessToken")).isNotBlank();
        assertThat(result.getData().get("refreshToken")).isNotBlank();

        // 토큰이 정상적으로 반환되었는지 확인 (형식 검증)
        // JWT는 초 단위 타임스탬프를 사용하므로 동일 초 내 재발급 시 값이 같을 수 있음 — 구조만 검증
        assertThat(result.getData().get("accessToken")).matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");
        assertThat(result.getData().get("refreshToken")).matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");

        // DB upsert가 호출되었는지 확인
        verify(mapper, times(1)).upsertRefreshToken(any(RefreshTokenDTO.class));
    }

    @Test
    @DisplayName("reissue: 갱신된 Refresh Token이 DB에 올바른 userId로 저장되어야 한다")
    void reissue_validRefreshToken_savesNewRefreshTokenWithCorrectUserId() throws Exception {
        // given
        UserDTO user = buildTestUser();
        TokenDTO originalTokenDTO = tokenProvider.generateTokenPair(user);
        String refreshToken = originalTokenDTO.getRefreshToken();

        RefreshTokenDTO storedToken = RefreshTokenDTO.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .expireDt(new Date(System.currentTimeMillis() + 604800000L))
                .build();

        when(mapper.findRefreshTokenByUserId(user.getId())).thenReturn(Optional.of(storedToken));
        when(mapper.findByUserId(user.getId())).thenReturn(Optional.of(user));
        when(mapper.selectUserAuthorities(user.getId())).thenReturn(user.getAuthorities());

        // when
        securityService.reissue(refreshToken);

        // then: upsertRefreshToken 호출 시 userId가 올바른지 검증
        ArgumentCaptor<RefreshTokenDTO> captor = ArgumentCaptor.forClass(RefreshTokenDTO.class);
        verify(mapper).upsertRefreshToken(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(user.getId());
        assertThat(captor.getValue().getRefreshToken()).isNotBlank();
    }

    // -----------------------------------------------------------------------
    // 실패 케이스 - 유효하지 않은 토큰
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("reissue: 유효하지 않은 토큰이면 실패 응답을 반환해야 한다")
    void reissue_invalidToken_returnsFailResponse() throws Exception {
        // when
        ResponseMessages result = securityService.reissue("invalid.jwt.token");

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("유효하지 않은 Refresh Token");

        // Mapper는 전혀 호출되지 않아야 함
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("reissue: 만료된 토큰이면 실패 응답을 반환해야 한다")
    void reissue_expiredToken_returnsFailResponse() throws Exception {
        // given: 만료 시간 0초로 설정한 provider로 토큰 생성
        TokenProvider shortLivedProvider = new TokenProvider(TEST_SECRET, 0L, 0L, mockBlacklistService);
        UserDTO user = buildTestUser();
        TokenDTO expiredTokenDTO = shortLivedProvider.generateTokenPair(user);

        // when
        ResponseMessages result = securityService.reissue(expiredTokenDTO.getRefreshToken());

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("유효하지 않은 Refresh Token");

        verifyNoInteractions(mapper);
    }

    // -----------------------------------------------------------------------
    // 실패 케이스 - DB 불일치
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("reissue: DB에 저장된 토큰과 다른 토큰이면 실패 응답을 반환해야 한다")
    void reissue_tokenMismatch_returnsFailResponse() throws Exception {
        // given: 유효한 토큰이지만 DB에는 다른 문자열이 저장되어 있음
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);
        String refreshToken = tokenDTO.getRefreshToken();

        // DB에는 의도적으로 다른 값(suffix를 추가한 변형 토큰)이 저장된 상태
        // 단순 suffix 추가는 파싱 시 예외를 유발하므로, 다른 userId로 생성한 토큰을 DB에 저장
        UserDTO otherUser = UserDTO.builder()
                .id("user-999")
                .loginId("otheruser")
                .password("pw")
                .name("다른 사용자")
                .use(true)
                .lock(false)
                .authorities(buildTestUser().getAuthorities())
                .build();
        TokenDTO otherTokenDTO = tokenProvider.generateTokenPair(otherUser);

        RefreshTokenDTO storedToken = RefreshTokenDTO.builder()
                .userId(user.getId())
                .refreshToken(otherTokenDTO.getRefreshToken()) // 다른 유저의 토큰이 DB에 저장
                .expireDt(new Date(System.currentTimeMillis() + 604800000L))
                .build();

        when(mapper.findRefreshTokenByUserId(user.getId())).thenReturn(Optional.of(storedToken));

        // when
        ResponseMessages result = securityService.reissue(refreshToken);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("Refresh Token이 일치하지 않습니다");

        verify(mapper, never()).upsertRefreshToken(any());
    }

    @Test
    @DisplayName("reissue: DB에 해당 userId의 토큰이 없으면 실패 응답을 반환해야 한다")
    void reissue_noTokenInDb_returnsFailResponse() throws Exception {
        // given
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        when(mapper.findRefreshTokenByUserId(user.getId())).thenReturn(Optional.empty());

        // when
        ResponseMessages result = securityService.reissue(tokenDTO.getRefreshToken());

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("Refresh Token이 일치하지 않습니다");

        verify(mapper, never()).upsertRefreshToken(any());
    }

    @Test
    @DisplayName("reissue: DB에 사용자가 존재하지 않으면 실패 응답을 반환해야 한다")
    void reissue_userNotFound_returnsFailResponse() throws Exception {
        // given
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);
        String refreshToken = tokenDTO.getRefreshToken();

        RefreshTokenDTO storedToken = RefreshTokenDTO.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .expireDt(new Date(System.currentTimeMillis() + 604800000L))
                .build();

        when(mapper.findRefreshTokenByUserId(user.getId())).thenReturn(Optional.of(storedToken));
        when(mapper.findByUserId(user.getId())).thenReturn(Optional.empty()); // 사용자 없음

        // when
        ResponseMessages result = securityService.reissue(refreshToken);

        // then
        assertThat(result.isSuccess()).isFalse();
        verify(mapper, never()).upsertRefreshToken(any());
    }
}

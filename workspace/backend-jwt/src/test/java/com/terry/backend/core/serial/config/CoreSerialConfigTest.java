package com.terry.backend.core.serial.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class CoreSerialConfigTest {

    private CoreSerialConfig config;

    @BeforeEach
    void setUp() {
        config = new CoreSerialConfig();
    }

    @Test
    @DisplayName("기본 dateFormat은 yyyyMMddHHmm이다")
    void defaultDateFormat() {
        assertThat(config.getDateFormat()).isEqualTo("yyyyMMddHHmm");
    }

    @Test
    @DisplayName("기본 compareDateFormat은 yyyyMMddHHmm이다")
    void defaultCompareDateFormat() {
        assertThat(config.getCompareDateFormat()).isEqualTo("yyyyMMddHHmm");
    }

    @Test
    @DisplayName("getSerial: 날짜 접두사(12자리) + 6자리 숫자로 총 18자리를 반환한다")
    void getSerial_returnsEighteenCharString() {
        String serial = config.getSerial(1);

        assertThat(serial).hasSize(18);
    }

    @Test
    @DisplayName("getSerial: 첫 12자리는 현재 시각(yyyyMMddHHmm)과 일치한다")
    void getSerial_datePrefix_matchesCurrentTime() throws Exception {
        String expectedPrefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

        String serial = config.getSerial(1);

        assertThat(serial).startsWith(expectedPrefix);
    }

    @Test
    @DisplayName("getSerial: 뒤 6자리는 value가 6자리 zero-padding된 숫자이다")
    void getSerial_suffix_isZeroPaddedValue() {
        assertThat(config.getSerial(1)).endsWith("000001");
        assertThat(config.getSerial(100)).endsWith("000100");
        assertThat(config.getSerial(999999)).endsWith("999999");
    }

    @Test
    @DisplayName("getSerial: value가 달라지면 숫자 부분도 달라진다")
    void getSerial_differentValues_produceDifferentSuffixes() {
        String serial1 = config.getSerial(1);
        String serial2 = config.getSerial(2);

        assertThat(serial1).isNotEqualTo(serial2);
        assertThat(serial1.substring(12)).isNotEqualTo(serial2.substring(12));
    }

    @Test
    @DisplayName("getSerial: 연속 호출 시 날짜 접두사는 동일하다")
    void getSerial_consecutiveCalls_sameDatePrefix() {
        String serial1 = config.getSerial(1);
        String serial2 = config.getSerial(2);

        assertThat(serial1.substring(0, 12)).isEqualTo(serial2.substring(0, 12));
    }
}

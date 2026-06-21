package com.terry.backend.core.serial.service;

import com.terry.backend.core.serial.config.CoreSerialConfig;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.dto.SerialDTO;
import com.terry.backend.core.serial.mapper.CoreSerialMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreSerialServiceTest {

    @InjectMocks
    private CoreSerialService service;

    @Mock
    private CoreSerialMapper mapper;

    @Mock
    private CoreSerialConfig config;

    /** 날짜 비교 포함 단순 테스트용 config */
    private final SerialConfiguration<String> testConfig = new SerialConfiguration<>() {
        @Override public String getSerial(Integer value)       { return "SERIAL_" + value; }
        @Override public String getDateFormat()                { return "yyyyMMdd"; }
        @Override public String getCompareDateFormat()         { return "yyyyMMdd"; }
        @Override public String getFormatString()              { return "%s%%04d"; }
    };

    /** compareDateFormat 없는 테스트용 config (날짜 리셋 없음) */
    private final SerialConfiguration<String> noDateConfig = new SerialConfiguration<>() {
        @Override public String getSerial(Integer value)       { return "SERIAL_" + value; }
        @Override public String getDateFormat()                { return "yyyyMMdd"; }
        @Override public String getCompareDateFormat()         { return ""; }
        @Override public String getFormatString()              { return "%s%%04d"; }
    };

    @AfterEach
    void resetRunFlag() throws Exception {
        Field runField = CoreSerialService.class.getDeclaredField("run");
        runField.setAccessible(true);
        runField.set(null, false);
    }

    // -----------------------------------------------------------------------
    // 신규 생성 (DB 레코드 없음)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("신규 생성: DB에 레코드가 없으면 1번부터 시작하는 시리얼을 1개 반환한다")
    void getSerial_noRecord_startsFromOne() throws Exception {
        when(mapper.findOne(any())).thenReturn(Optional.empty());

        List<Object> result = service.getSerial("TEST_ID", 1, testConfig);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("SERIAL_1");
    }

    @Test
    @DisplayName("신규 생성: value=3이면 1,2,3 순서로 3개를 반환한다")
    void getSerial_noRecord_returnsMultipleSerials() throws Exception {
        when(mapper.findOne(any())).thenReturn(Optional.empty());

        List<Object> result = service.getSerial("TEST_ID", 3, testConfig);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("SERIAL_1", "SERIAL_2", "SERIAL_3");
    }

    @Test
    @DisplayName("신규 생성: 생성 후 DB에 저장한다")
    void getSerial_noRecord_savesToDatabase() throws Exception {
        when(mapper.findOne(any())).thenReturn(Optional.empty());
        ArgumentCaptor<SerialDTO> captor = ArgumentCaptor.forClass(SerialDTO.class);

        service.getSerial("TEST_ID", 3, testConfig);

        verify(mapper).save(captor.capture());
        SerialDTO saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo("TEST_ID");
        assertThat(saved.getValue()).isEqualTo(4); // value + 1
        assertThat(saved.getDatetime()).isNotNull();
    }

    // -----------------------------------------------------------------------
    // 연속 생성 (같은 날짜의 기존 레코드 존재)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("연속 생성: 같은 날짜 레코드가 있으면 기존 카운터에서 이어서 생성한다")
    void getSerial_existingRecordSameDate_incrementsFromCurrent() throws Exception {
        SerialDTO existing = new SerialDTO("TEST_ID", 5, new Date());
        when(mapper.findOne(eq("TEST_ID"))).thenReturn(Optional.of(existing));

        List<Object> result = service.getSerial("TEST_ID", 2, testConfig);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("SERIAL_5", "SERIAL_6");
    }

    @Test
    @DisplayName("연속 생성: 기존 카운터 값이 저장된 값과 정확히 이어진다")
    void getSerial_existingRecordSameDate_savesIncrementedValue() throws Exception {
        SerialDTO existing = new SerialDTO("TEST_ID", 5, new Date());
        when(mapper.findOne(eq("TEST_ID"))).thenReturn(Optional.of(existing));
        ArgumentCaptor<SerialDTO> captor = ArgumentCaptor.forClass(SerialDTO.class);

        service.getSerial("TEST_ID", 2, testConfig);

        verify(mapper).save(captor.capture());
        assertThat(captor.getValue().getValue()).isEqualTo(7); // 5 + 2
    }

    // -----------------------------------------------------------------------
    // 날짜 변경 시 카운터 초기화
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("날짜 리셋: 기존 레코드의 날짜가 다르면 카운터를 1로 초기화하고 생성한다")
    void getSerial_existingRecordDifferentDate_resetsCounter() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SerialDTO existing = new SerialDTO("TEST_ID", 99, cal.getTime());
        when(mapper.findOne(eq("TEST_ID"))).thenReturn(Optional.of(existing));

        List<Object> result = service.getSerial("TEST_ID", 2, testConfig);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("SERIAL_1", "SERIAL_2");
    }

    @Test
    @DisplayName("날짜 리셋: 초기화 후 DB에 새로운 날짜와 리셋된 카운터로 저장한다")
    void getSerial_existingRecordDifferentDate_savesResetEntity() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SerialDTO existing = new SerialDTO("TEST_ID", 99, cal.getTime());
        when(mapper.findOne(eq("TEST_ID"))).thenReturn(Optional.of(existing));
        ArgumentCaptor<SerialDTO> captor = ArgumentCaptor.forClass(SerialDTO.class);

        service.getSerial("TEST_ID", 2, testConfig);

        verify(mapper).save(captor.capture());
        SerialDTO saved = captor.getValue();
        assertThat(saved.getValue()).isEqualTo(3); // value + 1
        assertThat(saved.getDatetime()).isAfter(cal.getTime());
    }

    // -----------------------------------------------------------------------
    // compareDateFormat 미설정 (날짜 비교 없음)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("compareDateFormat 미설정: 날짜가 달라도 기존 카운터에서 이어서 생성한다")
    void getSerial_noCompareDateFormat_alwaysIncrements() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SerialDTO existing = new SerialDTO("TEST_ID", 10, cal.getTime());
        when(mapper.findOne(any())).thenReturn(Optional.of(existing));

        List<Object> result = service.getSerial("TEST_ID", 1, noDateConfig);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("SERIAL_10");
    }

    // -----------------------------------------------------------------------
    // 기본 설정 (configuration=null → CoreSerialConfig 사용)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("configuration=null이면 CoreSerialConfig를 사용한다")
    void getSerial_nullConfiguration_usesCoreSerialConfig() throws Exception {
        when(mapper.findOne(any())).thenReturn(Optional.empty());
        when(config.getSerial(1)).thenReturn("DEFAULT_001");

        List<Object> result = service.getSerial("TEST_ID", 1, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("DEFAULT_001");
    }
}

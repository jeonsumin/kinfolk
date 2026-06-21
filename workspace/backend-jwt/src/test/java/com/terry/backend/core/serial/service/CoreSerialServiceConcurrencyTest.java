package com.terry.backend.core.serial.service;

import com.terry.backend.api.admin.system.code.strategy.CodeStrategy;
import com.terry.backend.core.serial.config.CoreSerialConfig;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.dto.SerialDTO;
import com.terry.backend.core.serial.mapper.CoreSerialMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * CoreSerialService 동시 접근 / run 플래그 관련 테스트
 *
 * 검증 항목
 * 1. 동시에 N개 요청이 들어와도 에러 없이 유니크한 시리얼이 생성되어야 한다.
 * 2. 예외 발생 시 run 플래그가 true로 고착되어 이후 요청이 차단된다. (현재 버그)
 * 3. CodeStrategy 분 단위 리셋 - 같은 분 내 연속 호출은 카운터를 이어받아야 한다.
 * 4. CodeStrategy 분 단위 리셋 - 분이 바뀌면 카운터가 1로 초기화되어야 한다.
 */
@ExtendWith(MockitoExtension.class)
class CoreSerialServiceConcurrencyTest {

    @InjectMocks
    private CoreSerialService service;

    @Mock
    private CoreSerialMapper mapper;

    @Mock
    private CoreSerialConfig config;

    /** CodeStrategy: yyyyMMddHHmm 기준 분 단위 리셋 */
    private final SerialConfiguration<String> codeStrategy = new CodeStrategy();

    @AfterEach
    void resetRunFlag() throws Exception {
        setRunFlag(false);
    }

    // -----------------------------------------------------------------------
    // 동시 접근 테스트
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("동시 접근")
    class ConcurrentAccess {

        @Test
        @DisplayName("동시에 10개 요청이 들어와도 에러 없이 모두 완료되어야 한다")
        void concurrent_tenRequests_allCompleteWithoutError() throws Exception {
            int threadCount = 10;

            // DB 상태를 인메모리로 시뮬레이션 (단일 스레드로 직렬화되므로 AtomicReference 충분)
            AtomicReference<SerialDTO> dbState = new AtomicReference<>(null);
            Object dbLock = new Object();

            when(mapper.findOne(any())).thenAnswer(inv -> {
                synchronized (dbLock) {
                    SerialDTO current = dbState.get();
                    return current == null
                            ? Optional.empty()
                            : Optional.of(new SerialDTO(current.getId(), current.getValue(), current.getDatetime()));
                }
            });
            doAnswer(inv -> {
                synchronized (dbLock) {
                    dbState.set(inv.getArgument(0));
                }
                return null;
            }).when(mapper).save(any());

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            List<Object> allResults = Collections.synchronizedList(new ArrayList<>());
            List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startGate.await();
                        List<Object> serials = service.getSerial("CODE", 1, codeStrategy);
                        allResults.addAll(serials);
                    } catch (Exception e) {
                        errors.add(e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown(); // 모든 스레드 동시 출발
            boolean finished = doneLatch.await(15, TimeUnit.SECONDS);
            executor.shutdown();

            assertThat(finished)
                    .as("15초 이내에 모든 스레드가 완료되어야 한다")
                    .isTrue();
            assertThat(errors)
                    .as("예외 없이 처리되어야 한다")
                    .isEmpty();
            assertThat(allResults)
                    .as("10개의 시리얼이 생성되어야 한다")
                    .hasSize(threadCount);
            assertThat(new HashSet<>(allResults))
                    .as("생성된 시리얼은 모두 유니크해야 한다 (중복 없음)")
                    .hasSize(threadCount);
        }

        @Test
        @DisplayName("동시에 생성된 시리얼은 순차적으로 증가해야 한다")
        void concurrent_tenRequests_serialsAreSequential() throws Exception {
            int threadCount = 5;

            AtomicReference<SerialDTO> dbState = new AtomicReference<>(null);
            Object dbLock = new Object();

            when(mapper.findOne(any())).thenAnswer(inv -> {
                synchronized (dbLock) {
                    SerialDTO current = dbState.get();
                    return current == null
                            ? Optional.empty()
                            : Optional.of(new SerialDTO(current.getId(), current.getValue(), current.getDatetime()));
                }
            });
            doAnswer(inv -> {
                synchronized (dbLock) {
                    dbState.set(inv.getArgument(0));
                }
                return null;
            }).when(mapper).save(any());

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            List<Object> allResults = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startGate.await();
                        allResults.addAll(service.getSerial("CODE", 1, codeStrategy));
                    } catch (Exception ignored) {
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            doneLatch.await(15, TimeUnit.SECONDS);
            executor.shutdown();

            // 시리얼에서 숫자 부분만 추출하여 연속성 확인
            // 예: CODE202603291234_0001 → 1
            List<Integer> numbers = allResults.stream()
                    .map(s -> {
                        String str = (String) s;
                        return Integer.parseInt(str.substring(str.length() - 4));
                    })
                    .sorted()
                    .toList();

            assertThat(numbers).doesNotHaveDuplicates();
            // 1부터 threadCount까지 연속해야 함
            for (int i = 0; i < numbers.size(); i++) {
                assertThat(numbers.get(i)).isEqualTo(i + 1);
            }
        }
    }

    // -----------------------------------------------------------------------
    // run 플래그 고착 버그 테스트
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("run 플래그 고착 버그")
    class RunFlagLeakBug {

        @Test
        @DisplayName("[버그] mapper.save()에서 예외 발생 시 run 플래그가 true로 남는다")
        void afterException_runFlagRemainsTrue() throws Exception {
            when(mapper.findOne(any())).thenReturn(Optional.empty());
            // save() 호출 시 예외 발생 시뮬레이션
            doAnswer(inv -> { throw new RuntimeException("DB 저장 실패"); })
                    .when(mapper).save(any());

            try {
                service.getSerial("CODE", 1, codeStrategy);
            } catch (Exception ignored) {
                // 예외 발생 예상
            }

            // run 플래그가 true로 고착되었는지 확인
            boolean runFlag = getRunFlag();
            assertThat(runFlag)
                    .as("[버그 확인] 예외 발생 후 run 플래그가 true로 남아있다 → 이후 모든 요청이 차단된다")
                    .isTrue();
        }

        @Test
        @DisplayName("[버그] run 플래그 고착 후 이후 요청이 무한 대기에 빠진다")
        void afterRunFlagLeaks_subsequentCallBlocks() throws Exception {
            // run 플래그를 강제로 true로 설정 (예외 발생 후 고착 상태 시뮬레이션)
            setRunFlag(true);

            // 다음 요청은 while(run) 루프에서 빠져나오지 못해야 한다
            assertTimeoutPreemptively(
                    java.time.Duration.ofSeconds(2),
                    () -> {
                        // 별도 스레드에서 호출 - 2초 이내에 완료되지 않으면 차단 상태임을 확인
                        Future<?> future = Executors.newSingleThreadExecutor()
                                .submit(() -> {
                                    try {
                                        service.getSerial("CODE", 1, codeStrategy);
                                    } catch (Exception ignored) {
                                    }
                                });

                        try {
                            future.get(1, TimeUnit.SECONDS);
                            // 1초 이내에 완료되었다면 버그가 없는 것 (또는 플래그가 초기화된 것)
                        } catch (TimeoutException e) {
                            // 타임아웃 = run 플래그 고착으로 요청이 차단됨 → 버그 확인됨
                            future.cancel(true);
                            throw new AssertionError(
                                    "[버그 확인] run 플래그 고착으로 후속 요청이 1초 이상 차단됨. " +
                                    "예외 발생 시 run 플래그를 finally 블록에서 초기화해야 한다.");
                        }
                    },
                    "타임아웃 검사 자체가 2초 초과 시 실패"
            );
        }

        @Test
        @DisplayName("run 플래그가 false 상태라면 후속 요청이 즉시 처리된다 (정상 흐름 검증)")
        void whenRunFlagIsFalse_nextCallProceedsImmediately() throws Exception {
            when(mapper.findOne(any())).thenReturn(Optional.empty());
            doAnswer(inv -> null).when(mapper).save(any());

            // run 플래그가 false인 상태에서 요청 → 바로 처리되어야 함
            List<Object> result = service.getSerial("CODE", 1, codeStrategy);

            assertThat(result).hasSize(1);
            assertThat(getRunFlag())
                    .as("정상 처리 후 run 플래그는 false로 복원되어야 한다")
                    .isFalse();
        }
    }

    // -----------------------------------------------------------------------
    // CodeStrategy 분 단위 리셋 시나리오
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("CodeStrategy - 분 단위 카운터 리셋")
    class CodeStrategyMinuteReset {

        @Test
        @DisplayName("같은 분(minute) 내 연속 호출이면 카운터를 이어받아야 한다")
        void sameMinute_counterContinues() throws Exception {
            // 현재 시각과 같은 분의 기존 레코드 (값 = 5)
            SerialDTO existing = new SerialDTO("CODE", 5, new Date());
            when(mapper.findOne(any())).thenReturn(Optional.of(existing));
            doAnswer(inv -> null).when(mapper).save(any());

            List<Object> result = service.getSerial("CODE", 1, codeStrategy);

            assertThat(result).hasSize(1);
            // 이전 값(5)에서 이어받아 CODE...0005 를 반환해야 함
            String serial = (String) result.get(0);
            assertThat(serial).endsWith("0005");
        }

        @Test
        @DisplayName("분이 바뀌면 카운터가 0001부터 다시 시작해야 한다")
        void differentMinute_counterResetsToOne() throws Exception {
            // 1분 전 레코드 (값 = 99)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            SerialDTO oldRecord = new SerialDTO("CODE", 99, cal.getTime());
            when(mapper.findOne(any())).thenReturn(Optional.of(oldRecord));
            doAnswer(inv -> null).when(mapper).save(any());

            List<Object> result = service.getSerial("CODE", 1, codeStrategy);

            assertThat(result).hasSize(1);
            String serial = (String) result.get(0);
            assertThat(serial).endsWith("0001");
        }

        @Test
        @DisplayName("생성된 CODE 시리얼은 'CODE' 접두어와 날짜(분까지) + 4자리 순번 형식이어야 한다")
        void generatedSerial_hasCorrectFormat() throws Exception {
            when(mapper.findOne(any())).thenReturn(Optional.empty());
            doAnswer(inv -> null).when(mapper).save(any());

            List<Object> result = service.getSerial("CODE", 1, codeStrategy);

            String serial = (String) result.get(0);
            // 형식: CODE + yyyyMMddHHmm (12자리) + 4자리 숫자
            // 예: CODE2026032912340001
            assertThat(serial)
                    .as("'CODE'로 시작해야 한다")
                    .startsWith("CODE");
            assertThat(serial)
                    .as("총 길이는 CODE(4) + 날짜분(12) + 순번(4) = 20자리여야 한다")
                    .hasSize(20);
            assertThat(serial.substring(serial.length() - 4))
                    .as("첫 번째 시리얼의 순번은 0001이어야 한다")
                    .isEqualTo("0001");
        }
    }

    // -----------------------------------------------------------------------
    // 헬퍼 메서드 (run 플래그 리플렉션 접근)
    // -----------------------------------------------------------------------

    private void setRunFlag(boolean value) throws Exception {
        Field runField = CoreSerialService.class.getDeclaredField("run");
        runField.setAccessible(true);
        runField.set(null, value);
    }

    private boolean getRunFlag() throws Exception {
        Field runField = CoreSerialService.class.getDeclaredField("run");
        runField.setAccessible(true);
        return (boolean) runField.get(null);
    }
}

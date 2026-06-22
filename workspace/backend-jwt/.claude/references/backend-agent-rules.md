# Backend Agent 프로젝트 특화 룰

이 파일은 backend-agent가 신규 기능 구현 또는 기존 코드 수정 시 반드시 참고할 **프로젝트 관찰 기반 코드 컨벤션**이다.
STYLE_GUIDE.md와 함께 읽어야 한다. 이 파일이 동일 항목에서 더 구체적인 규칙을 제공하면 이 파일을 우선한다.

---

## 1. 파일 생성 순서 및 체크리스트 (신규 API 기능)

신규 도메인 기능을 추가할 때 아래 순서로 파일을 생성한다.

```
1. dto/          → DTO 클래스 + Enum (도메인 데이터 구조 먼저 확정)
2. mapper/       → Java Mapper 인터페이스
3. mappers/...   → XML Mapper (namespace = Mapper 인터페이스 FQCN)
4. exception/    → 도메인 예외 (필요한 경우)
5. service/      → Service (트랜잭션 경계 여기에만)
6. controller/   → Controller (마지막에 HTTP 바인딩)
```

체크리스트:
- [ ] DTO 필드명이 lowerCamelCase이고 DB 컬럼명이 아닌가?
- [ ] Mapper 인터페이스 메서드명과 XML statement id가 정확히 일치하는가?
- [ ] XML namespace가 Mapper 인터페이스의 완전 수식 이름(FQCN)인가?
- [ ] 다중 스칼라 파라미터는 `@Param`으로 명시했는가?
- [ ] 1:N 관계 조회는 `<collection>`을 사용하고 N+1이 발생하지 않는가?
- [ ] Enum 필드를 DB에 저장·조회할 때 `typeHandler`를 지정했는가?
- [ ] `<foreach>`로 IN 절을 구성할 때 null/empty guard(`userIds != null and userIds.size() > 0`)를 추가했는가?
- [ ] 쓰기 Service 메서드에 `@Transactional`이 있는가?

---

## 2. Enum 설계 패턴

이 프로젝트에는 두 가지 Enum 패턴이 공존한다.

### 패턴 A: 단순 상태 코드 Enum (신규 도메인 권장)
```java
// 파일 위치: api/<feature>/dto/<EnumName>.java
// 관련 DTO와 같은 dto 패키지에 둔다
public enum AttendeesType {
    PENDING,
    ACCEPTED,
    DECLINED,
    TENTATIVE
}
```
- 코드 필드나 유틸 메서드가 없는 순수 상태 열거형
- DB 저장/조회 시 XML에서 `typeHandler=org.apache.ibatis.type.EnumToStringTypeHandler` 지정

### 패턴 B: 코드 값과 유틸 메서드를 가진 Enum (기존 admin 도메인)
```java
// 관련 패키지 최상위(dto 아님)에 단독 파일로 위치할 수 있음
public enum MenuType {
    DIR("MenuType.DIR"),
    PAGE("MenuType.PAGE");

    private String code;

    private MenuType(String code) { this.code = code; }

    public static List<CodeDTO> toCodeList() { ... }
}
```
- 코드 API 응답으로 노출해야 하는 Enum에 사용

**결정 기준**: 해당 Enum 값을 프론트엔드 코드 목록 API로 노출할 필요가 있으면 패턴 B, 단순 내부 상태 표현이면 패턴 A.

---

## 3. DTO 계층 구조

### BaseDTO를 상속하는 경우
감사 필드(`createDt`, `createId`, `updateDt`, `updateId`)가 DB 테이블에 `REGIST_DT`, `REGIST_ID`, `UPDT_DT`, `UPDT_ID`로 있고 API 응답에 포함되는 경우.

```java
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SomeDomainDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;
    // 도메인 필드
}
```

### BaseDTO를 상속하지 않는 경우
- 도메인 테이블에 표준 감사 컬럼이 없거나
- 참조 테이블(예: `EVENT_ATTENDEES`)처럼 감사 컬럼이 다른 이름이거나
- 단순 집계/join 결과 DTO인 경우

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EventAttendeesDTO {
    private static final long serialVersionUID = 1L;
    // 도메인 필드
}
```

주의: BaseDTO를 상속하지 않아도 `private static final long serialVersionUID = 1L;`은 선언한다. 단, `Serializable` implements가 없으면 serialVersionUID 선언은 컴파일 경고 대상이 될 수 있다. 기존 CalendarDTO/EventAttendeesDTO 패턴에서는 선언만 되어 있으므로 동일 도메인 신규 DTO는 이 패턴을 따른다.

### 1:N 관계를 담는 DTO
부모 DTO 내부에 자식 DTO `List` 필드를 선언한다.

```java
public class CalendarDTO {
    // ...
    private List<EventAttendeesDTO> attendees; // 참석자 목록 (1:N 관계)
}
```
XML에서 `<collection>`으로 매핑한다 (섹션 5 참조).

---

## 4. Mapper 인터페이스 패턴

### @Param 표기 스타일
이 프로젝트에는 두 스타일이 공존한다:
- 기존 admin 도메인: `@Param(value = "id")`
- 신규 calendar 도메인: `@Param("workspaceId")`

한 인터페이스 안에서는 한 스타일만 사용한다. 신규 인터페이스는 `@Param("paramName")` 형식을 기본으로 한다.

### 다중 스칼라 파라미터 + List 파라미터 혼합
```java
List<CalendarDTO> selectWorkspaceEventsByMonth(
        @Param("workspaceId") String workspaceId,
        @Param("userIds") List<String> userIds,
        @Param("startOfMonth") String startOfMonth,
        @Param("endOfMonth") String endOfMonth
);
```
List 파라미터도 `@Param`으로 명시하고 XML에서 `<foreach>`로 처리한다.

### Mapper Javadoc
공개 조회 메서드가 여러 개일 때 번호와 한국어 설명을 포함한 Javadoc을 작성한다.
```java
/**
 * 1. 워크스페이스 팀원 월간 조회
 * @param workspaceId 워크스페이스 ID
 * @param userIds     필터링할 유저 ID 목록 (null이거나 비어있으면 전체조회)
 */
List<CalendarDTO> selectWorkspaceEventsByMonth(...);
```

### void 반환 관례
변경(insert, update, delete) 메서드는 `void`를 반환한다.
```java
void insertEvent(CalendarDTO event);
void updateEvent();   // 파라미터 없이 stub 상태도 허용 (추후 완성)
```

---

## 5. XML Mapper 패턴

### 1:N 관계 resultMap (`<collection>`)
부모-자식 1:N 관계는 JOIN으로 한 번에 조회하고 `<collection>`으로 매핑한다. N+1은 허용하지 않는다.

```xml
<resultMap id="eventDetailMap" type="com.terry.backend.api.calendar.dto.CalendarDTO">
    <id property="id" column="ID" />
    <result property="userId" column="USER_ID" />
    <!-- 부모 컬럼들... -->

    <!-- 조인으로 가져온 자식 리스트 매핑 -->
    <collection property="attendees" ofType="com.terry.backend.api.calendar.dto.EventAttendeesDTO">
        <result property="eventId" column="ATTENDEE_EVENT_ID"/>
        <result property="userId" column="ATTENDEE_USER_ID"/>
        <result property="status" column="STATUS"/>
    </collection>
</resultMap>
```

JOIN 시 컬럼 충돌을 피하기 위해 자식 테이블 컬럼에 `AS ATTENDEE_xxx` 별칭을 붙인다.

### Enum 타입 핸들러 (inline 방식)
```xml
<!-- INSERT/UPDATE에서 Enum → 문자열 변환 -->
<insert id="insertAttendee">
    INSERT INTO EVENT_ATTENDEES (EVENT_ID, USER_ID, STATUS)
    VALUES (#{eventId},
            #{userId},
            #{status, typeHandler=org.apache.ibatis.type.EnumToStringTypeHandler})
</insert>
```
`<result>` 태그에서는 typeHandler 생략 가능 (MyBatis 기본 EnumTypeHandler 동작). 단, 명시적으로 문자열로 저장한 경우 `typeHandler=org.apache.ibatis.type.EnumToStringTypeHandler`를 `<result>` 태그에도 지정한다.

### `<foreach>` — List IN 절
```xml
<if test="userIds != null and userIds.size() > 0">
    AND wu.USER_ID IN
    <foreach collection="userIds" item="uid" open="(" separator="," close=")">
        #{uid}
    </foreach>
</if>
```
항상 null/empty guard를 먼저 검사한다.

### CDATA 사용 범위
`<![CDATA[...]]>`는 비교 연산자(`<`, `>`, `<=`, `>=`) 또는 `ORDER BY` 절이 포함된 블록에 사용한다.
**한 statement 안에서 CDATA와 비-CDATA를 섞지 않는다.** 일부 statement에서는 CDATA 내부에 `<if>` 태그를 포함할 수 없으므로, 조건절(`<if>`, `<where>`, `<foreach>`)이 필요한 부분은 CDATA 밖에, 범위 비교 및 정렬은 CDATA 안에 배치한다.

```xml
<select id="selectWorkspaceEventsByMonth" resultMap="eventDetailMap">
    SELECT ...
    FROM ...
    WHERE
        e.USER_ID IN (
            SELECT wu.USER_ID FROM WORKSPACE_USER wu
            WHERE wu.WS_ID = #{workspaceId}
            <if test="userIds != null and userIds.size() > 0">
                AND wu.USER_ID IN
                <foreach ...>#{uid}</foreach>
            </if>
        )
    <![CDATA[
    AND e.START_DT >= #{endOfMonth}
    AND e.END_DT >= #{startOfMonth}
    ORDER BY e.START_DT ASC, e.IS_ALL_DAY DESC
    ]]>
</select>
```

### SQL 백틱 예약어 처리
MySQL 예약어 컬럼(예: `DESC`)은 백틱으로 감싼다: `` `DESC` ``.

### INSERT 포맷
컬럼과 값이 많을 때 컬럼 목록은 여는 괄호 뒤에 줄 단위로 나열하고, VALUES 블록도 동일한 정렬을 따른다.

```xml
<insert id="insertEvent">
    INSERT INTO CALENDAR_EVENTS ( ID
                                , USER_ID
                                , TITLE)
    VALUES ( #{id}
           , #{userId}
           , #{title})
</insert>
```

---

## 6. ResultMapper.xml 구조

이 프로젝트에는 두 개의 ResultMapper.xml이 있다.

| 파일 | namespace | 용도 |
|---|---|---|
| `mappers/core/ResultMapper.xml` | `core` | 프로젝트 전체 공통 resultMap (Code, Base, File) |
| `mappers/api/ResultMapper.xml` | `core` (현재 비어 있음) | api 도메인 전용 공통 resultMap 예비 위치 |

- 다수 도메인에서 재사용되는 resultMap은 `mappers/core/ResultMapper.xml`에 `<resultMap id="...">` 형태로 추가하고, 다른 XML에서 `resultMap="core.ResultMapId"`로 참조한다.
- 단일 도메인 전용 resultMap은 해당 도메인 XML 파일에 직접 선언한다 (예: `CalendarMapper.xml`의 `eventDetailMap`).
- `mappers/api/ResultMapper.xml`에 추가할 때는 namespace를 `core`에서 `api`처럼 구분된 이름으로 변경하는 것을 먼저 팀에서 결정한다. 현재는 비어 있으므로 기존 관례 확인 없이 채우지 않는다.

---

## 7. 패키지 배치 실제 예시 (calendar 도메인 기준)

```
api/calendar/
    dto/
        AttendeesType.java       ← 단순 상태 Enum
        CalendarDTO.java         ← 이벤트 메인 DTO (List<EventAttendeesDTO> 포함)
        EventAttendeesDTO.java   ← 참석자 DTO
    mapper/
        CalendarMapper.java      ← @Mapper 인터페이스

mappers/api/
    ResultMapper.xml             ← api 공통 resultMap (현재 placeholder)
    calendar/
        CalendarMapper.xml       ← namespace = CalendarMapper FQCN
```

신규 api 도메인은 `api/<feature>/` 아래 동일 구조로 배치한다. controller, service, exception은 현재 calendar 도메인에 없으므로 추가 시 같은 위치에 생성한다.

---

## 8. 보안 주의사항 (application.yml 관련)

현재 `application.yml`에 DB 자격증명과 JWT secret이 하드코딩된 기본값으로 커밋되어 있다.

```yaml
url: ${DB_URL:jdbc:log4jdbc:mysql://...}
username: ${DB_USERNAME:terry}
password: ${DB_PASSWORD:quftkxkd1!}
jwt:
  secret: '1UEsiw0R+/...'
```

이는 개발 편의용 기본값이며 프로덕션 배포 시 반드시 환경 변수로 주입해야 한다. 신규 코드에서 자격증명을 하드코딩하지 않는다. `.env.example`에 키 이름만 추가하고 실제 값은 `.env`(gitignore됨)나 시크릿 관리 도구를 사용한다.

# Backend JWT 코드 스타일 가이드

## 1. 목적과 적용 범위

이 문서는 현재 `src/main/java/com/terry/backend` 및 `src/main/resources/mappers`의 구현을 분석해 정리한 **신규·수정 코드용 기준**이다. Spring Boot 3 / Java 17 / MyBatis / MySQL 조합을 전제로 한다.

기존 코드에는 서로 다른 시기에 도입된 관례가 함께 있다. 이 문서는 자주 반복되는 패턴을 기본 규칙으로 삼되, 보안 또는 유지보수에 문제가 되는 기존 관례(`System.out`, 문자열 결합 로그, 비밀번호 로그 등)는 신규 코드에서 재현하지 않는다.

## 2. 패키지 구조

루트 패키지는 `com.terry.backend`이다. 기능의 성격에 따라 다음과 같이 배치한다.

```text
com.terry.backend
├── api/
│   ├── <feature>/                         # 일반 API (예: code)
│   └── admin/system/<domain>/             # 관리자 시스템 도메인
│       ├── controller/
│       ├── dto/
│       ├── exception/
│       ├── mapper/
│       ├── service/
│       └── strategy/                      # 식별자 생성 전략 등이 필요할 때만
├── core/                                  # 여러 도메인에서 공유하는 기반 기능
│   └── <feature>/{config,dto,mapper,service,util,...}
├── web/                                   # 웹 경계 및 웹 인프라
│   └── <feature>/{config,controller,dto,exception,mapper,service,...}
└── thirdparty/                            # 외부 기술의 어댑터·설정·핸들러
    └── <technology>/
```

- 도메인별 계층은 `controller → service → mapper` 순서로 의존한다. 컨트롤러가 Mapper를 직접 호출하지 않는다.
- 공통 DTO, 페이징, 메시지, 직렬번호 등은 `core`에 둔다. JWT, Swagger, MyBatis `TypeHandler`처럼 외부 라이브러리와 직접 맞닿는 구현은 `thirdparty`에 둔다.
- Mapper XML은 Java 인터페이스와 같은 책임 단위로 `src/main/resources/mappers/<area>/<domain>/<MapperName>.xml`에 둔다. 예: `api.admin.system.member.mapper.AdminMemberMapper`는 `mappers/admin/member/AdminMemberMapper.xml`에 둔다.
- 현재 MyBatis 탐색 경로는 `classpath:/mappers/**/*Mapper.xml`이다. 파일명은 반드시 `Mapper.xml`로 끝나야 한다.

## 3. 네이밍 규칙

| 대상 | 기준 | 예시 |
| --- | --- | --- |
| 패키지 | 모두 소문자, 역할·도메인 순서 | `api.admin.system.member.service` |
| 클래스·인터페이스·enum | PascalCase, 역할 접미사 사용 | `AdminMemberService`, `MemberDTO`, `AdminMemberMapper`, `MenuType` |
| 예외 | 의미가 분명한 명사구 + `Exception` 또는 기존 도메인 관례 | `MemberNotFound`, `MenuNotFoundException` |
| 메서드 | lowerCamelCase, 동사로 시작 | `selectById`, `findByPath`, `deleteByList`, `checkLoginId` |
| 조회 Mapper 메서드 | `select...` 또는 `find...` | `select`, `selectByUsername`, `findOne` |
| 변경 Mapper 메서드 | `insert`, `update`, `delete`, `save`, `upsert...` | `deleteAuthorityLink`, `upsertRefreshToken` |
| DTO 필드·파라미터 | lowerCamelCase | `parentId`, `loginId`, `refreshTokenExpireTime` |
| DB 테이블·컬럼 | 대문자 `SNAKE_CASE`, 도메인 접두어 `SWC_` | `SWC_USER`, `USER_LOGIN_ID`, `REGIST_DT` |
| SQL 별칭 | 테이블을 나타내는 짧은 대문자 | `SWC_USER U`, `SWC_MENU M` |

- 식별자 파라미터는 기본적으로 `id`를 쓴다. 문맥상 둘 이상이면 `userId`, `authorityId`, `menuCode`처럼 도메인을 붙인다.
- DTO 이름의 약어는 기존 명명(`DTO`, `YN`, `JWT`)을 따른다. 새 패키지·클래스에는 대소문자가 섞인 `DTO` 디렉터리 대신 기존 다수 관례인 소문자 `dto`를 사용한다.
- 기존의 `CodeAreadyExists` 및 `core.excption`은 오탈자가 포함된 기존 공개 타입·패키지다. 호환 목적 외 새 이름에 이 철자를 확장하지 않는다.

## 4. DTO 작성 스타일

### 기본 형태

- 영속 도메인 DTO는 `BaseDTO`를 상속한다. `BaseDTO`가 공통 감사 필드(`createDt`, `createId`, `updateDt`, `updateId`)와 `Serializable`을 제공한다.
- 상속 DTO는 Lombok의 `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@SuperBuilder`, `@EqualsAndHashCode(callSuper = false)` 조합을 기본으로 한다.
- 상속하지 않는 단순 요청·응답 DTO는 `@Data`와 필요한 생성자, `@Builder`를 사용한다. 상속 관계가 없으면 `@SuperBuilder` 대신 `@Builder`를 쓴다.
- 검색 조건은 `<Domain>SearchParam`으로 분리하고, 내부 전용 조건은 `@JsonIgnore`로 노출을 막는다. 공통 검색 조건이 필요하면 `BaseSearchParam` 또는 `PageableSearchParam`을 상속한다.
- 목록과 부가 정보를 함께 반환할 때는 `<Domain>ResponseEntity`처럼 별도 응답 DTO를 둔다.

```java
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MenuDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private String id;

    @NotEmpty
    @Size(min = 4, max = 40)
    private String code;

    @NotNull
    @Min(0)
    private Integer sort;
}
```

### 검증과 직렬화

- 요청 DTO의 필수값과 범위는 필드에 Jakarta Validation 어노테이션(`@NotEmpty`, `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Pattern`, `@Email`)으로 선언한다. 사용자에게 보여 줄 검증 오류가 필요한 경우 한국어 `message`를 함께 둔다.
- 컨트롤러에서 DTO 본문을 받을 때는 `@RequestBody @Valid`를 함께 사용한다.
- 감사 사용자 ID처럼 API에 노출하면 안 되는 필드는 `@JsonIgnore`를 사용한다. 검색 DTO와 페이징 응답은 필요에 따라 `@JsonInclude(NON_NULL)`을 사용한다.
- DTO 필드는 데이터베이스 컬럼명이 아니라 Java lowerCamelCase로 작성하고, XML `resultMap` 또는 `map-underscore-to-camel-case`로 연결한다.

## 5. 예외 처리 방식

### 현재 구조

- 공통 기반 예외는 `core.excption.SystemException`이다. 이 예외는 메시지 코드와 인자를 보관하고 `MessageSourceUtils`로 지역화된 메시지를 만든다.
- 도메인 예외는 각 도메인의 `exception` 패키지에서 `SystemException`을 상속한다. `NotFound`는 `404`, 중복은 `409`를 `getHttpStatus()` 오버라이드로 표현한다.
- 서비스는 조회 결과가 없거나 도메인 규칙을 위반할 때 구체적 도메인 예외를 던진다. 예: `throw new MemberNotFound(id);`.
- 로그인처럼 정상적인 업무 실패를 HTTP 오류 대신 본문으로 응답하는 흐름은 `ResponseMessages.fail(...)`을 반환한다.

```java
public class MemberNotFound extends SystemException {
    public MemberNotFound(final String id) {
        super("AdminMemberService.ERROR.NotFound", new Object[] { id });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
```

### 신규 코드 기준

- 새 도메인 오류는 `SystemException` 하위 타입으로 만들고, 메시지 키는 메시지 properties에 추가한다. HTTP 상태가 기본값(500)이 아니면 `getHttpStatus()`를 오버라이드한다.
- 현재 전역 예외 처리기(`@RestControllerAdvice` / `@ExceptionHandler`)는 없다. 따라서 `SystemException`의 `httpStatus`를 실제 HTTP 응답으로 일관되게 변환할 공통 처리도 구현되어 있지 않다. 이 문서만으로 해당 동작을 가정하지 않는다.
- 요청 단위에서 예외를 잡을 필요가 있을 때만 잡고, 복구할 수 없다면 원인을 보존해 다시 던진다. 트랜잭션 메서드에서 예외를 `catch`한 뒤 실패 응답만 반환하면 롤백되지 않을 수 있다.
- 기존의 `throws Exception`은 다수의 서비스·컨트롤러에 존재하지만, 새 공개 메서드에는 구체적인 도메인 예외 또는 런타임 예외를 우선한다. 불필요한 광범위 선언을 추가하지 않는다.

## 6. 트랜잭션 처리 방식

- 트랜잭션은 Service 계층의 쓰기 작업에 선언한다. 컨트롤러와 Mapper에는 선언하지 않는다.
- 관리자 CRUD의 저장·삭제·일괄삭제는 다음 관례를 사용한다.

```java
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
public void save(final String id, final MemberDTO entity) throws Exception {
    // 검증, 여러 Mapper 변경 작업
}
```

- 보안·초기 설정 기능에는 기본 `@Transactional`도 사용된다. 즉, 현재 프로젝트에는 두 관례가 공존한다.
  - 독립 커밋이 반드시 필요한 관리자 CRUD: `REQUIRES_NEW`, `rollbackFor = Throwable.class`
  - 일반적인 하나의 업무 단위: 기본 전파 옵션의 `@Transactional`
- 여러 Mapper 변경(예: 본 엔터티 저장 + 권한 연결 저장/삭제)은 하나의 Service 공개 메서드 안에서 처리하고 하나의 트랜잭션으로 묶는다.
- 읽기 전용 메서드에는 현재 `@Transactional(readOnly = true)` 관례가 없다. 새 코드도 명확한 요구가 없는 한 기존과 맞춰 붙이지 않는다.
- 예외를 잡아 삼키는 방식은 트랜잭션 경계를 무력화할 수 있으므로, 트랜잭션 내 변경 작업에서는 로그만 남기고 반환하지 않는다. 재던지거나 실패를 호출자에게 전파한다.

## 7. MyBatis Mapper 작성 규칙

### Java 인터페이스

- Mapper는 해당 도메인의 `mapper` 패키지에 인터페이스로 만들고 `@Mapper`를 붙인다.
- XML의 `<mapper namespace>`는 인터페이스의 완전 수식 이름과 정확히 같아야 한다. XML statement `id`는 인터페이스 메서드명과 정확히 맞춘다.
- 단일 또는 다중 스칼라 인자는 `@Param`으로 명시한다. 기존 다수 코드는 `@Param(value = "...")` 형식을 쓴다.
- 조회는 DTO, `List<DTO>`, `Optional<DTO>`, 스칼라를 반환한다. 변경 메서드는 기존 관례상 `void`를 사용한다.
- 복잡한 조회나 재사용되는 매핑은 XML `resultMap`을 사용한다. 단순한 컬럼-프로퍼티 변환은 `resultType`을 사용할 수 있다.

```java
@Mapper
public interface AdminMemberMapper {

    List<MemberDTO> select(MemberSearchParam param);

    MemberDTO selectById(@Param(value = "id") String id);

    void insert(MemberDTO entity);

    void update(MemberDTO entity);
}
```

### XML

- XML 헤더와 MyBatis Mapper 3.0 DTD를 유지한다.
- 공통 결과 매핑은 `mappers/core/ResultMapper.xml`의 `core.<ResultMapId>`를 참조할 수 있다. 도메인 전용 매핑은 해당 XML에 `<resultMap id="...">`로 둔다.
- 조건절은 `<where>`, 선택 조건은 `<if test="...">`, 분기는 `<choose>`를 사용한다. 각 placeholder에는 현재 관례대로 JDBC 타입을 명시한다: `#{id, jdbcType=VARCHAR}`.
- 파라미터는 `${...}`가 아니라 `#{...}`로 바인딩한다. `${...}`가 필요한 동적 식별자·정렬식은 별도 화이트리스트가 없으면 추가하지 않는다.
- SQL 문법의 비교 연산자 또는 긴 블록은 `<![CDATA[ ... ]]>`로 감쌀 수 있다. 기존 XML은 CDATA 사용 여부가 혼재하므로, 한 statement 안에서는 한 방식으로 일관되게 작성한다.

## 8. SQL 스타일

- 예약어와 테이블·컬럼명은 대문자로 작성한다. 테이블 별칭은 선언 뒤 모든 컬럼 참조에 사용한다: `SWC_USER U`, `U.USER_ID`.
- `SELECT`, `FROM`, `WHERE`, `ORDER BY`, `INSERT INTO`, `VALUES`, `UPDATE`, `SET`, `DELETE FROM`을 줄 단위로 분리한다. 선택 컬럼과 `SET`·`VALUES` 값은 첫 항목 뒤에 선행 쉼표를 둔다.

```sql
SELECT
    U.USER_ID
  , U.USER_LOGIN_ID
  , U.USER_NM
FROM
    SWC_USER U
WHERE
    U.USER_ID = UPPER(#{id, jdbcType=VARCHAR})
ORDER BY
    U.USER_NM
```

- 문자 식별자와 코드값은 저장·비교 시 `UPPER(...)`로 정규화하는 패턴이 널리 쓰인다. 같은 테이블의 같은 성격의 새 조건에도 이를 적용한다.
- 생성·수정 시각은 SQL의 `NOW()`를 사용하고, 감사 ID는 DTO의 `createId`/`updateId`를 바인딩한다.
- MySQL 문법을 사용한다: `IFNULL`, `CONCAT`, `ON DUPLICATE KEY UPDATE`, `WITH RECURSIVE`. 계층형 코드·메뉴는 재귀 CTE로 하위 항목을 처리한다.
- `SELECT *`를 사용하지 않고 필요한 컬럼을 명시한다. DTO와 컬럼명이 다르거나 TypeHandler가 필요하면 `resultMap`을 명시한다.
- 새 SQL에서는 `+` 문자열 결합이나 대소문자가 섞인 `where` 같은 기존 편차를 늘리지 말고, `CONCAT`과 대문자 키워드로 통일한다.

## 9. 주석 스타일

- 공개 Service·Mapper 메서드 중 의미가 바로 드러나지 않는 API는 한국어 Javadoc으로 책임, 파라미터, 반환값·예외를 설명한다.
- 구현 내부의 업무 단계는 한국어 `//` 한 줄 주석으로 표시한다. 기존의 `/** ... */` 블록 주석도 존재하지만, 새 구현 단계에는 `//`를 우선한다.
- Mapper XML 설명은 `<!-- 관리자 권한을 가진 사용자 수 조회 -->` 형식으로 둔다.
- 주석은 코드가 표현하지 못하는 업무 이유·제약만 설명한다. `// 사용자 생성`처럼 바로 다음 코드와 동일한 내용은 긴 설명이 필요할 때만 남긴다.
- 임시 코드, 폐기된 캐시 코드처럼 주석 처리한 실행 코드는 신규 코드에 남기지 않는다. 필요한 배경은 이슈·커밋 또는 짧은 설명 주석으로 대체한다.

## 10. 로그 작성 방식

- 로그가 필요한 Spring 컴포넌트에는 Lombok `@Slf4j`를 선언하고 `log.debug/info/warn/error`를 사용한다.
- SLF4J placeholder 형식을 기본으로 사용한다. 문자열 연결이나 `String.format`은 새 코드에서 사용하지 않는다.

```java
log.info("Login successful - Username: {}, IP: {}", username, clientIP);
log.warn("Login rate limit exceeded - IP: {}", clientIP);
log.error("Error creating first admin account", e);
```

- 레벨은 다음 기준을 따른다.
  - `debug`: 정상 처리의 상세 진단 정보
  - `info`: 로그인 성공, 초기 관리자 생성 등 중요한 정상 이벤트
  - `warn`: 인증 실패, 권한 없는 접근, 제한 초과 등 예상 가능한 보안·업무 이상
  - `error`: 복구되지 않은 예외 또는 처리 실패. 스택 트레이스가 필요하면 마지막 인자로 예외 객체를 전달한다.
- 사용자 ID, 로그인 ID, IP처럼 운영 분석에 필요한 식별자는 필요한 범위에서 기록한다. 비밀번호, 원문 JWT/Refresh Token, 해시값, 인증 객체 전체는 기록하지 않는다.
- `System.out.println`은 로그가 아니다. 기존 코드의 콘솔 출력과 비밀번호 출력은 레거시 예외이며 신규 코드에 사용하지 않는다.

## 11. 일반 코딩 컨벤션

### Java와 Spring

- Java 클래스는 한 파일에 하나만 둔다. 패키지 선언 뒤 import, 그 뒤 어노테이션·클래스 순서로 작성한다.
- DI는 `final` 필드와 생성자 주입을 사용한다. 기존에는 명시 생성자와 `@RequiredArgsConstructor`가 공존하므로, 한 클래스에서는 둘 중 하나만 사용한다.
- Spring 역할 어노테이션은 클래스에 선언한다: `@RestController`, `@Service`, `@Mapper`, `@Configuration`.
- HTTP 엔드포인트는 메서드 매핑 어노테이션으로 선언하고, 경로 변수는 `@PathVariable`, 요청 쿼리는 `@RequestParam`, JSON 본문은 `@RequestBody`로 명시한다. 생성은 `@ResponseStatus(HttpStatus.CREATED)`를 사용한다.
- Swagger를 사용하는 새 공개 API는 기존 Setup·Admin API와 같이 `@Tag`, 필요하면 `@Operation`/`@Schema`를 추가한다.

### 메서드와 데이터 처리

- 공개 Service 메서드는 `select`, `selectById`, `save`, `delete`, `deleteByList`, `check...`처럼 업무 동사를 사용한다. 생성/수정의 상세 단계는 `private create...`, `private edit...` 메서드로 분리할 수 있다.
- 외부에서 변경하지 않아야 하는 파라미터·지역 변수에는 기존 관리자 서비스처럼 `final`을 사용한다. 이미 작성 중인 파일의 스타일을 우선한다.
- 컬렉션 결과는 `null` 대신 빈 컬렉션을 우선하고, nullable 반환값은 호출부에서 명시적으로 검사한다. Mapper가 `Optional`을 반환하는 경우 `isEmpty`/`isPresent`로 처리한다.
- 컨트롤러는 HTTP 입출력과 검증만 담당하고, 업무 규칙·DB 변경은 Service로 위임한다.

### 형식

- 들여쓰기는 공백 4칸을 기본으로 한다. 기존 파일이 2칸 또는 탭을 사용한다면 해당 파일의 기존 형식을 보존하고, 관련 없는 대규모 재포맷은 하지 않는다.
- 여는 중괄호는 선언문과 같은 줄에 둔다. 메서드와 논리 블록 사이에는 빈 줄을 둔다.
- import와 어노테이션의 세부 순서는 기존 파일 사이에 완전한 단일 규칙이 없다. 수정 시에는 대상 파일의 순서를 보존하며, 새 파일은 프로젝트 import → 외부 라이브러리 → JDK 순으로 그룹화한다.

## 12. 구현 전 점검 목록

- [ ] 기능이 `api`, `core`, `web`, `thirdparty` 중 어느 책임에 속하는가?
- [ ] Controller, Service, Mapper, DTO, Exception을 필요한 범위에서만 만들었는가?
- [ ] 쓰기 Service 메서드가 적절한 `@Transactional` 경계를 가지는가?
- [ ] Mapper 인터페이스 메서드명·`@Param`명·XML namespace·statement id가 일치하는가?
- [ ] SQL이 명시 컬럼, `#{}` 바인딩, JDBC 타입, 감사 필드를 사용하는가?
- [ ] 요청 DTO에 검증과 `@Valid`가 적용되었는가?
- [ ] 예외 메시지 키와 HTTP 상태가 정의되어 있는가?
- [ ] 민감정보와 `System.out` 없이 적절한 수준의 로그가 남는가?
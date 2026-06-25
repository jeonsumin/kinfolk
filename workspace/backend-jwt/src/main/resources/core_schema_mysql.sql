-- WORKSPACE definition

CREATE TABLE `WORKSPACE` (
                             `ID` varchar(255) NOT NULL,
                             `WS_NM` varchar(255) DEFAULT NULL COMMENT '워크스페이스 이름',
                             `WS_DESC` text DEFAULT NULL COMMENT '워크스페이스 설명',
                             `WS_OWNER_ID` varchar(255) DEFAULT NULL COMMENT '워크스페이스 관리자',
                             `REGIST_DT` timestamp NULL DEFAULT NULL COMMENT '생성일자',
                             PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci COMMENT='워크스페이스';

-- WORKSPACE_USER definition

CREATE TABLE `WORKSPACE_USER` (
                                  `ID` varchar(255) DEFAULT NULL,
                                  `WS_ID` varchar(255) DEFAULT NULL COMMENT '워크스페이스 ID',
                                  `USER_ID` varchar(255) DEFAULT NULL COMMENT '회원 ID',
                                  `AUTHORITY` varchar(50) DEFAULT NULL COMMENT '권한',
                                  `REGIST_DT` timestamp NULL DEFAULT NULL COMMENT '생성일자',
                                  KEY `WORKSPACE_USER_WORKSPACE_FK` (`WS_ID`),
                                  CONSTRAINT `WORKSPACE_USER_WORKSPACE_FK` FOREIGN KEY (`WS_ID`) REFERENCES `WORKSPACE` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci COMMENT='조직 회원 매핑';

-- WORKSPACE_INVITATION definition

CREATE TABLE `WORKSPACE_INVITATION` (
  `INVITATION_ID`    varchar(255) NOT NULL,
  `WS_ID`            varchar(255) NOT NULL                    COMMENT '워크스페이스 ID',
  `INVITE_EMAIL`     varchar(255) DEFAULT NULL                COMMENT '초대 대상 이메일(링크 초대면 NULL)',
  `INVITE_TOKEN`     varchar(255) NOT NULL                    COMMENT '초대 토큰(UUID)',
  `AUTHORITY`        varchar(50)  NOT NULL DEFAULT 'MEMBER'   COMMENT '부여 권한(OWNER/MEMBER)',
  `STATUS`           varchar(20)  NOT NULL DEFAULT 'PENDING'  COMMENT '상태(PENDING/ACCEPTED/EXPIRED/REVOKED)',
  `EXPIRE_DT`        timestamp    NOT NULL                    COMMENT '만료일시(3일)',
  `ACCEPTED_USER_ID` varchar(255) DEFAULT NULL                COMMENT '수락한 사용자 ID',
  `ACCEPTED_DT`      timestamp    NULL DEFAULT NULL           COMMENT '수락일시',
  `REGIST_DT`        timestamp    NULL DEFAULT NULL           COMMENT '생성일자',
  `REGIST_ID`        varchar(255) DEFAULT NULL                COMMENT '초대자 ID',
  PRIMARY KEY (`INVITATION_ID`),
  UNIQUE KEY `uk_invitation_token` (`INVITE_TOKEN`),
  KEY `idx_invitation_ws_id` (`WS_ID`),
  KEY `idx_invitation_email` (`INVITE_EMAIL`),
  CONSTRAINT `WORKSPACE_INVITATION_WORKSPACE_FK` FOREIGN KEY (`WS_ID`) REFERENCES `WORKSPACE` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci COMMENT='워크스페이스 초대';

-- SWC_USER definition

CREATE TABLE IF NOT EXISTS `SWC_USER`(
`USER_ID`            VARCHAR(255) NOT NULL,
`USER_LOGIN_ID`      VARCHAR(255) DEFAULT NULL                          COMMENT '아이디',
`USER_NM`            VARCHAR(255) DEFAULT NULL                          COMMENT '이름',
`USER_PASSWORD`      VARCHAR(255) DEFAULT NULL                          COMMENT '비밀번호',
`USER_IMAGE_URI`     VARCHAR(255) DEFAULT NULL                          COMMENT '이미지 경로',
`USER_SEXDSTN`       VARCHAR(255) DEFAULT NULL                          COMMENT '성별',
`USER_EMAIL`         VARCHAR(255) DEFAULT NULL                          COMMENT '이메일',
`USER_DC`            VARCHAR(255) DEFAULT NULL                          COMMENT '설명',
`USER_USE`           VARCHAR(1)   DEFAULT NULL                          COMMENT '사용 여부',
`USER_LOCK`          VARCHAR(1)   DEFAULT NULL                          COMMENT '잠금 여부',
`USER_LAST_LOGIN_DT` DATETIME     DEFAULT NULL                          COMMENT '마지막 로그인 일시',
`HIST_ID`            VARCHAR(255) DEFAULT NULL                          COMMENT '이력',
`REGIST_ID`          VARCHAR(255) DEFAULT NULL                          COMMENT '생성자',
`REGIST_DT`          DATETIME     DEFAULT NOW() NULL                    COMMENT '생성일시',
`UPDT_ID`            VARCHAR(255) DEFAULT NULL                          COMMENT '수정자',
`UPDT_DT`            DATETIME     DEFAULT NOW() ON UPDATE NOW() NULL    COMMENT '수정일시',
`USER_MBTLNUM`       VARCHAR(255) DEFAULT NULL                          COMMENT '핸드폰 번호',
PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '회원 테이블';

-- SWC_MENU definition
CREATE TABLE IF NOT EXISTS `SWC_MENU` (
`MENU_ID`           VARCHAR(255)    NOT NULL,
`MENU_UPPER_ID`     VARCHAR(255)    DEFAULT NULL COMMENT '부모 메뉴 ID',
`MENU_CODE`         VARCHAR(255)    DEFAULT NULL COMMENT '메뉴 코드',
`MENU_NM`           VARCHAR(255)    DEFAULT NULL COMMENT '메뉴 이름',
`MENU_DC`           VARCHAR(255)    DEFAULT NULL COMMENT '메뉴 설명',
`MENU_TY`           VARCHAR(255)    DEFAULT NULL COMMENT '메뉴 타입 (DIR:디렉토리, PAGE: 페이지)',
`MENU_SORT`         INT(11)         DEFAULT NULL COMMENT '정렬 순서',
`MENU_ICON`         VARCHAR(255)    DEFAULT NULL COMMENT '메뉴 아이콘',
`MENU_URI`          VARCHAR(255)    DEFAULT NULL COMMENT '메뉴 경로',
`MENU_LEVEL`        INT(11)         DEFAULT NULL COMMENT '계층 레벨',
`MENU_USE`          VARCHAR(1)      DEFAULT NULL COMMENT '사용 여부 (Y/N)',
`HIST_ID`           VARCHAR(255)    DEFAULT NULL COMMENT '이력 ID',
`HIST_SN`           INT(11)         DEFAULT NULL COMMENT '이력 시리얼 번호',
`REGIST_DT`         datetime        DEFAULT NULL COMMENT '생성 일시 ',
`REGIST_ID`         VARCHAR(255)    DEFAULT NULL COMMENT '생성자',
`UPDT_DT`           datetime        DEFAULT NULL COMMENT '수정일시 ',
`UPDT_ID`           VARCHAR(255)    DEFAULT NULL COMMENT '수정자',
`LEVEL_AUTHOR`      INT(11)         DEFAULT NULL COMMENT '권한 레벨',
PRIMARY KEY (`MENU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '메뉴 테이블';

-- SWC_CODE definition

CREATE TABLE IF NOT EXISTS `SWC_CODE` (
`CODE_ID`               varchar(255)    NOT NULL,
`CODE_UPPER_ID`         varchar(255)    DEFAULT NULL COMMENT '부모 코드 ID',
`CODE`                  varchar(255)    DEFAULT NULL COMMENT '코드',
`CODE_NM`               varchar(255)    DEFAULT NULL COMMENT '코드 이름',
`CODE_DC`               varchar(255)    DEFAULT NULL COMMENT '코드 설명',
`CODE_SORT`             int(11)         DEFAULT NULL COMMENT '정렬',
`CODE_COURS`            varchar(2000)   DEFAULT NULL COMMENT '경로',
`CODE_ALL_COURS`        varchar(2000)   DEFAULT NULL COMMENT '전체 경로',
`CODE_LEVEL`            int(11)         DEFAULT NULL COMMENT '계층 레벨',
`CODE_TY`               varchar(255)    DEFAULT NULL COMMENT '타입 (SYSTEM,...)',
`CODE_VALUE1`           varchar(255)    DEFAULT NULL COMMENT '사용자 정의 1',
`CODE_VALUE2`           varchar(255)    DEFAULT NULL COMMENT '사용자 정의 2',
`CODE_VALUE3`           varchar(255)    DEFAULT NULL COMMENT '사용자 정의 3',
`CODE_USE`              varchar(1)      DEFAULT NULL COMMENT '사용 여부 (Y/N)',
`HIST_ID`               varchar(255)    DEFAULT NULL COMMENT '이력 ID',
`HIST_SN`               int(11)         DEFAULT NULL COMMENT '이력 시리얼번호',
`REGIST_DT`             datetime        DEFAULT NULL COMMENT '생성일시',
`REGIST_ID`             varchar(255)    DEFAULT NULL COMMENT '생성자',
`UPDT_DT`               datetime        DEFAULT NULL COMMENT '수정일시',
`UPDT_ID`               varchar(255)    DEFAULT NULL COMMENT '수정자',
PRIMARY KEY (`CODE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '공통 코드 테이블';

-- SWC_AUTHOR definition

CREATE TABLE IF NOT EXISTS `SWC_AUTHOR` (
`AUTHOR_ID`         varchar(255)    NOT NULL,
`AUTHOR_CODE`       varchar(255)    DEFAULT NULL   COMMENT '권한 코드',
`AUTHOR_NM`         varchar(255)    DEFAULT NULL   COMMENT '권한 이름',
`AUTHOR_DC`         varchar(255)    DEFAULT NULL   COMMENT '권한 설명',
`AUTHOR_USE`        CHAR(4)         DEFAULT NULL   COMMENT '사용 여부 (Y/N)',
`REGIST_DT`         datetime        DEFAULT NULL   COMMENT '생성일시',
`REGIST_ID`         varchar(255)    DEFAULT NULL   COMMENT '생성자',
`UPDT_DT`           datetime        DEFAULT NULL   COMMENT '수정일시',
`UPDT_ID`           varchar(255)    DEFAULT NULL   COMMENT '수정자',
PRIMARY KEY (`AUTHOR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT "권한 테이블";

-- PAIR.SWC_USER_AUTHOR definition

CREATE TABLE IF NOT EXISTS `SWC_USER_AUTHOR` (
`USER_ID`           varchar(255)    NOT NULL        COMMENT '회원 ID',
`AUTHOR_ID`         varchar(255)    NOT NULL        COMMENT '권한 ID',
`AUTHOR_VALUE`      int(11)         DEFAULT NULL    COMMENT '권한 계층 레벨',
`HIST_ID`           varchar(255)    DEFAULT NULL    COMMENT '이력 ID',
`REGIST_DT`         datetime        DEFAULT NULL    COMMENT '생성일시',
`REGIST_ID`         varchar(255)    DEFAULT NULL    COMMENT '생성자',
`UPDT_DT`           datetime        DEFAULT NULL    COMMENT '수정일시',
`UPDT_ID`           varchar(255)    DEFAULT NULL    COMMENT '수정자',
PRIMARY KEY (`USER_ID`,`AUTHOR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '회원 권한 테이블';

-- PAIR.SWC_AUTHOR_MENU definition

CREATE TABLE IF NOT EXISTS `SWC_AUTHOR_MENU` (
`MENU_ID`           varchar(255)    NOT NULL COMMENT '메뉴 ID',
`AUTHOR_ID`         varchar(255)    NOT NULL COMMENT '권한 ID',
`AUTHOR_VALUE`      int(11)         DEFAULT NULL COMMENT '권한 계층 레벨',
`REGIST_DT`         datetime        DEFAULT NULL COMMENT '생성일시',
`REGIST_ID`         varchar(255)    DEFAULT NULL COMMENT '생성자',
`UPDT_DT`           datetime        DEFAULT NULL COMMENT '수정일시',
`UPDT_ID`           varchar(255)    DEFAULT NULL COMMENT '수정자',
PRIMARY KEY (`MENU_ID`,`AUTHOR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '메뉴 권한 테이블';


-- SWC_SERIAL definition

CREATE TABLE IF NOT EXISTS`SWC_SERIAL` (
`SERIAL_ID`             VARCHAR(255)    NOT NULL        COMMENT '시리얼 코드',
`VALUE`                 INT(11)         DEFAULT NULL    COMMENT '시리얼 번호',
`UPDATE_DT`             DATETIME        DEFAULT NULL    COMMENT '생성일시',
PRIMARY KEY (`SERIAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='시리얼 테이블';

-- FILE definition

CREATE TABLE IF NOT EXISTS `SWC_FILE` (
`FILE_ID`       varchar(255) NOT NULL,
`FILE_NM`       varchar(255) DEFAULT NULL   COMMENT '파일 이름',
`FILE_COURS`    varchar(255) DEFAULT NULL   COMMENT '파일 경로',
`FILE_MG`       varchar(255) DEFAULT NULL   COMMENT '파일 사이즈',
`FILE_TY`       varchar(255) DEFAULT NULL   COMMENT '파일 타입',
`REGIST_ID`     varchar(255) DEFAULT NULL   COMMENT '생성자',
`REGIST_DT`     datetime DEFAULT NULL       COMMENT '생성일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파일 테이블';

-- SHOPPING_CATEGORY definition

CREATE TABLE IF NOT EXISTS `SHOPPING_CATEGORY` (
`CATEGORY_ID`  VARCHAR(255) NOT NULL,
`WS_ID`        VARCHAR(255) NOT NULL                         COMMENT '워크스페이스 ID',
`CATEGORY_NM`  VARCHAR(100) NOT NULL                         COMMENT '카테고리명',
`CATEGORY_ICON` VARCHAR(50) DEFAULT NULL                     COMMENT '아이콘 키',
`SORT_ORDER`   INT          DEFAULT 0                        COMMENT '정렬 순서',
`REGIST_DT`    DATETIME     DEFAULT NOW()                    COMMENT '생성일시',
`REGIST_ID`    VARCHAR(255) DEFAULT NULL                     COMMENT '생성자',
PRIMARY KEY (`CATEGORY_ID`),
KEY `idx_shopping_category_ws_id` (`WS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='장보기 카테고리';

-- SHOPPING_ITEM definition

CREATE TABLE IF NOT EXISTS `SHOPPING_ITEM` (
`ITEM_ID`           VARCHAR(255) NOT NULL,
`WS_ID`             VARCHAR(255) NOT NULL                    COMMENT '워크스페이스 ID',
`CATEGORY_ID`       VARCHAR(255) DEFAULT NULL                COMMENT '카테고리 ID',
`ITEM_NM`           VARCHAR(255) NOT NULL                    COMMENT '아이템명',
`QUANTITY`          INT          DEFAULT 1                   COMMENT '수량',
`IS_CHECKED`        TINYINT(1)   DEFAULT 0                   COMMENT '구매완료 여부',
`ASSIGNED_USER_ID`  VARCHAR(255) DEFAULT NULL                COMMENT '담당자 ID',
`REGIST_DT`         DATETIME     DEFAULT NOW()               COMMENT '생성일시',
`REGIST_ID`         VARCHAR(255) DEFAULT NULL                COMMENT '생성자',
`UPDT_DT`           DATETIME     DEFAULT NOW() ON UPDATE NOW() COMMENT '수정일시',
`UPDT_ID`           VARCHAR(255) DEFAULT NULL                COMMENT '수정자',
PRIMARY KEY (`ITEM_ID`),
KEY `idx_shopping_item_ws_id` (`WS_ID`),
KEY `idx_shopping_item_category_id` (`CATEGORY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='장보기 아이템';

-- SCHEDULE_POLL definition
CREATE TABLE IF NOT EXISTS `SCHEDULE_POLL` (
`POLL_ID`      VARCHAR(255) NOT NULL,
`WS_ID`        VARCHAR(255) NOT NULL          COMMENT '워크스페이스 ID',
`PLANNER_ID`   VARCHAR(255) NOT NULL          COMMENT '플래너 ID',
`TITLE`        VARCHAR(255) NOT NULL          COMMENT '투표 제목',
`IS_ANONYMOUS` VARCHAR(1)   DEFAULT 'Y'       COMMENT '익명 여부 (항상 Y)',
`REGIST_DT`    DATETIME     DEFAULT NOW()     COMMENT '생성일시',
`REGIST_ID`    VARCHAR(255) DEFAULT NULL      COMMENT '생성자',
PRIMARY KEY (`POLL_ID`),
KEY `idx_schedule_poll_ws_id` (`WS_ID`),
KEY `idx_schedule_poll_planner_id` (`PLANNER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='희망일정 투표';

-- SCHEDULE_POLL_CANDIDATE definition
CREATE TABLE IF NOT EXISTS `SCHEDULE_POLL_CANDIDATE` (
`CANDIDATE_ID` VARCHAR(255) NOT NULL,
`POLL_ID`      VARCHAR(255) NOT NULL      COMMENT '투표 ID',
`START_DT`     DATE         NOT NULL      COMMENT '시작 날짜',
`END_DT`       DATE         NOT NULL      COMMENT '종료 날짜',
`REGIST_DT`    DATETIME     DEFAULT NOW() COMMENT '생성일시',
`REGIST_ID`    VARCHAR(255) DEFAULT NULL  COMMENT '생성자',
PRIMARY KEY (`CANDIDATE_ID`),
KEY `idx_schedule_poll_candidate_poll_id` (`POLL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='희망일정 후보';

-- SCHEDULE_POLL_VOTE definition
CREATE TABLE IF NOT EXISTS `SCHEDULE_POLL_VOTE` (
`VOTE_ID`      VARCHAR(255) NOT NULL,
`CANDIDATE_ID` VARCHAR(255) NOT NULL          COMMENT '후보 ID',
`USER_ID`      VARCHAR(255) NOT NULL          COMMENT '투표자 ID',
`REGIST_DT`    DATETIME     DEFAULT NOW()     COMMENT '투표일시',
PRIMARY KEY (`VOTE_ID`),
UNIQUE KEY `uq_schedule_poll_vote` (`CANDIDATE_ID`, `USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='희망일정 투표 기록';

-- PLACE_SUGGESTION definition
CREATE TABLE IF NOT EXISTS `PLACE_SUGGESTION` (
`PLACE_ID`      VARCHAR(255)  NOT NULL,
`WS_ID`         VARCHAR(255)  NOT NULL        COMMENT '워크스페이스 ID',
`PLANNER_ID`   VARCHAR(255) NOT NULL          COMMENT '플래너 ID',
`SOURCE_URL`    VARCHAR(1000) NOT NULL         COMMENT '장소 원본 URL',
`THUMBNAIL_URL` VARCHAR(1000) DEFAULT NULL     COMMENT '썸네일 URL',
`TITLE`         VARCHAR(255)  DEFAULT NULL     COMMENT '장소명',
`DESCRIPTION`   VARCHAR(1000) DEFAULT NULL     COMMENT '설명',
`CATEGORY`      VARCHAR(100)  DEFAULT NULL     COMMENT '카테고리',
`REGIST_DT`     DATETIME      DEFAULT NOW()    COMMENT '생성일시',
`REGIST_ID`     VARCHAR(255)  DEFAULT NULL     COMMENT '생성자',
PRIMARY KEY (`PLACE_ID`),
KEY `idx_place_suggestion_ws_id` (`WS_ID`),
KEY `idx_place_suggestion_planner_id` (`PLANNER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='장소 제안';

-- PLACE_SUGGESTION_VOTE definition
CREATE TABLE IF NOT EXISTS `PLACE_SUGGESTION_VOTE` (
`VOTE_ID`   VARCHAR(255) NOT NULL,
`PLACE_ID`  VARCHAR(255) NOT NULL          COMMENT '장소 ID',
`USER_ID`   VARCHAR(255) NOT NULL          COMMENT '투표자 ID',
`REGIST_DT` DATETIME     DEFAULT NOW()     COMMENT '투표일시',
PRIMARY KEY (`VOTE_ID`),
UNIQUE KEY `uq_place_suggestion_vote` (`PLACE_ID`, `USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='장소 제안 투표';

-- SETTLEMENT_EXPENSE definition
CREATE TABLE IF NOT EXISTS `SETTLEMENT_EXPENSE` (
`EXPENSE_ID`   VARCHAR(255) NOT NULL,
`WS_ID`        VARCHAR(255) NOT NULL             COMMENT '워크스페이스 ID',
`PLANNER_ID`   VARCHAR(255) NOT NULL          COMMENT '플래너 ID',
`EXPENSE_DATE` DATE         NOT NULL             COMMENT '정산 날짜 (서버 오늘)',
`ITEM`         VARCHAR(255) NOT NULL             COMMENT '항목명',
`PAYER`        VARCHAR(255) NOT NULL             COMMENT '지불자 (자유 텍스트)',
`AMOUNT`       BIGINT       NOT NULL             COMMENT '금액 (원)',
`STATUS`       VARCHAR(20)  DEFAULT 'IN_PROGRESS' COMMENT '정산 상태 (IN_PROGRESS/PENDING/SETTLED)',
`REGIST_DT`    DATETIME     DEFAULT NOW()        COMMENT '생성일시',
`REGIST_ID`    VARCHAR(255) DEFAULT NULL         COMMENT '생성자',
PRIMARY KEY (`EXPENSE_ID`),
KEY `idx_settlement_expense_ws_id` (`WS_ID`),
KEY `idx_settlement_expense_planner_id` (`PLANNER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='정산 내역';

-- PLANNER definition
CREATE TABLE IF NOT EXISTS `PLANNER` (
`PLANNER_ID`   VARCHAR(255) NOT NULL,
`WS_ID`        VARCHAR(255) NOT NULL              COMMENT '워크스페이스 ID',
`TITLE`        VARCHAR(255) NOT NULL              COMMENT '플래너 제목',
`PARTICIPANTS` JSON         DEFAULT NULL          COMMENT '참여자 목록 (JSON array)',
`COLOR`        VARCHAR(20)  DEFAULT 'blue'        COMMENT '플래너 색상 (blue/green/mauve)',
`CAL_YEAR`     INT          DEFAULT NULL          COMMENT '캘린더 연도',
`CAL_MONTH`    INT          DEFAULT NULL          COMMENT '캘린더 월',
`ITINERARY`    JSON         DEFAULT NULL          COMMENT '여행 일정 (JSON array)',
`REGIST_DT`    DATETIME     DEFAULT NOW()         COMMENT '생성일시',
`REGIST_ID`    VARCHAR(255) DEFAULT NULL          COMMENT '생성자',
`UPDT_DT`      DATETIME     DEFAULT NOW() ON UPDATE NOW() COMMENT '수정일시',
`UPDT_ID`      VARCHAR(255) DEFAULT NULL          COMMENT '수정자',
PRIMARY KEY (`PLANNER_ID`),
KEY `idx_planner_ws_id` (`WS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='플래너';

-- Refresh Token 저장 테이블
CREATE TABLE IF NOT EXISTS SWC_REFRESH_TOKEN
(
    USER_ID       VARCHAR(100) NOT NULL                         COMMENT '사용자 ID',
    REFRESH_TOKEN VARCHAR(512) NOT NULL                         COMMENT 'Refresh Token 값',
    EXPIRE_DT     DATETIME     NOT NULL                         COMMENT '만료일시',
    REGIST_DT     DATETIME     DEFAULT NOW()                    COMMENT '등록일시',
    UPDT_DT       DATETIME     DEFAULT NOW() ON UPDATE NOW()    COMMENT '수정일시',
    PRIMARY KEY (USER_ID)
) COMMENT = 'Refresh Token 관리';

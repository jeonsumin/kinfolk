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

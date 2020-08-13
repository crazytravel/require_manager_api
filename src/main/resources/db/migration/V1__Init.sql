CREATE TABLE RM_USER
(
    id            VARCHAR(100) NOT NULL PRIMARY KEY,
    username      VARCHAR(200) NOT NULL UNIQUE,
    password      VARCHAR(200) NOT NULL,
    nickname      VARCHAR(255) NULL,
    real_name     VARCHAR(255) NULL,
    phone         VARCHAR(20)  NULL,
    email         VARCHAR(100) NULL,
    who_is_who_id VARCHAR(100) NULL,
    is_active     BOOLEAN DEFAULT (true),
    created_at    TIMESTAMP,
    modified_at   TIMESTAMP
);

CREATE TABLE RM_ROLE
(
    id          VARCHAR(100) PRIMARY KEY,
    code        VARCHAR(200) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

CREATE TABLE RM_PERMISSION
(
    id          VARCHAR(100) PRIMARY KEY,
    code        VARCHAR(200) NOT NULL UNIQUE,
    name        VARCHAR(200) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

CREATE TABLE RM_USER_ROLE
(
    id          varchar(100) primary key,
    user_id     varchar(100) not null,
    role_id     varchar(100) not null,
    created_at  timestamp,
    modified_at timestamp
);

CREATE TABLE RM_ROLE_PERMISSION
(
    id            varchar(100) primary key,
    role_id       varchar(100) not null,
    permission_id varchar(100) not null,
    created_at    timestamp,
    modified_at   timestamp
);

CREATE TABLE RM_PROJECT
(
    id          varchar(100) primary key,
    code        varchar(100) unique not null,
    name        varchar(200) not null,
    description text,
    active      boolean default false,
    created_at  timestamp,
    modified_at timestamp
);

CREATE TABLE RM_PROJECT_USER
(
    id          varchar(100) primary key,
    project_id  varchar(100) not null,
    user_id     varchar(100) not null,
    owner       boolean default false,
    created_at  timestamp,
    modified_at timestamp
);

CREATE TABLE RM_STAGE
(
    id          varchar(100) primary key,
    project_id  varchar(100) not null,
    name        varchar(200) not null,
    next_id     varchar(100),
    created_at  timestamp,
    modified_at timestamp
);

CREATE TABLE RM_TASK
(
    id          varchar(100) primary key,
    code        varchar(100) unique not null,
    project_id  varchar(100)        not null,
    stage_id    varchar(100)        not null,
    content     text,
    status      varchar(20),
    next_id     varchar(100),
    user_id     varchar(100),
    created_at  timestamp,
    modified_at timestamp
);


INSERT INTO RM_USER (id, username, password, nickname, real_name)
VALUES ('0', 'admin', '{bcrypt}$2a$10$Mauvb3WBioPsOf9hZHX7l.np69XxobcoDn.kOEvcuu6YSafmqgQ6q', '系统管理员', '系统管理员'),
       ('1', 'larry', '{bcrypt}$2a$10$Mauvb3WBioPsOf9hZHX7l.np69XxobcoDn.kOEvcuu6YSafmqgQ6q', 'Larry', 'Larry'),
       ('2', 'sam', '{bcrypt}$2a$10$Mauvb3WBioPsOf9hZHX7l.np69XxobcoDn.kOEvcuu6YSafmqgQ6q', 'Sam', 'Sam');


INSERT INTO RM_PROJECT (id, code, name, description, active)
VALUES ('0', 'test1', '仓储系统', '仓储系统', true),
       ('1', 'test2', 'ERP系统', 'ERP系统', false),
       ('2', 'test3', 'APP项目', 'APP项目', false),
       ('3', 'test4', '微信小程序', '微信小程序', false);

INSERT INTO RM_PROJECT_USER(id, project_id, user_id, owner)
VALUES ('0', '0', '0', true);

INSERT INTO RM_STAGE (id, project_id, name, next_id)
VALUES ('0', '0', '需求列表', '1'),
       ('1', '0', '代办任务', '2'),
       ('2', '0', '开发中', '3'),
       ('3', '0', '测试中', '4'),
       ('4', '0', '已完成', '-1');

INSERT INTO RM_TASK (id, project_id, code, stage_id, content, next_id)
VALUES ('0', '0', 'm-1', '0', '初始化项目git仓库', '1'),
       ('1', '0', 'm-2', '0', '申请域名', '2'),
       ('2', '0', 'm-3', '0', '设计线框图', '3'),
       ('3', '0', 'm-4', '0', '分析业务流程', '-1');
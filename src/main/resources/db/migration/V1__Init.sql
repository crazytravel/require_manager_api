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
    id            varchar(100) primary key,
    code          varchar(100) not null,
    name          varchar(200) not null,
    description   text,
    owner_user_id varchar(100),
    active        boolean default false,
    created_at    timestamp,
    modified_at   timestamp
);

CREATE TABLE RM_PROJECT_USER
(
    id          varchar(100) primary key,
    project_id  varchar(100) not null,
    user_id     varchar(100) not null,
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
    project_id  varchar(100) not null,
    stage_id    varchar(100) not null,
    content     text,
    status      varchar(20),
    next_id     varchar(100),
    created_at  timestamp,
    modified_at timestamp
);

CREATE TABLE RM_TASK_USER
(
    id          varchar(100) primary key,
    project_id  varchar(100) not null,
    stage_id    varchar(100) not null,
    user_id     varchar(100) not null,
    created_at  timestamp,
    modified_at timestamp
);


INSERT INTO RM_USER (id, username, password, nickname, real_name)
VALUES ('0', 'admin', '{bcrypt}$2a$10$Mauvb3WBioPsOf9hZHX7l.np69XxobcoDn.kOEvcuu6YSafmqgQ6q', '默认用户', '默认用户');

INSERT INTO RM_PROJECT (id, code, name, description, owner_user_id, active)
VALUES ('0', 'test1', '测试项目1', '这是一个测试项目1', '0', true),
       ('1', 'test2', '测试项目2', '这是一个测试项目2', '0', false),
       ('2', 'test3', '测试项目3', '这是一个测试项目3', '0', false),
       ('3', 'test4', '测试项目4', '这是一个测试项目4', '0', false),
       ('4', 'test5', '测试项目5', '这是一个测试项目5', '0', false),
       ('5', 'test6', '测试项目6', '这是一个测试项目6', '0', false),
       ('6', 'test7', '测试项目7', '这是一个测试项目7', '0', false),
       ('7', 'test8', '测试项目8', '这是一个测试项目8', '0', false);

INSERT INTO RM_PROJECT_USER(id, project_id, user_id)
VALUES ('0', '0', '0');

INSERT INTO RM_STAGE (id, project_id, name, next_id)
VALUES ('0', '0', '需求列表', '1'),
       ('1', '0', '代办任务', '2'),
       ('2', '0', '开发中', '3'),
       ('3', '0', '测试中', '4'),
       ('4', '0', '已完成', '-1');

INSERT INTO RM_TASK (id, project_id, stage_id, content, status, next_id) VALUES
('0', '0', '0', '这是一个任务1', null, '1'),
('1', '0', '0', '这是一个任务2', null, '2'),
('2', '0', '0', '这是一个任务3', null, '3'),
('3', '0', '0', '这是一个任务4',null, '-1');
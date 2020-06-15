CREATE SEQUENCE SEQ_STRING_KEY;

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
    id VARCHAR(100) PRIMARY KEY,
    code VARCHAR(200) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

CREATE TABLE RM_USER_ROLE
(
    id varchar(100) primary key,
    user_id varchar(100) not null,
    role_id varchar(100) not null,
    created_at timestamp,
    modified_at timestamp
);

CREATE TABLE RM_ROLE_PERMISSION
(
    id varchar(100) primary key,
    role_id varchar(100) not null,
    permission_id varchar(100) not null,
    created_at timestamp,
    modified_at timestamp
);

CREATE TABLE RM_DEMAND
(
   id varchar(100) primary key,
   name varchar(200) not null,
   description text,
   created_at timestamp,
   modified_at timestamp
);

CREATE TABLE RM_TEMPLATE
(

);

CREATE TABLE RM_TASK
(

);

INSERT INTO RM_USER (id, username, password, nickname, real_name)
VALUES ('0', 'admin', '{bcrypt}$2a$10$Mauvb3WBioPsOf9hZHX7l.np69XxobcoDn.kOEvcuu6YSafmqgQ6q', '默认用户', '默认用户');
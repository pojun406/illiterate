CREATE TABLE User
(
    id        INT NOT NULL AUTO_INCREMENT,
    userid   VARCHAR(50) NOT NULL,
    username VARCHAR(10) NOT NULL,
    password  VARCHAR(225) NOT NULL,
    email  VARCHAR(255) NOT NULL,
    roles      VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
) COMMENT '유저 테이블';

CREATE TABLE Board
(
    board_id    INT          NOT NULL AUTO_INCREMENT,
    id          INT          NOT NULL,
    writer      VARCHAR(50)  NOT NULL,
    title       VARCHAR(50)  NOT NULL,
    content     TEXT         NOT NULL,
    request_img VARCHAR(255) NOT NULL,
    reg_date    VARCHAR(50)  NOT NULL,
    del_date    VARCHAR(50)  NULL,
    PRIMARY KEY (board_id),
    CONSTRAINT FK_User_TO_Board FOREIGN KEY (id) REFERENCES User (id)
) COMMENT '요청게시판';

CREATE TABLE OCR_Result
(
    id         INT          NOT NULL AUTO_INCREMENT,
    ocr_image  VARCHAR(255) NOT NULL,
    ocr_result VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_User_TO_OCR_Result FOREIGN KEY (id) REFERENCES User (id)
) COMMENT 'OCR결과';

INSERT INTO User (userid, username, password, email, roles)
VALUES ('test', 'JohnDoe', '$2a$10$flj8Cw.17anZaN6l.Wolte0z/J9mfl4WSDVYSyj/NM8OxrcMxJb5S', 'john@example.com', 'admin');
# 비밀번호 = 1234
select * from user;
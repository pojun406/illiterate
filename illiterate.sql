CREATE TABLE User
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    userid   VARCHAR(50) NOT NULL,
    username VARCHAR(10) NOT NULL,
    password  VARCHAR(225) NOT NULL,
    email  VARCHAR(255) NOT NULL,
    resetToken VARCHAR(255) NULL,
    roles      VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) COMMENT '유저 테이블';

CREATE TABLE Board
(
    board_id    BIGINT          NOT NULL AUTO_INCREMENT,
    id          BIGINT          NOT NULL,
    writer      VARCHAR(50)  NOT NULL,
    title       VARCHAR(50)  NOT NULL,
    content     TEXT         NOT NULL,
    request_img VARCHAR(255) NOT NULL,
    reg_date    VARCHAR(50)  NOT NULL,
    del_date    VARCHAR(50)  NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (board_id),
    CONSTRAINT FK_User_TO_Board FOREIGN KEY (id) REFERENCES User (id)
) COMMENT '요청게시판';

CREATE TABLE OCR_Result
(
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    image_path LONGTEXT,
    processed_image_path LONGTEXT,
    result JSON,
    created_at TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT FK_User_TO_OCR_Result FOREIGN KEY (user_id) REFERENCES User (id)
) COMMENT 'OCR결과';

INSERT INTO User (userid, username, password, email, roles)
VALUES ('test', 'JohnDoe', '$2a$10$EJ5IFDV.iroLZGpKslDH9.qJ8A9jjuP6ALlhZVf7I2ixgajCzSNe6', 'john@example.com', 'ROLE_ADMIN');
# 비밀번호 = 1234

select * from user;
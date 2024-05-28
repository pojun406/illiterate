
CREATE TABLE Board
(
    board_id    INT          NOT NULL,
    id          INT          NOT NULL DEFAULT 1,
    writer      VARCHAR(50)  NOT NULL,
    title       VARCHAR(50)  NOT NULL,
    content     TEXT         NOT NULL,
    request_img VARCHAR(255) NOT NULL,
    reg_date    VARCHAR(50)  NOT NULL,
    del_date    VARCHAR(50)  NULL    ,
    PRIMARY KEY (board_id)
) COMMENT '요청게시판';

CREATE TABLE OCR_Result
(
    id         INT          NOT NULL DEFAULT 1,
    ocr_image  VARCHAR(255) NOT NULL,
    ocr_result VARCHAR(255) NOT NULL
) COMMENT 'OCR결과';

CREATE TABLE User
(
    id        INT         NOT NULL DEFAULT 1,
    user_id   VARCHAR(50) NOT NULL,
    user_name VARCHAR(10) NOT NULL,
    password  VARCHAR(50) NOT NULL,
    phonenum  VARCHAR(15) NOT NULL,
    role      VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
) COMMENT '유저 테이블';

ALTER TABLE OCR_Result
    ADD CONSTRAINT FK_User_TO_OCR_Result
        FOREIGN KEY (id)
            REFERENCES User (id);

ALTER TABLE Board
    ADD CONSTRAINT FK_User_TO_Board
        FOREIGN KEY (id)
            REFERENCES User (id);

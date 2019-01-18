CREATE TABLE user
(
id        int          NOT NULL        AUTO_INCREMENT,
username  char(50)     NOT NULL,
password  char(50)     NOT NULL,
name      char(25)     NOT NULL,
gender    char(5)      NOT NULL,
birthday  date         NOT NULL,
phone     char(13)     NULL,
isadmin   int          NOT NULL,
PRIMARY KEY (id)
)
CHARACTER SET utf8 COLLATE utf8_general_ci
ENGINE=InnoDB;

INSERT INTO user(username,password,name,gender,birthday,isadmin)
VALUES ('admin','admin','管理员','男','2019-01-01',1);
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
PRIMARY KEY (id,username)
)
CHARACTER SET utf8 COLLATE utf8_general_ci
ENGINE=InnoDB;

INSERT INTO user(username,password,name,gender,birthday,isadmin)
VALUES ('admin00','admin00','管理员','男','2019-01-01',1);
INSERT INTO user(username,password,name,gender,birthday,isadmin)
VALUES ('admin01','admin01','管理员','女','2019-01-01',1);
INSERT INTO user(username,password,name,gender,birthday,isadmin)
VALUES ('williams','williams','威廉姆斯','男','1989-03-05',0);
INSERT INTO user(username,password,name,gender,birthday,isadmin)
VALUES ('会飞的烟火','071703122','李雷','男','2000-12-31',0);
INSERT INTO user(username,password,name,gender,birthday,isadmin)
VALUES ('悲伤的回忆','221307170','韩梅梅','女','2001-01-01',0);
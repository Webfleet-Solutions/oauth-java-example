CREATE TABLE USERTOKENS
(
    USERID        VARCHAR2(50),
    REFRESH_TOKEN VARCHAR2(4000),
    CREATED       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (USERID, REFRESH_TOKEN)
);
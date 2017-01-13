DROP DATABASE if exists DatabaseCherriew;

CREATE DATABASE DatabaseCherriew;

USE DatabaseCherriew;

CREATE TABLE text_cherriew
(
	uname varchar(255) not null,
	pword varchar(255) not null
);

# just a test case
INSERT INTO text_cherriew (uname, pword) VALUES ('cherriew', 'hello5');
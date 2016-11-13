DROP DATABASE if exists UsersDB;

CREATE DATABASE UsersDB;

USE UsersDB;

CREATE TABLE Users (
  studentID int(11) primary key not null auto_increment,
  username varchar(50) not null,
  password varchar(50) not null
);
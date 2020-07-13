CREATE DATABASE tm_db;

CREATE USER 'tm_db_user'@'localhost' IDENTIFIED BY 'qwe123';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER ON tm_db.* TO 'tm_db_user'@'localhost';
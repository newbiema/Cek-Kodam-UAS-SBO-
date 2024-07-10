CREATE DATABASE kodam_db;
USE kodam_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role ENUM('admin', 'user') NOT NULL
);

//Tambah kodam sama admin dan user

INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('user', 'user123', 'user');

INSERT INTO kodam (name, type) VALUES
('Pocong', 'SSR'),


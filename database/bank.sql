CREATE DATABASE IF NOT EXISTS 3dec;
USE 3dec;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    gender VARCHAR(20),
    balance DOUBLE DEFAULT 0,
    wlimit DOUBLE DEFAULT 10000
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    amount DOUBLE,
    balance DOUBLE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (username)
        REFERENCES users(username)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
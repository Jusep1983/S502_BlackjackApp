CREATE DATABASE IF NOT EXISTS blackjack_app_players;

USE blackjack_app_players;

CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    alias VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    games_tied INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO players (user_name, alias, password, role, games_played, games_won, games_lost, games_tied) VALUES
('Jose', 'Jose', '$2a$10$2waTUSY3jb057ce95ZaBb.SqSzY4zcs1aESzKskZBkjLgFEqiB', 'ADMIN', 10, 2, 7, 1),
('Rakel', 'Rakel', '$2a$10$2waTUSY3jb057ce95ZaBb.SqSzY4zcs1aESzKskZBkjLgFEqiB', 'USER', 5, 3, 1, 0),
('Valeria', 'Valeria', '$2a$10$2waTUSY3jb057ce95ZaBb.SqSzY4zcs1aESzKskZBkjLgFEqiB', 'USER', 8, 5, 2, 1),
('Nerea', 'Nerea', '$2a$10$2waTUSY3jb057ce95ZaBb.SqSzY4zcs1aESzKskZBkjLgFEqiB', 'USER', 8, 5, 3, 0);

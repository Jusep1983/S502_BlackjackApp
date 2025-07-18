CREATE DATABASE IF NOT EXISTS blackjack_app_players;

USE blackjack_app_players;

CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    games_tied INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO players (name, games_played, games_won, games_lost, games_tied) VALUES
('Jose', 10, 2, 7, 1),
('Rakel', 5, 3, 1, 0),
('Valeria', 8, 5, 2, 1),
('Nerea', 8, 5, 3, 0);

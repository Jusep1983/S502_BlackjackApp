package com.jusep1983.blackjack.player.dto;

import java.util.List;

public record PlayerWithGamesDTO(
        String userName,
        String alias,
        int gamesPlayed,
        int gamesWon,
        int gamesLost,
        int gamesTied,
        List<GameSummaryDTO> games
) {}

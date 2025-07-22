package com.jusep1983.blackjack.player.dto;

import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.enums.GameStatus;

import java.time.LocalDateTime;

public record GameSummaryDTO(
        int number,
        String id,
        GameStatus status,
        GameResult result,
        LocalDateTime createdAt
) {}
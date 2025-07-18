package com.jusep1983.blackjack.player;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("players")
public class Player {
    @Id
    @Schema(description = "Unique identifier of the player", example = "1")
    private Long id;

    @Schema(description = "Name of the player", example = "Jose")
    private String name;

    @Column("games_played")
    @Schema(description = "Number of games the player has played", example = "10")
    private int gamesPlayed;

    @Column("games_won")
    @Schema(description = "Number of games the player has won", example = "6")
    private int gamesWon;

    @Column("games_lost")
    @Schema(description = "Number of games the player has loss", example = "6")
    private int gamesLost;

    @Column("games_tied")
    @Schema(description = "Number of games tied", example = "2")
    private int gamesTied;

    @Column("created_at")
    @Schema(description = "Date and time when the player was created", example = "2025-07-05T14:30:00")
    private LocalDateTime createdAt;
}

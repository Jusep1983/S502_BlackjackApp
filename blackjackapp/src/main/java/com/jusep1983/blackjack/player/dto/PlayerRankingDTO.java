package com.jusep1983.blackjack.player.dto;

import com.jusep1983.blackjack.player.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Getter
public class PlayerRankingDTO {
    @Schema(description = "Ranking position of the player", example = "1")
    private int position;
    @Schema(description = "Name of the player", example = "Jose")
    private String userName;
    @Schema(description = "Number of games the player has played", example = "20")
    private int gamesPlayed;
    @Schema(description = "Number of games the player has won", example = "14")
    private int gamesWon;
    @Schema(description = "Number of games the player has lost", example = "4")
    private int gamesLost;
    @Schema(description = "Number of games the player has tied", example = "2")
    private int gamesTied;
    @Schema(description = "Percentage of games the player has won", example = "70.0")
    private String winPercentage;
    @Schema(description = "id of the player", example = "1")
    private Long playerId;

    public PlayerRankingDTO(int position, Player player) {
        this.position = position;
        this.userName = player.getUserName();
        this.gamesPlayed = player.getGamesPlayed();
        this.gamesWon = player.getGamesWon();
        this.gamesLost = player.getGamesLost();
        this.gamesTied = player.getGamesTied();
        double porcentaje = gamesPlayed > 0 ? (gamesWon * 100.0) / gamesPlayed : 0.0;
        this.winPercentage = new BigDecimal(porcentaje)
                                     .setScale(2, RoundingMode.HALF_UP) + "%";
        this.playerId = player.getId();
    }

}

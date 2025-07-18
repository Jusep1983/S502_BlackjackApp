package com.jusep1983.blackjack.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jusep1983.blackjack.deck.Deck;
import com.jusep1983.blackjack.hand.Hand;
import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.enums.GameStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(description = "Game entity representing a Blackjack game session")
@Document(collection = "games_blackJack")
public class Game {

    @Id
    @Schema(description = "Unique identifier of the game", example = "123456789abcdef")
    private String id;
    @Schema(description = "Name of the player", example = "Pasqual")
    private String playerName;
    @Schema(description = "Current status of the game", example = "NEW")
    private GameStatus gameStatus = GameStatus.NO_STATUS;
    @Schema(description = "Date and time when the game was created", example = "2025-07-05T14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @Schema(description = "Cards in the player's hand")
    private Hand playerHand;
    @Schema(description = "Points of the player's hand", example = "18")
    private int playerPoints;
    @Schema(description = "Cards in the dealer's hand")
    private Hand dealerHand;
    @Schema(description = "Points of the dealer's hand", example = "20")
    private int dealerPoints;
    @Schema(description = "Winner of the game: player, dealer or tie", example = "PLAYER_WIN")
    private GameResult gameResult;
    private Deck deck;

    public Game(String playerName) {
        this.playerName = playerName;
        this.createdAt = LocalDateTime.now();
    }

}

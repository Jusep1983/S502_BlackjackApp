package com.jusep1983.blackjack.game.dto;

import com.jusep1983.blackjack.deck.dto.CardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameStatusDTO {
    private String gameStatus;
    private int playerPoints;
    private int dealerPoints;
    private List<CardDTO> playerCards;
    private List<CardDTO> dealerCards;
}

package com.jusep1983.blackjack.deck;

import com.jusep1983.blackjack.shared.enums.Rank;
import com.jusep1983.blackjack.shared.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private Suit suit;
    private Rank rank;

    public int getValue() {
        return rank.getValue();
    }
}



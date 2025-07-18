package com.jusep1983.blackjack.hand;

import com.jusep1983.blackjack.deck.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hand {

    private List<Card> cards = new ArrayList<>();


    public void addCard(Card card) {
        cards.add(card);
    }

    public int getCardCount() {
        return cards.size();
    }

    public boolean isBust(int maxPoints) {
        return getPoints() > maxPoints;
    }

    public int getPoints() {
        // Aquí puedes poner la lógica para calcular puntos en blackjack,
        // teniendo en cuenta Ases, figuras, etc.
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            int cardValue = card.getRank().getValue(); // asumiendo que Rank tiene getValue()
            if (card.getRank().name().equals("ACE")) {
                aces++;
            }
            total += cardValue;
        }

        // Ajuste por Ases: un As puede valer 1 o 11
        while (total > 21 && aces > 0) {
            total -= 10; // Cambio As de 11 a 1
            aces--;
        }
        return total;
    }

    public void clear() {
        cards.clear();
    }
}



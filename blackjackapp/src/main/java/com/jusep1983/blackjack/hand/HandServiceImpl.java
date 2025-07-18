package com.jusep1983.blackjack.hand;

import com.jusep1983.blackjack.shared.enums.Rank;
import com.jusep1983.blackjack.deck.Card;
import org.springframework.stereotype.Service;

@Service
public class HandServiceImpl implements HandService {

    private static final int MAX_POINTS = 21;

    @Override
    public void addCardToHand(Hand hand, Card card) {
        hand.getCards().add(card);
    }

    @Override
    public int calculatePoints(Hand hand) {
        int total = 0;
        int aces = 0;

        for (Card card : hand.getCards()) {
            Rank rank = card.getRank();
            switch (rank) {
                case ACE:
                    aces++;
                    total += 11;
                    break;
                case TWO:
                    total += 2;
                    break;
                case THREE:
                    total += 3;
                    break;
                case FOUR:
                    total += 4;
                    break;
                case FIVE:
                    total += 5;
                    break;
                case SIX:
                    total += 6;
                    break;
                case SEVEN:
                    total += 7;
                    break;
                case EIGHT:
                    total += 8;
                    break;
                case NINE:
                    total += 9;
                    break;
                case TEN:
                case JACK:
                case QUEEN:
                case KING:
                    total += 10;
                    break;
            }
        }

        // Ajustar Ases de 11 a 1 si pasamos de 21
        while (total > MAX_POINTS && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    @Override
    public boolean isBust(Hand hand) {
        return calculatePoints(hand) > MAX_POINTS;
    }

    @Override
    public void clearHand(Hand hand) {
        hand.getCards().clear();
    }

}

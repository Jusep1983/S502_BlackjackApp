package com.jusep1983.blackjack.hand;

import com.jusep1983.blackjack.deck.Card;

public interface HandService {
    void addCardToHand(Hand hand, Card card);
    int calculatePoints(Hand hand);
    boolean isBust(Hand hand);
    void clearHand(Hand hand);
}

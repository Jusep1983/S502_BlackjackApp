package com.jusep1983.blackjack.deck;

public interface DeckService {
    Deck createDeck();
    void shuffleDeck(Deck deck);
    Card drawCard(Deck deck);
}

package com.jusep1983.blackjack.deck;

import com.jusep1983.blackjack.shared.enums.Rank;
import com.jusep1983.blackjack.shared.enums.Suit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DeckServiceImpl implements DeckService {

    @Override
    public Deck createDeck() {
        Suit[] suits = {Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS, Suit.SPADES};
        Rank[] ranks = {
                Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN,
                Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING
        };
        List<Card> cards = new ArrayList<>();
        for (Suit suit : suits) {
            for (Rank rank : ranks) {
                cards.add(new Card(suit, rank));
            }
        }
        return new Deck(cards);
    }

    @Override
    public void shuffleDeck(Deck deck) {
        Collections.shuffle(deck.getCards());
    }

    @Override
    public Card drawCard(Deck deck) {
        if (deck.getCards().isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return deck.getCards().remove(0);
    }

}

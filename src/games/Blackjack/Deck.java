package games.Blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final Random rng = new Random();
    private static final int DECK_COUNT = 6; // Standard 6-deck shoe

    public Deck() {
        initializeDeck();
    }

    private void initializeDeck() {
        String[] suits = { "S", "H", "D", "C" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

        for (int shoe = 0; shoe < DECK_COUNT; shoe++) {
            for (String s : suits) {
                for (String r : ranks) {
                    cards.add(new Card(r, s));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards, rng);
    }

    public Card draw() {
        if (cards.isEmpty())
            throw new IllegalStateException("Deck empty - reshuffle needed");
        return cards.remove(cards.size() - 1);
    }

    public int getCardsRemaining() {
        return cards.size();
    }

    public int getTotalCards() {
        return 52 * DECK_COUNT;
    }
}
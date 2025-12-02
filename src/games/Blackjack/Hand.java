package games.Blackjack;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Card> cards = new ArrayList<>();
    private boolean active = false;

    public void add(Card c) {
        cards.add(c);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Calculate hand value with proper Ace handling
     * Aces start as 11, convert to 1 if needed to avoid bust
     */
    public int getValue() {
        int total = 0;
        int aces = 0;

        // Sum all card values
        for (Card c : cards) {
            total += c.value();
            if ("A".equals(c.getRank()))
                aces++;
        }

        // Convert aces from 11 to 1 until hand is <= 21
        while (total > 21 && aces > 0) {
            total -= 10; // Convert one ace from 11 to 1
            aces--;
        }

        return total;
    }

    /**
     * Check if hand is "soft" (contains an Ace counted as 11)
     */
    public boolean isSoftHand() {
        int total = 0;
        int aces = 0;
        for (Card c : cards) {
            total += c.value();
            if ("A".equals(c.getRank()))
                aces++;
        }
        // Hand is soft if we can subtract 10 and still be valid (meaning an ace is
        // being counted as 11)
        return aces > 0 && (total - 10) > 0 && (total - 10) <= 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean canSplit() {
        if (cards.size() != 2)
            return false;
        return cards.get(0).getRank().equals(cards.get(1).getRank());
    }

    public String showFirstCard() {
        if (cards.isEmpty())
            return "";
        return cards.get(0).toString() + " [?]";
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public int getCardCount() {
        return cards.size();
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0)
                sb.append(" ");
            sb.append(cards.get(i));
        }
        return sb.toString();
    }
}
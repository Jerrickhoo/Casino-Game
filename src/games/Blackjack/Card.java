package games.Blackjack;

public class Card {
    private final String rank;
    private final String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int value() {
        if ("J".equals(rank) || "Q".equals(rank) || "K".equals(rank))
            return 10;
        if ("A".equals(rank))
            return 11;
        return Integer.parseInt(rank);
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + suit;
    }
}
package games.Unlucky9;

import java.util.Random;

public class Unlucky9Logic {
    private Random random = new Random();

    public int[] drawHand(int n) {
        int[] cards = new int[n];
        for (int i = 0; i < n; i++) cards[i] = drawSingle();
        return cards;
    }

    public int drawSingle() { 
        return random.nextInt(9) + 1; 
    }

    public int handValue(int[] cards) {
        int sum = 0;
        for (int c : cards) sum += c;
        return sum % 10;
    }

    public double resolvePayout(double bet, int pv, int dv) {
        if (pv == 9) return bet * 3;
        if (pv > dv) return bet * 2;
        if (pv == dv) return 0;
        return -1;
    }

    public int[] appendCard(int[] arr, int card) {
        int[] out = new int[arr.length + 1];
        System.arraycopy(arr, 0, out, 0, arr.length);
        out[arr.length] = card;
        return out;
    }
}
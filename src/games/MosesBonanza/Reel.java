package games.MosesBonanza;

import java.util.Random;

public class Reel {
    private String[] symbols;
    private int position;
    private Random random = new Random();

    public Reel(String[] symbols) {
        this.symbols = symbols;
    }

    public void spin() {
        position = random.nextInt(symbols.length);
    }

    public String getSymbol() {
        return symbols[position];
    }
}

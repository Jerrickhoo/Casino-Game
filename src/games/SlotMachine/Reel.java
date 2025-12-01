package games.SlotMachine;

import java.util.Random;

public class Reel {
    private String[] symbols;
    private int position;
    private Random rand = new Random();

    public Reel(String[] symbols) {
        this.symbols = symbols;
    }

    public void spin() {
        position = rand.nextInt(symbols.length);
    }

    public String getSymbol() {
        return symbols[position];
    }
}

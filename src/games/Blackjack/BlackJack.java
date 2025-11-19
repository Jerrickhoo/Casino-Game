package games.Blackjack;

import games.Game;

/**
 * Minimal BlackJack stub adapted to Game base class.
 */
public class BlackJack extends Game {

    public BlackJack() {
        super();
    }

    @Override
    public void startGame(Core.Player player, Core.PlayerDatabase playerDB) {
        System.out.println("BlackJack starting (not yet implemented).");
    }

    @Override
    public void playRound() {
        // TODO: implement round logic
    }

    @Override
    public double calculatePayout() {
        return 0;
    }

    @Override
    public void displayRules() {
        System.out.println("Blackjack rules: get 21 without busting.");
    }

    @Override
    public String getGameName() {
        return "BlackJack";
    }

    @Override
    public void updateBalance(double amount) {
        this.balance += amount;
        if (this.player != null)
            this.player.setBalance(this.balance);
    }

}

package games.ChuckALuck;

import games.Game;

/**
 * Minimal Chuck-A-Luck stub adapted to Game base class.
 */
public class ChuckALuck extends Game {

    public ChuckALuck() {
        super();
    }

    @Override
    public void startGame(Core.Player player, Core.PlayerDatabase playerDB) {
        System.out.println("Chuck-A-Luck starting (not yet implemented).");
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
        System.out.println("Chuck-A-Luck rules: roll dice and bet on outcomes.");
    }

    @Override
    public String getGameName() {
        return "ChuckALuck";
    }

    @Override
    public void updateBalance(double amount) {
        this.balance += amount;
        if (this.player != null)
            this.player.setBalance(this.balance);
    }

    @Override
    public void saveGameState() {
        // no-op
    }
}

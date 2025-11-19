package games;

import Core.Player;
import Core.PlayerDatabase;

/**
 * Base abstract class for all casino games.
 * Provides common fields and required game lifecycle methods.
 */
public abstract class Game {
    protected Player player;
    protected double balance;
    protected String gameName;

    public Game() {
    }

    public Game(Player player) {
        this.player = player;
        if (player != null)
            this.balance = player.getBalance();
    }

    // Start the game loop / entry point for instance usage
    // Accepts the current player and the player database for logging/persistence
    public abstract void startGame(Player player, PlayerDatabase playerDB);

    // Play a single round (if applicable)
    public abstract void playRound();

    // Calculate payout for the last played round
    public abstract double calculatePayout();

    // Print/display game rules
    public abstract void displayRules();

    // Human readable game name
    public abstract String getGameName();

    // Update internal balance and (if linked) the player object
    public abstract void updateBalance(double amount);

    // Persist any game-specific state (optional)
    public abstract void saveGameState();
}

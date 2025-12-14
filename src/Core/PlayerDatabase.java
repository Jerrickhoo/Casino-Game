package Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import utilities.Formatter;
import utilities.ConsoleDisplay;
import utilities.InputValidator;
import ui.AnimationDisplay;

public class PlayerDatabase {
    // SortKey is defined as a package-level enum in Core.SortKey
    // Use a TreeMap for ordered username keys (case-insensitive)
    private Map<String, Player> players;
    private static final String DATA_DIRECTORY = "../src/data";
    private String playersFilePath = DATA_DIRECTORY + "/players.txt";
    // Cash limits
    private static final double MIN_CASH = 50.0;
    private static final double MAX_CASH_IN = 10000.0;

    public PlayerDatabase() {
        // Keep usernames ordered and allow case-insensitive lookups/ordering
        this.players = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        // Ensure data directory exists
        File dataDirectory = new File(DATA_DIRECTORY);
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        loadPlayers();
    }

    /**
     * Load players from text file
     */
    private void loadPlayers() {
        File file = new File(playersFilePath);
        if (!file.exists()) {
            System.out.println("Files: No existing player database. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int loadedCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                Player player = Player.fromFileString(line);
                if (player != null) {
                    players.put(player.getUsername(), player);
                    loadedCount++;
                }
            }

            System.out.print("   SUCCESS: Loaded " + loadedCount + " players");
            AnimationDisplay.showLoadingAnimation("", 1500);
            ConsoleDisplay.clearConsole();

        } catch (IOException e) {
            System.out.println("ERROR: Error loading players: " + e.getMessage());
        }
    }

    /**
     * Save all players to text file
     */
    public void savePlayers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(playersFilePath))) {
            // TreeMap.values() returns players in username order
            for (Player player : players.values()) {
                writer.println(player.toFileString());
            }
        } catch (IOException e) {
            System.out.println("ERROR: Error saving players: " + e.getMessage());
        }
    }

    /**
     * Add a new player
     */
    public boolean addPlayer(Player player) {
        if (players.containsKey(player.getUsername())) {
            return false;
        }

        players.put(player.getUsername(), player);
        Transaction.log(player.getUsername(), player.getPlayerId(), "SYSTEM", "ACCOUNT_CREATION",
                player.getBalance(), player.getBalance());
        savePlayers();
        return true;
    }

    /**
     * Get player by username
     */
    public Player getPlayer(String username) {
        return players.get(username);
    }

    /**
     * Authenticate a player using username and password.
     * Returns the Player object on success, or null on failure.
     */
    public Player authenticate(String username, String password) {
        if (username == null || password == null)
            return null;
        Player player = getPlayer(username);
        if (player != null && player.verifyPassword(password)) {
            return player;
        }
        return null;
    }

    /**
     * Update player (auto-saves to file)
     */
    public void updatePlayer(Player player) {
        players.put(player.getUsername(), player);
        savePlayers(); // Auto-save on every update
    }

    /**
     * Check if player exists
     */
    public boolean playerExists(String username) {
        return players.containsKey(username);
    }

    /**
     * Get leaderboard sorted using a specified key and order.
     * This replaces the older bubble-sort implementation with a Comparator based
     * sort.
     */
    public List<Player> getLeaderboard(SortKey sortKey, boolean ascending) {
        // Build a mutable list of players and sort it with a comparator
        List<Player> playerList = new ArrayList<>(players.values());

        Comparator<Player> comparator;
        switch (sortKey) {
            case BALANCE:
                comparator = Comparator.comparingDouble(Player::getBalance);
                break;
            case PLAYER_ID:
                comparator = Comparator.comparing(Player::getPlayerId,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case NAME:
                comparator = Comparator.comparing(Player::getUsername,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case GAMES_PLAYED:
            default:
                comparator = Comparator.comparingInt(Player::getGamesPlayed);
                break;
        }

        // Stable tie-breaker: case-insensitive username
        comparator = comparator.thenComparing(p -> p.getUsername(), String.CASE_INSENSITIVE_ORDER);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        playerList.sort(comparator);
        return playerList;
    }

    /**
     * Interactive leaderboard using the table format from CasinoMain.
     * Centralizes all leaderboard display and sorting here so callers (e.g.,
     * CasinoMain)
     * simply call this single method.
     */
    public void displayLeaderboard() {
        ConsoleDisplay.clearConsole();
        System.out.println("\n\n");
        System.out.println("                 ╔══════════════════════════════════════════════════════════╗");
        System.out.println("                 ║                     TOP PLAYERS                          ║");
        System.out.println("                 ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        // Show default leaderboard first (Balance, descending)
        List<Player> leaderboard = getLeaderboard(SortKey.BALANCE, false);
        printLeaderboardTable(leaderboard);

        // Let the user re-sort or return to previous menu repeatedly
        while (true) {
            System.out.println();
            System.out.println("                         ╔═════════════════════════════════════╗");
            System.out.println("                         ║    Options:                         ║");
            System.out.println("                         ║      1. Sort by other value         ║");
            System.out.println("                         ║      2. Exit                        ║");
            System.out.println("                         ╚═════════════════════════════════════╝");
            System.out.println("                         ╔═════════════════════════════════════╗");
            System.out.print("                              Choose option (1-2): ");
            int next = InputValidator.readInt(1, 2);

            if (next == 2)
                break; // return

            // Ask how they'd like to sort
            ConsoleDisplay.clearConsole();
            System.out.println("\n\n");
            System.out.println("                         ╔═════════════════════════════════════╗");
            System.out.println("                         ║       Sort by:                      ║");
            System.out.println("                         ║       1. Balance                    ║");
            System.out.println("                         ║       2. Player ID                  ║");
            System.out.println("                         ║       3. Name                       ║");
            System.out.println("                         ║       4. Games Played               ║");
            System.out.println("                         ╚═════════════════════════════════════╝");
            System.out.println("                         ╔═════════════════════════════════════╗");
            System.out.print("                              Choose sort option (1-4): ");
            int sortOption = InputValidator.readInt(1, 4);

            ConsoleDisplay.clearConsole();
            System.out.println("\n\n");
            System.out.println("                         ╔═══════════════════════════════════════════╗");
            System.out.println("                         ║      Order:                               ║");
            System.out.println("                         ║      1. Descending (high -> low / Z -> A) ║");
            System.out.println("                         ║      2. Ascending  (low -> high / A -> Z) ║");
            System.out.println("                         ╚═══════════════════════════════════════════╝");
            System.out.println("                         ╔═══════════════════════════════════════════╗");
            System.out.print("                              Choose order (1-2): ");
            int orderOption = InputValidator.readInt(1, 2);

            // Map to sort key enum
            SortKey key;
            switch (sortOption) {
                case 1:
                    key = SortKey.BALANCE;
                    break;
                case 2:
                    key = SortKey.PLAYER_ID;
                    break;
                case 3:
                    key = SortKey.NAME;
                    break;
                case 4:
                    key = SortKey.GAMES_PLAYED;
                    break;
                default:
                    key = SortKey.BALANCE;
                    break;
            }

            boolean ascending = (orderOption == 2);
            leaderboard = getLeaderboard(key, ascending);
            ConsoleDisplay.clearConsole();
            System.out.println("\n\n");
            System.out.println("                 ╔══════════════════════════════════════════════════════════╗");
            System.out.println("                 ║                     TOP PLAYERS                          ║");
            System.out.println("                 ╚══════════════════════════════════════════════════════════╝");
            System.out.println("");
            printLeaderboardTable(leaderboard);
        }
    }

    // helper: centralize leaderboard table formatting used by interactive method
    private void printLeaderboardTable(java.util.List<Player> leaderboard) {
        if (leaderboard == null || leaderboard.isEmpty()) {
            System.out.println("                 No players yet. Be the first to register!");
            return;
        }

        System.out.println(
                "                 ┌──────┬────────────────┬────────────────────┬─────────────────┬──────────┐");
        System.out.println(
                "                 │ Rank │ Player ID      │ Player             │ Balance         │ Games    │");
        System.out.println(
                "                 ├──────┼────────────────┼────────────────────┼─────────────────┼──────────┤");

        for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
            Player player = leaderboard.get(i);
            String rank = (i == 0) ? " " : (i == 1) ? " " : (i == 2) ? " " : " ";
            System.out.printf("                 │ %-4s │ %-12s │ %-18s │ %-15s │ %-8d │\n",
                    rank + (i + 1),
                    player.getPlayerId(),
                    player.getUsername(),
                    Formatter.formatCurrency(player.getBalance()),
                    player.getGamesPlayed());
        }

        System.out.println(
                "                 └──────┴────────────────┴────────────────────┴─────────────────┴──────────┘");
    }

    public boolean cashIn(Player player, double amount) {
        if (player == null || amount < MIN_CASH || amount > MAX_CASH_IN) {
            return false;
        }
        // Update balance
        player.setBalance(player.getBalance() + amount);
        // Log and persist
        Transaction.log(player.getUsername(), player.getPlayerId(), "SYSTEM", "CASH_IN", amount, player.getBalance());
        updatePlayer(player); // saves players
        return true;
    }

    /**
     * Cash out a specific amount from player's balance. Returns true if successful.
     */
    public boolean cashOut(Player player, double amount) {
        if (player == null || amount < MIN_CASH || !player.canAfford(amount)) {
            return false;
        }

        double balance = player.getBalance();
        // Deduct and persist
        player.setBalance(balance - amount);
        Transaction.log(player.getUsername(), player.getPlayerId(), "SYSTEM", "CASH_OUT", amount, player.getBalance());
        updatePlayer(player);
        return true;
    }

    /**
     * Cash out the player's entire balance and return the amount paid out.
     */
    public double cashOutAll(Player player) {
        if (player == null)
            return 0.0;
        double amount = player.getBalance();
        if (amount < MIN_CASH || !player.canAfford(amount))
            return 0.0;

        player.setBalance(0.0);
        Transaction.log(player.getUsername(), player.getPlayerId(), "SYSTEM", "CASH_OUT_ALL", amount,
                player.getBalance());
        updatePlayer(player);
        return amount;
    }

    /**
     * Delete a player account with password verification and cleanup.
     * Returns true if deletion succeeded.
     */
    public boolean deleteAccount(Player player, String password) {
        if (player == null || password == null)
            return false;

        // Verify password as an extra safeguard (UI also verifies before calling)
        if (!player.verifyPassword(password))
            return false;
        // Delegate core deletion logic to deletePlayer(username) to avoid
        // duplicating logging and transaction-cleanup behavior.
        return deletePlayer(player.getUsername());
    }

    /**
     * Delete player by username without password verification (caller must verify).
     * Returns true if removed.
     */
    public boolean deletePlayer(String username) {
        if (username == null)
            return false;
        Player removed = players.remove(username);
        if (removed == null)
            return false;

        // Log deletion and persist
        Transaction.log(removed.getUsername(), removed.getPlayerId(), "SYSTEM", "ACCOUNT_DELETION",
                removed.getBalance(), 0.0);
        savePlayers();

        // Remove transaction history
        Transaction.deleteForPlayer(removed);
        return true;
    }

}
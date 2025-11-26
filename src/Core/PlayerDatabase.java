package Core;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import utilities.Formatter;
import utilities.ConsoleDisplay;
import ui.AnimationDisplay;

public class PlayerDatabase {
    // SortKey is defined as a package-level enum in Core.SortKey
    private Map<String, Player> players;
    private static final String DATA_DIR = "../src/data";
    private String playersFile = DATA_DIR + "/players.txt";
    private String transactionsFile = DATA_DIR + "/transactions.log";

    public PlayerDatabase() {
        this.players = new HashMap<>();
        // Ensure data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        loadPlayers();
    }

    /**
     * Load players from text file
     */
    private void loadPlayers() {
        File file = new File(playersFile);
        if (!file.exists()) {
            System.out.println("📁 No existing player database. Starting fresh.");
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

            System.out.print("✅ Loaded " + loadedCount + " players");
            AnimationDisplay.showLoadingAnimation("", 1500);
            ConsoleDisplay.clearConsole();

        } catch (IOException e) {
            System.out.println("❌ Error loading players: " + e.getMessage());
        }
    }

    /**
     * Save all players to text file
     */
    public void savePlayers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(playersFile))) {
            for (Player player : players.values()) {
                writer.println(player.toFileString());
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving players: " + e.getMessage());
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
        logTransaction(player.getUsername(), "SYSTEM", "ACCOUNT_CREATION",
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
     * Get all players
     */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    /**
     * Get leaderboard sorted using a specified key and order.
     * This replaces the older bubble-sort implementation with a Comparator based
     * sort.
     */
    public List<Player> getLeaderboard(SortKey key, boolean ascending) {
        List<Player> leaderboard = getAllPlayers();

        Comparator<Player> comparator;
        switch (key) {
            case PLAYER_ID:
                comparator = Comparator.comparing(Player::getPlayerId, Comparator.nullsLast(String::compareTo));
                break;
            case NAME:
                comparator = Comparator.comparing(Player::getUsername,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case GAMES_PLAYED:
                comparator = Comparator.comparingInt(Player::getGamesPlayed);
                break;
            case BALANCE:
            default:
                comparator = Comparator.comparingDouble(Player::getBalance);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        leaderboard.sort(comparator);
        return leaderboard;
    }

    /**
     * Backwards compatible method: default to balance DESC (previous behavior)
     */
    public List<Player> getLeaderboard() {
        return getLeaderboard(SortKey.BALANCE, false);
    }

    /**
     * Display formatted leaderboard (default: balance DESC)
     */
    public void displayLeaderboard() {
        displayLeaderboard(SortKey.BALANCE, false);
    }

    /**
     * Display formatted leaderboard using specified sort key and order
     */
    public void displayLeaderboard(SortKey key, boolean ascending) {
        List<Player> leaderboard = getLeaderboard(key, ascending);

        System.out.println("🏆 CASINO LEADERBOARD");
        System.out.println("════════════════════════════");

        if (leaderboard.isEmpty()) {
            System.out.println("No players yet. Be the first to join!");
            return;
        }

        System.out.printf("%-4s %-15s %-12s %-12s%n", "Rank", "Player", "Balance", "Games");
        System.out.println("════════════════════════════");

        for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
            Player player = leaderboard.get(i);
            String medal = "";
            if (i == 0)
                medal = "🥇 ";
            else if (i == 1)
                medal = "🥈 ";
            else if (i == 2)
                medal = "🥉 ";

            System.out.printf("%-4s %-15s %-12s %-12d%n",
                    medal + (i + 1),
                    player.getUsername(),
                    Formatter.formatCurrency(player.getBalance()),
                    player.getGamesPlayed());
        }
    }

    /**
     * Log transaction to transactions.log
     * Format: 2024-01-15 10:30:15 | username | game | action | amount |
     * balance_after
     */
    public void logTransaction(String username, String game, String action, double amount, double balanceAfter) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = String.format("%s | %s | %s | %s | %.2f | %.2f",
                timestamp, username, game, action, amount, balanceAfter);

        try (PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.out.println("❌ Error logging transaction: " + e.getMessage());
        }
    }

    /**
     * Display recent transactions
     */
    public void displayRecentTransactions(int count) {
        File file = new File(transactionsFile);
        if (!file.exists()) {
            System.out.println("No transactions yet.");
            return;
        }

        List<String> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    transactions.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
            return;
        }

        System.out.println("📋 RECENT TRANSACTIONS");
        System.out.println("════════════════════════════");

        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        // Show most recent transactions first
        int startIndex = Math.max(0, transactions.size() - count);
        for (int i = transactions.size() - 1; i >= startIndex; i--) {
            System.out.println(transactions.get(i));
        }
    }

    /**
     * Get player count
     */
    public int getPlayerCount() {
        return players.size();
    }

    public boolean cashIn(Player player, double amount) {
        if (player == null || amount <= 0) {
            return false;
        }
        // Update balance
        player.setBalance(player.getBalance() + amount);
        // Log and persist
        logTransaction(player.getUsername(), "SYSTEM", "CASH_IN", amount, player.getBalance());
        updatePlayer(player); // saves players
        return true;
    }

    /**
     * Cash out a specific amount from player's balance. Returns true if successful.
     */
    public boolean cashOut(Player player, double amount) {
        if (player == null || amount <= 0) {
            return false;
        }
        double balance = player.getBalance();
        if (amount > balance) {
            return false;
        }

        // Deduct and persist
        player.setBalance(balance - amount);
        logTransaction(player.getUsername(), "SYSTEM", "CASH_OUT", amount, player.getBalance());
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
        if (amount <= 0)
            return 0.0;

        player.setBalance(0.0);
        logTransaction(player.getUsername(), "SYSTEM", "CASH_OUT_ALL", amount, player.getBalance());
        updatePlayer(player);
        return amount;
    }

}
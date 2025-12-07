package Core;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import utilities.Formatter;
import utilities.ConsoleDisplay;
import utilities.InputValidator;
import ui.AnimationDisplay;

public class PlayerDatabase {
    // SortKey is defined as a package-level enum in Core.SortKey
    // Use a TreeMap for ordered username keys (case-insensitive)
    private Map<String, Player> players;
    private static final String DATA_DIR = "../src/data";
    private String playersFile = DATA_DIR + "/players.txt";
    private String transactionsFile = DATA_DIR + "/transactions.log";

    public PlayerDatabase() {
        // Keep usernames ordered and allow case-insensitive lookups/ordering
        this.players = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
            // TreeMap.values() returns players in username order
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
        logTransaction(player.getUsername(), player.getPlayerId(), "SYSTEM", "ACCOUNT_CREATION",
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
        // Return a LinkedList to reflect a common familiar structure
        return new LinkedList<>(players.values());
    }

    /**
     * Get leaderboard sorted using a specified key and order.
     * This replaces the older bubble-sort implementation with a Comparator based
     * sort.
     */
    public List<Player> getLeaderboard(SortKey key, boolean ascending) {
        Collection<Player> source = players.values();

        // For BALANCE use a heap (PriorityQueue) for efficient top-N extraction.
        if (key == SortKey.BALANCE) {
            Comparator<Player> balComp = Comparator.comparingDouble(Player::getBalance);
            if (!ascending)
                balComp = balComp.reversed(); // max-heap by reversing comparator
            PriorityQueue<Player> pq = new PriorityQueue<>(balComp);
            pq.addAll(source);
            List<Player> result = new ArrayList<>(pq.size());
            while (!pq.isEmpty())
                result.add(pq.poll());
            return result;
        }

        // For other keys use a TreeSet with tie-breaker on username to preserve all
        // entries
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
            default:
                comparator = Comparator.comparingInt(Player::getGamesPlayed);
                break;
        }

        // Ensure determinism: tie-break by username so TreeSet doesn't drop equal-key
        // players
        comparator = comparator.thenComparing(p -> p.getUsername(), String.CASE_INSENSITIVE_ORDER);
        if (!ascending)
            comparator = comparator.reversed();

        TreeSet<Player> set = new TreeSet<>(comparator);
        set.addAll(source);
        return new ArrayList<>(set);
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
        java.util.List<Player> leaderboard = getLeaderboard(SortKey.BALANCE, false);
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

        System.out.println("                 ┌──────┬────────────────────┬───────────────┬──────────┐");
        System.out.println("                 │ Rank │ Player             │ Balance       │ Games    │");
        System.out.println("                 ├──────┼────────────────────┼───────────────┼──────────┤");

        for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
            Player player = leaderboard.get(i);
            String rank = (i == 0) ? " " : (i == 1) ? " " : (i == 2) ? " " : " ";
            System.out.printf("                 │ %-4s │ %-18s │ %-13s │ %-8d │\n",
                    rank + (i + 1),
                    player.getUsername(),
                    Formatter.formatCurrency(player.getBalance()),
                    player.getGamesPlayed());
        }

        System.out.println("                 └──────┴────────────────────┴───────────────┴──────────┘");
    }

    /**
     * Log transaction to transactions.log
     * New Format: timestamp | username | playerId | game | action | amount |
     * balance_after
     */
    public void logTransaction(String username, String playerId, String game, String action, double amount,
            double balanceAfter) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = String.format("%s | %s | %s | %s | %s | %.2f | %.2f",
                timestamp, username, playerId == null ? "" : playerId, game, action, amount, balanceAfter);

        try (PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.out.println("❌ Error logging transaction: " + e.getMessage());
        }
    }

    /**
     * Read all transactions and return those that belong to the provided player.
     * Matching logic:
     * - If the log entry contains a playerId (7 parts), match by playerId.
     * This ensures players cannot see other players' transactions.
     */
    public java.util.List<String> getTransactionsForPlayer(Player player) {
        java.util.List<String> results = new ArrayList<>();
        File file = new File(transactionsFile);
        if (!file.exists()) {
            return results;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                // Split by pipe separator and trim parts
                String[] parts = line.split("\\|");
                for (int i = 0; i < parts.length; i++)
                    parts[i] = parts[i].trim();

                boolean match = false;
                if (parts.length >= 7) {
                    // timestamp | username | playerId | game | action | amount | balance
                    String entryPlayerId = parts[2];
                    if (player.getPlayerId() != null && player.getPlayerId().equals(entryPlayerId))
                        match = true;
                }

                if (match) {
                    results.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }

        return results;
    }

    /**
     * Display transactions for a player in chronological order (oldest -> newest).
     */
    public void displayTransactionsForPlayer(Player player, int maxEntries) {
        java.util.List<String> allTransactions = getTransactionsForPlayer(player);
        if (allTransactions.isEmpty()) {
            System.out.println("No transactions found for " + player.getUsername() + ".");
            return;
        }

        System.out.println("📋 TRANSACTION HISTORY for " + player.getUsername());
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");

        // Print in the order they occurred (file order)
        int start = Math.max(0, allTransactions.size() - maxEntries);
        for (int i = start; i < allTransactions.size(); i++) {
            System.out.println(allTransactions.get(i));
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
        logTransaction(player.getUsername(), player.getPlayerId(), "SYSTEM", "CASH_IN", amount, player.getBalance());
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
        logTransaction(player.getUsername(), player.getPlayerId(), "SYSTEM", "CASH_OUT", amount, player.getBalance());
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
        logTransaction(player.getUsername(), player.getPlayerId(), "SYSTEM", "CASH_OUT_ALL", amount,
                player.getBalance());
        updatePlayer(player);
        return amount;
    }

}
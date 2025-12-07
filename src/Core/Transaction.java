package Core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;
import java.io.*;
import utilities.Formatter;

/**
 * Simple Transaction model to parse and format transaction log lines.
 */
public class Transaction {
    private Date timestamp;
    private String username;
    private String playerId;
    private String game;
    private String action;
    private double amount;
    private double balanceAfter;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Transaction(Date timestamp, String username, String playerId, String game, String action, double amount,
            double balanceAfter) {
        this.timestamp = timestamp;
        this.username = username;
        this.playerId = playerId;
        this.game = game;
        this.action = action;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public static Transaction fromLogLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            for (int i = 0; i < parts.length; i++)
                parts[i] = parts[i].trim();

            if (parts.length < 7)
                return null;

            Date ts = sdf.parse(parts[0]);
            String username = parts[1];
            String playerId = parts[2].isEmpty() ? null : parts[2];
            String game = parts[3];
            String action = parts[4];
            double amount = Double.parseDouble(parts[5]);
            double balance = Double.parseDouble(parts[6]);

            return new Transaction(ts, username, playerId, game, action, amount, balance);
        } catch (ParseException | NumberFormatException e) {
            return null;
        }
    }

    // --- File I/O helpers ---
    private static final Path TRANSACTIONS_PATH = Paths.get("../src/data/transactions.log");
    private static final Path DATA_DIR = TRANSACTIONS_PATH.getParent();

    private static void ensureDataDir() {
        try {
            if (DATA_DIR != null) {
                Files.createDirectories(DATA_DIR);
            }
        } catch (IOException e) {
            // ignore; callers will handle IO exceptions when writing
        }
    }

    public static void log(String username, String playerId, String game, String action, double amount,
            double balanceAfter) {
        ensureDataDir();
        String timestamp = sdf.format(new Date());
        String logEntry = String.format("%s | %s | %s | %s | %s | %.2f | %.2f",
                timestamp, username, playerId == null ? "" : playerId, game, action, amount, balanceAfter);
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_PATH.toFile(), true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.out.println("⚠️ Failed to write transaction: " + e.getMessage());
        }
    }

    public static List<Transaction> readAll() {
        List<Transaction> out = new ArrayList<>();
        File file = TRANSACTIONS_PATH.toFile();
        if (!file.exists())
            return out;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Transaction t = Transaction.fromLogLine(line);
                if (t != null)
                    out.add(t);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Failed to read transactions: " + e.getMessage());
        }
        return out;
    }

    public static List<Transaction> forPlayer(Player player) {
        List<Transaction> all = readAll();
        List<Transaction> filtered = new ArrayList<>();
        if (player == null)
            return filtered;
        for (Transaction t : all) {
            if (t.getPlayerId() != null && t.getPlayerId().equals(player.getPlayerId()))
                filtered.add(t);
        }
        return filtered;
    }

    public static void displayForPlayer(Player player, int maxEntries) {
        List<Transaction> all = forPlayer(player);
        if (all.isEmpty()) {
            System.out.println("No transactions found for " + (player == null ? "" : player.getUsername()) + ".");
            return;
        }

        System.out.println("📋 TRANSACTION HISTORY for " + player.getUsername());
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");

        int start = Math.max(0, all.size() - maxEntries);
        for (int i = start; i < all.size(); i++) {
            System.out.println(all.get(i).toDisplayString());
        }
    }

    public String toDisplayString() {
        return String.format("%s | %s | %s | %s | %s | %s | %s",
                sdf.format(timestamp), username, playerId == null ? "" : playerId, game, action,
                Formatter.formatCurrency(amount), Formatter.formatCurrency(balanceAfter));
    }

    // Getters
    public Date getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getGame() {
        return game;
    }

    public String getAction() {
        return action;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }
}

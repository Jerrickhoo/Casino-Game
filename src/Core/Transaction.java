package Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // --- File I/O helpers ---
    private static final Path TRANSACTIONS_FILE_PATH = Paths.get("../src/data/transactions.log");
    private static final Path DATA_DIRECTORY = TRANSACTIONS_FILE_PATH.getParent();

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

    public static Transaction fromLogLine(String logLine) {
        try {
            String[] parts = logLine.split("\\|", -1);
            for (int i = 0; i < parts.length; i++)
                parts[i] = parts[i].trim();

            if (parts.length < 7)
                return null;

            Date timestamp = DATE_FORMAT.parse(parts[0]);
            String username = parts[1];
            String playerId = parts[2].isEmpty() ? null : parts[2];
            String game = parts[3];
            String action = parts[4];
            double amount = Double.parseDouble(parts[5]);
            double balanceAfter = Double.parseDouble(parts[6]);

            return new Transaction(timestamp, username, playerId, game, action, amount, balanceAfter);
        } catch (ParseException | NumberFormatException e) {
            return null;
        }
    }

    private static void ensureDataDirectory() {
        try {
            if (DATA_DIRECTORY != null) {
                Files.createDirectories(DATA_DIRECTORY);
            }
        } catch (IOException e) {
            // ignore; callers will handle IO exceptions when writing
        }
    }

    public static void log(String username, String playerId, String game, String action, double amount,
            double balanceAfter) {
        ensureDataDirectory();
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("%s | %s | %s | %s | %s | %.2f | %.2f",
                timestamp, username, playerId == null ? "" : playerId, game, action, amount, balanceAfter);
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE_PATH.toFile(), true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to write transaction: " + e.getMessage());
        }
    }

    public static List<Transaction> readAll() {
        List<Transaction> transactions = new ArrayList<>();
        File file = TRANSACTIONS_FILE_PATH.toFile();
        if (!file.exists())
            return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Transaction transaction = Transaction.fromLogLine(line);
                if (transaction != null)
                    transactions.add(transaction);
            }
        } catch (IOException e) {
            System.out.println("ERROR: Failed to read transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static List<Transaction> forPlayer(Player player) {
        List<Transaction> allTransactions = readAll();
        List<Transaction> filteredTransactions = new ArrayList<>();
        if (player == null)
            return filteredTransactions;
        for (Transaction transaction : allTransactions) {
            if (transaction.playerId != null && transaction.playerId.equals(player.getPlayerId()))
                filteredTransactions.add(transaction);
        }
        return filteredTransactions;
    }

    /**
     * Delete all transaction log entries for the specified player.
     * Returns true if operation succeeded.
     */
    public static boolean deleteForPlayer(Player player) {
        if (player == null)
            return false;

        ensureDataDirectory();
        File sourceFile = TRANSACTIONS_FILE_PATH.toFile();
        if (!sourceFile.exists())
            return true; // nothing to do

        File temporaryFile = new File(sourceFile.getAbsolutePath() + ".tmp");
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
                PrintWriter writer = new PrintWriter(new FileWriter(temporaryFile))) {
            String line;
            String username = player.getUsername();
            String playerId = player.getPlayerId();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 3) {
                    String userNameField = parts[1].trim();
                    String playerIdField = parts[2].trim();
                    if (userNameField.equals(username)
                            || (!playerIdField.isEmpty() && playerIdField.equals(playerId))) {
                        // skip this line (delete)
                        continue;
                    }
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("ERROR: Failed to delete transactions for player: " + e.getMessage());
            if (temporaryFile.exists())
                temporaryFile.delete();
            return false;
        }

        // Replace original file with cleaned temporary file
        try {
            Path sourcePath = sourceFile.toPath();
            Path temporaryPath = temporaryFile.toPath();
            Files.delete(sourcePath);
            Files.move(temporaryPath, sourcePath);
            return true;
        } catch (IOException e) {
            System.out.println("ERROR: Failed to finalize transaction deletion: " + e.getMessage());
            return false;
        }
    }

    public static void displayForPlayer(Player player, int maxEntries) {
        List<Transaction> allTransactions = forPlayer(player);
        if (allTransactions.isEmpty()) {
            System.out.println("No transactions found for " + (player == null ? "" : player.getUsername()) + ".");
            return;
        }

        System.out.println(" TRANSACTION HISTORY for " + player.getUsername());
        System.out.println(" ╔═══════════════════════════════════════════════════════════════════════════════════════════╗");

        int startIndex = Math.max(0, allTransactions.size() - maxEntries);
        for (int i = startIndex; i < allTransactions.size(); i++) {
            System.out.println(allTransactions.get(i).toDisplayString());
        }
    }

    public String toDisplayString() {
        return String.format(" %s | %s | %s | %s | %s | %s | %s",
                DATE_FORMAT.format(timestamp), username, playerId == null ? "" : playerId, game, action,
                Formatter.formatCurrency(amount), Formatter.formatCurrency(balanceAfter));
    }
}

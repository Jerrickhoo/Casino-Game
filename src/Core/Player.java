package Core;

import utilities.Formatter;

public class Player {
    private String username;
    private String password;
    private double balance;
    private String playerId;
    private int gamesPlayed;

    // Constructor for new players
    public Player(String username, String password, double initialBalance) {
        this.username = username;
        this.password = password;
        this.balance = initialBalance;
        this.playerId = generatePlayerId();
        this.gamesPlayed = 0;
    }

    // Constructor for loading from file
    public Player(String username, String password, double balance,
            String playerId, int gamesPlayed) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.playerId = playerId;
        this.gamesPlayed = gamesPlayed;
    }

    // Generate player ID in format: IDyyyyMMddHHmm (e.g., ID202503231530)
    private String generatePlayerId() {
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter
                .ofPattern("yyyyMMddHHmm");
        String datetime = java.time.LocalDateTime.now().format(dateTimeFormatter);
        return "ID" + datetime;
    }

    // Convert to file format: username:password:balance:playerId:gamesPlayed
    public String toFileString() {
        return String.join(":",
                username,
                password,
                String.format("%.2f", balance), // Keep 2 decimal places
                playerId,
                String.valueOf(gamesPlayed));
    }

    // Create from file string
    public static Player fromFileString(String fileString) {
        try {
            String[] parts = fileString.split(":");
            if (parts.length != 5) {
                return null;
            }

            return new Player(
                    parts[0], // username
                    parts[1], // password
                    Double.parseDouble(parts[2]), // balance
                    parts[3], // playerId
                    Integer.parseInt(parts[4]) // gamesPlayed
            );
        } catch (Exception e) {
            System.out.println("Error parsing player: " + fileString);
            return null;
        }
    }

    // Verify password
    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Update games played
    public void updateGamesPlayed() {
        this.gamesPlayed++;
    }

    // Get player stats
    public String getPlayerStats() {
        return String.format(
                "Player: %s\n" +
                        "ID: %s\n" +
                        "Balance: %s\n" +
                        "Games Played: %d",
                username, playerId, Formatter.formatCurrency(balance), gamesPlayed);
    }

    // Check if player can afford a bet
    public boolean canAfford(double amount) {
        return balance >= amount;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    // Setters
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Validation methods
    public static boolean isValidUsername(String username) {
        return username != null &&
                username.length() >= 3 &&
                username.length() <= 20 &&
                !username.contains(":"); // No colon in username (would break file format)
    }

    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 4 &&
                password.length() <= 30 &&
                !password.contains(":"); // No colon in password
    }
}
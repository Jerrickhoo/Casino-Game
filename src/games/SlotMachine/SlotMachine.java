package games.SlotMachine;

import java.util.Random;
import java.util.Scanner;
import games.Game;
import Core.Player;
import Core.PlayerDatabase;

public class SlotMachine extends Game {
    private int balance;
    private final Scanner scanner = new Scanner(System.in);

    public SlotMachine(double playerBalance) {
        super();
        this.balance = (int) playerBalance;
    }

    public double start() {
        System.out.println("=====================================");
        System.out.println("     Welcome to Moses Bonanza!");
        System.out.println("=====================================");

        while (true) {
            System.out.println("Current Balance: P" + balance);
            System.out.println("[1] Play");
            System.out.println("[2] Exit to Casino Menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                playGame();
            } else if (choice.equals("2")) {
                System.out.println("Returning to Casino...");
                return balance;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    private void playGame() {
        if (balance <= 0) {
            System.out.println("You have no balance! Please cash in first.");
            return;
        }

        System.out.print("Place bet: P");
        int bet = scanner.nextInt();
        scanner.nextLine();

        if (bet > balance || bet <= 0) {
            System.out.println("Invalid bet amount!");
            return;
        }

        balance -= bet;

        String[] row = spinRow();
        displayRow(row);
        int payout = getPayout(row, bet);

        if (payout > 0) {
            balance += payout;
            System.out.println("Paldo! You won P" + payout);
        } else {
            System.out.println("Oops, you lost!");
        }
    }

    private String[] spinRow() {
        String[] numbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        String[] row = new String[3];
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            row[i] = numbers[random.nextInt(numbers.length)];
        }
        return row;
    }

    private void displayRow(String[] row) {
        System.out.println("---------------------");
        System.out.println(" " + String.join(" | ", row));
        System.out.println("---------------------");
    }

    private int getPayout(String[] row, int bet) {
        if (row[0].equals(row[1]) && row[0].equals(row[2])) {
            return bet * 3;
        }
        return 0;
    }

    // --- Implement abstract Game methods (minimal wrappers) ---
    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        // Create a slot instance initialized with the player's balance,
        // run the existing start loop, then persist the updated balance.
        SlotMachine slot = new SlotMachine(player.getBalance());
        double newBalance = slot.start();
        player.setBalance(newBalance);
        if (playerDB != null)
            playerDB.updatePlayer(player);
    }

    @Override
    public void playRound() {
        playGame();
    }

    @Override
    public double calculatePayout() {
        // SlotMachine's payout is computed per spin; not tracked here
        return 0;
    }

    @Override
    public void displayRules() {
        System.out.println("Slot Machine: match 3 symbols to win. Triple match pays 3x.");
    }

    @Override
    public String getGameName() {
        return "SlotMachine";
    }

    @Override
    public void updateBalance(double amount) {
        this.balance += (int) amount;
        if (this.player != null)
            this.player.setBalance(this.balance);
    }

    @Override
    public void saveGameState() {
        // No-op; CasinoMain/playerDB handles persistence
    }
}

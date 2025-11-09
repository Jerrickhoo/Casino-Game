package games.SlotMachine;

import java.util.Random;
import java.util.Scanner;

public class SlotMachine {
    private int balance;
    private final Scanner scanner = new Scanner(System.in);

    public SlotMachine(double playerBalance) {
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
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
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
}

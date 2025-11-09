package games.SlotMachine;

import java.util.Random;
import java.util.Scanner;

public class SlotMachine {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int balance = 0;
        int bet;
        int payout;
        String[] row;

        System.out.println("=====================================");
        System.out.println("     Welcome to Moses Bonanza!");
        System.out.println("=====================================");

        while (true) {
            System.out.println("             MAIN MENU");
            System.out.println("-------------------------------------");
            System.out.println("[1] Cash In");
            System.out.println("[2] Play");
            System.out.println("[3] Exit");
            System.out.println("-------------------------------------");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            System.out.println("-------------------------------------");

            switch (choice) {
                case "1": // 💵 Cash in
                    System.out.print("Enter cash-in amount: P");
                    int cashIn = scanner.nextInt();
                    scanner.nextLine();

                    if (cashIn > 0) {
                        balance += cashIn;
                        System.out.println("Successfully cashed-in P" + cashIn + "!");
                        System.out.println("=====================================");
                    } else {
                        System.out.println("Invalid amount.");
                        System.out.println("=====================================");
                    }
                    break;

                case "2": // 🎰 Play game
                    if (balance <= 0) {
                        System.out.println("You have no balance! Please cash in first.");
                        System.out.println("=====================================");
                        break;
                    }

                    while (balance > 0) {
                        System.out.println("Current Balance: P" + balance);
                        System.out.print("Place bet: P");
                        bet = scanner.nextInt();
                        scanner.nextLine();

                        if (bet > balance) {
                            System.out.println("Insufficient balance. Please cash in more money!");
                            System.out.println("=====================================");
                            continue;
                        } else if (bet <= 0) {
                            System.out.println("Bet must be greater than zero!");
                            System.out.println("=====================================");
                            continue;
                        } else {
                            balance -= bet;
                        }

                        System.out.println("----------------------------------");
                        System.out.println("Spinning...");
                        row = spinRow();
                        displayRow(row);
                        payout = getPayout(row, bet);

                        if (payout > 0) {
                            balance += payout;
                            System.out.println("Paldo! You won P" + payout);
                            System.out.println("=====================================");
                        } else {
                            System.out.println("Oops, you lost");
                            System.out.println("=====================================");
                        }

                        if (balance <= 0) {
                            System.out.println("----------------------------------");
                            System.out.println("You ran out of money!");
                            break;
                        }

                        System.out.print("Play again? (y/n): ");
                        String again = scanner.nextLine().trim().toLowerCase();
                        if (!again.equals("y")) {
                            break;
                        }
                        System.out.println("=====================================");
                    }
                    break;

                case "3": // 🚪 Exit game
                    System.out.println("\n==================================");
                    System.out.println("Thanks for playing Moses Bonanza!");
                    System.out.println("Final Balance: P" + balance);
                    System.out.println("==================================");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option! Please choose 1, 2, or 3.");
                    System.out.println("=====================================");
            }
        }
    }

    // 🎰 Spins and returns a row of 3 random numbers
    static String[] spinRow() {
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] row = new String[3];
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            row[i] = numbers[random.nextInt(numbers.length)];
        }
        return row;
    }

    // 🎞 Displays the slot row
    static void displayRow(String[] row) {
        System.out.println("----------------------------------");
        System.out.println(" " + String.join(" | ", row));
        System.out.println("-----------------------------------");
    }

    // 💸 Calculates payout based on matching results
    static int getPayout(String[] row, int bet) {
        if (row[0].equals(row[1]) && row[0].equals(row[2])) {
            return switch (row[0]) {
                case "1" -> bet * 2;
                case "2" -> bet * 3;
                case "3" -> bet * 4;
                case "4" -> bet * 5;
                case "5" -> bet * 10;
                case "6" -> bet * 20;
                case "7" -> bet * 50;
                case "8" -> bet * 100;
                case "9" -> bet * 500;
                default -> 0;
            };
        } else if (row[0].equals(row[1]) || row[1].equals(row[2]) || row[0].equals(row[2])) {
            return bet; // small reward for 2 matches
        }
        return 0;
    }
}

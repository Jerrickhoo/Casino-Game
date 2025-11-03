package utilities;

import java.util.Scanner;

public class utilities {
    private static final Scanner scanner = new Scanner(System.in);

    // * ==================== INPUT VALIDATION ====================
    public static int readInt(int min) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min) {
                    return value;
                } else {
                    System.out.print("Please enter a number greater than or equal to " + min + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    public static int readInt(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
            }
        }
    }

    public static double readDouble(double min) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                } else {
                    System.out.print("Please enter a number greater than or equal to " + min + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    public static double readDouble(double min, double max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
            }
        }
    }

    public static String readString() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("Input cannot be empty. Please try again: ");
        }
    }

    public static boolean readYesNo() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            } else if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            System.out.print("Please enter Y/N: ");
        }
    }

    // * ==================== FORMATTING METHODS ====================
    public static String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }

    // * ==================== Animations ====================
    public static void showProgressBar(String message, int durationMs) {
        System.out.print(message + " [");
        int bars = 20;
        for (int i = 0; i < bars; i++) {
            System.out.print("=");
            pause(durationMs / bars);
        }
        System.out.println("] Complete!");
    }

    // * ==================== CONSOLE OPERATIONS ====================
    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name").toLowerCase();

            if (operatingSystem.contains("windows")) {
                // For Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Unix-based systems (macOS, Linux)
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, print several newlines as a fallback
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static void pause(long milliseconds, String message) {
        if (milliseconds <= 0) {
            // Wait for Enter key
            System.out.print(message != null ? message : "Press Enter to continue...");
            scanner.nextLine();
        } else {
            // Show message if provided
            if (message != null) {
                System.out.println(message);
            }
            // Wait for specified duration
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void pause(long milliseconds) {
        pause(milliseconds, null);
    }

    public static void pause(String message) {
        pause(0, message);
    }

    public static void pause() {
        pause(0, null);
    }

    public static void closeScanner() {
        scanner.close();
    }
}

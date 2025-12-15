package utilities;

import java.util.Scanner;

public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);

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
                    System.out.print("                                                Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("                                                Invalid input. Please enter a number between " + min + " and " + max + ": ");
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

    public static void waitForUserInput() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    public static void waitForUserInput(String message) {
        System.out.print(message);
        scanner.nextLine();
    }

    /**
     * Read a double value, but allow "exit" command to cancel.
     * Returns Double.MIN_VALUE if user enters "exit" (special sentinel value).
     */
    public static double readDoubleOrExit(double min) {
        while (true) {
            try {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("exit")) {
                    return Double.MIN_VALUE; // sentinel value for exit
                }
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                } else {
                    System.out.print(
                            "Please enter a number greater than or equal to " + min + " (or 'exit' to cancel): ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number (or 'exit' to cancel): ");
            }
        }
    }

    public static void closeScanner() {
        scanner.close();
    }
}

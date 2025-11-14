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

    public static void waitForUserInput() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    public static void waitForUserInput(String message) {
        System.out.print(message);
        scanner.nextLine();
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

    public static void startUpGameAnimation(boolean isOn) {

        if(isOn == true){

        //first frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██                                                                                  ██                 ║");
        System.out.println("            ║                 ██                                                                                  ██                 ║");
        System.out.println("            ║                 ██                                                                                  ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                     ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                   ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                           ║");
        System.out.println("            ║                   ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                    ║");
        System.out.println("            ║                   ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                    ║");
        System.out.println("            ║                     ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(500);
        utilities.clearConsole();

        utilities.clearConsole();
        //second frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ████                                                                                ██                 ║");
        System.out.println("            ║                 ██                                                                                  ██                 ║");
        System.out.println("            ║                 ██                                                                                  ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                     ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                   ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                           ║");
        System.out.println("            ║                   ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                    ║");
        System.out.println("            ║                   ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                    ║");
        System.out.println("            ║                     ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(500);
        utilities.clearConsole();

        //second frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██████                                                                              ██                 ║");
        System.out.println("            ║                 ████                                                                                ██                 ║");
        System.out.println("            ║                 ██                                                                                  ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                     ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                   ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                           ║");
        System.out.println("            ║                   ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                    ║");
        System.out.println("            ║                   ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                    ║");
        System.out.println("            ║                     ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(500);
        utilities.clearConsole();

        //third frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ████████                                                                            ██                 ║");
        System.out.println("            ║                 ██████                                                                              ██                 ║");
        System.out.println("            ║                 ████                                                                                ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                     ██████  ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                     ██          ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                           ║");
        System.out.println("            ║                     ████      ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                    ║");
        System.out.println("            ║                         ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                    ║");
        System.out.println("            ║                     ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                      ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(500);
        utilities.clearConsole();

        //4th frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ████████████                                                                        ██                 ║");
        System.out.println("            ║                 ██████████                                                                          ██                 ║");
        System.out.println("            ║                 ████████                                                                            ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██    ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║                   ██  ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                       ║");
        System.out.println("            ║                   ██  ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                ║");
        System.out.println("            ║                   ██  ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                ║");
        System.out.println("            ║                   ██    ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(500);
        utilities.clearConsole();

        //5th frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ████████████████████                                                                ██                 ║");
        System.out.println("            ║                 ██████████████████                                                                  ██                 ║");
        System.out.println("            ║                 ████████████████                                                                    ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║               ████      ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║              █    ██  ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                       ║");
        System.out.println("            ║                 ██    ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                ║");
        System.out.println("            ║               ██      ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                ║");
        System.out.println("            ║              ██████     ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(1000);
        utilities.clearConsole();

        //6th frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██████████████████████████████████████████                                          ██                 ║");
        System.out.println("            ║                 ████████████████████████████████████████                                            ██                 ║");
        System.out.println("            ║                 ██████████████████████████████████████                                              ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║               ██████    ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║               ██      ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                       ║");
        System.out.println("            ║               ████    ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                ║");
        System.out.println("            ║                   ██  ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                ║");
        System.out.println("            ║               ████      ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(1000);
        utilities.clearConsole();

        //7th frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ████████████████████████████████████████████████████████████████████                ██                 ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████                  ██                 ║");
        System.out.println("            ║                 ████████████████████████████████████████████████████████████████                    ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║               ████      ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║             ██    ██  ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                       ║");
        System.out.println("            ║               ████    ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                ║");
        System.out.println("            ║             ██    ██  ██    ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                ║");
        System.out.println("            ║               ████      ████    ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(1000);
        utilities.clearConsole();

        //8th frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████  ██                 ║");
        System.out.println("            ║                 ████████████████████████████████████████████████████████████████████████████████    ██                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║              ██████    ██████   ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║             ██    ██  ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                       ║");
        System.out.println("            ║              ███████   ███████    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████                ║");
        System.out.println("            ║                   ██        ██  ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██                ║");
        System.out.println("            ║                   ██        ██  ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████                  ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(1500);
        utilities.clearConsole();

        //9th frame
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║           ██    ████      ████      ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████              ║");
        System.out.println("            ║           ██  ██    ██  ██    ██        ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                   ║");
        System.out.println("            ║           ██  ██    ██  ██    ██      ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████            ║");
        System.out.println("            ║           ██  ██    ██  ██    ██    ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██            ║");
        System.out.println("            ║           ██    ████      ████      ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████              ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(2500);
        utilities.clearConsole();
            
        utilities.pause(1500);
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║              ████            ████    ████        ████    ████       ████        ████████                               ║");
        System.out.println("            ║              ████            ████    ████        ████    ████       ████      ████████████                             ║");
        System.out.println("            ║              ████            ████    ███████     ████    ████       ████    ████        ████                           ║");
        System.out.println("            ║              ████            ████    ████████████████    ████      ████     ████        ████                           ║");
        System.out.println("            ║              ████            ████    ████     ███████    ████   ████        ████        ████                           ║");
        System.out.println("            ║              ████            ████    ████        ████    ████████           ████        ████                           ║");
        System.out.println("            ║              ████            ████    ████        ████    ████   ████        ████████████████                           ║");
        System.out.println("            ║              ████            ████    ████        ████    ████      ████     ████        ████                           ║");
        System.out.println("            ║              ████            ████    ████        ████    ████       ████    ████        ████                           ║");
        System.out.println("            ║              ████████████████████    ████        ████    ████       ████    ████        ████                           ║");
        System.out.println("            ║               ██████████████████     ████        ████    ████       ████    ████        ████                           ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                   ███ █████ █  █ ███   █  ██           ║");
        System.out.println("            ║                                                                                  █      █   █  █ █  █  █ █  █          ║");
        System.out.println("            ║                                                                                   ██    █   █  █ █   █ █ █  █          ║");
        System.out.println("            ║                                                                                     █   █   █  █ █  █  █ █  █          ║");
        System.out.println("            ║                                                                                  ███    █   ████ ███   █  ██           ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(2500);
        utilities.clearConsole();

        } else{
            //skip animation
        }

        /*
         * Splash arts
         * Ideas:
         * 1. Unka studio
         * 2.  
         */
    }

    public static void loginAnimation(boolean isOn) {
    if(isOn == true){
        // Frame 1
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                              ██████                                                                    ║");
        System.out.println("            ║                                              ██████                                                                    ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(700);
        utilities.clearConsole();

        // Frame 2
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                        ██████████████████████████                                                      ║");
        System.out.println("            ║                                        ██                  ██                                                          ║");
        System.out.println("            ║                                        ██      ██████      ██                                                          ║");
        System.out.println("            ║                                        ██      ██████      ██                                                          ║");
        System.out.println("            ║                                        ██                  ██                                                          ║");
        System.out.println("            ║                                        ██████████████████████████                                                      ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(700);
        utilities.clearConsole();

        // Frame 3
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                      ████████████████████████████████                                                  ║");
        System.out.println("            ║                                    ████████████████████████████████████                                                ║");
        System.out.println("            ║                                  ██████    ██████████████████    ██████                                                ║");
        System.out.println("            ║                                ██████          ██████████          ██████                                              ║");
        System.out.println("            ║                                ██████          ██████████          ██████                                              ║");
        System.out.println("            ║                                  ██████    ██████████████████    ██████                                                ║");
        System.out.println("            ║                                    ████████████████████████████████████                                                ║");
        System.out.println("            ║                                      ████████████████████████████████                                                  ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(700);
        utilities.clearConsole();

        // Frame 4
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██    ████    ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████ ██                 ║");
        System.out.println("            ║                 ██  ██    ██      ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██      ██                 ║");
        System.out.println("            ║                 ██  ██    ██    ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   █████                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(700);
        utilities.clearConsole();

        // Frame 5
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                 ██████████████████████████████████████████████████████████████████████████████████████                 ║");
        System.out.println("            ║                   ██████████████████████████████████████████████████████████████████████████████████                   ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║           ██    ████      ████      ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████              ║");
        System.out.println("            ║           ██  ██    ██  ██    ██        ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                   ║");
        System.out.println("            ║           ██  ██    ██  ██    ██      ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████            ║");
        System.out.println("            ║           ██  ██    ██  ██    ██    ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██            ║");
        System.out.println("            ║           ██    ████      ████      ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████              ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(800);
        utilities.clearConsole();

        // Frame 6
        System.out.println("            ╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                 *    ██████████████████████████████████████████████████████████████████████████████████    *           ║");
        System.out.println("            ║                     ██████████████████████████████████████████████████████████████████████████████████████             ║");
        System.out.println("            ║              *     ██████████████████████████████████████████████████████████████████████████████████████     *        ║");
        System.out.println("            ║                     ██████████████████████████████████████████████████████████████████████████████████████             ║");
        System.out.println("            ║                 *    ██████████████████████████████████████████████████████████████████████████████████    *           ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║           ██    ████      ████      ██  ██    ██        ████     █████   █████    ██  ██      ██   ██████              ║");
        System.out.println("            ║           ██  ██    ██  ██    ██        ██    ██      ██    ██  ██   ██  ██   ██  ██  ████    ██  ██                   ║");
        System.out.println("            ║           ██  ██    ██  ██    ██      ██      ██      ██    ██  ███████  ██   ██  ██  ██  ██  ██  ██   ████            ║");
        System.out.println("            ║           ██  ██    ██  ██    ██    ██        ██      ██    ██  ██   ██  ██   ██  ██  ██    ████  ██     ██            ║");
        System.out.println("            ║           ██    ████      ████      ██  ██    ██████    ████    ██   ██  █████    ██  ██      ██   ██████              ║");
        System.out.println("            ║                                                                                                                        ║");
        System.out.println("            ║                                         *** Users READY * WELCOME TO GAME HUB ***                                      ║");
        System.out.println("            ╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(1500);
    }
}

public static void qrCodeCashIn() {
        System.out.println("            ╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("            ║                                                                             ║");
        System.out.println("            ║             ██████████████       ██    ██████    ██████████████             ║");
        System.out.println("            ║             ██          ██  ██   ██  ██  ██      ██          ██             ║");
        System.out.println("            ║             ██  ██████  ██       ██  ██          ██  ██████  ██             ║");
        System.out.println("            ║             ██  ██████  ██         ██        ██  ██  ██████  ██             ║");
        System.out.println("            ║             ██  ██████  ██    ████       ██████  ██  ██████  ██             ║");
        System.out.println("            ║             ██          ██          ██    ██  ██ ██          ██             ║");
        System.out.println("            ║             ██████████████      ██  ██           ██████████████             ║");
        System.out.println("            ║                               ████         ██         ████                  ║");
        System.out.println("            ║             ████        ████████████                  ██                    ║");
        System.out.println("            ║             ██████████████████         ████       ██        ██              ║");
        System.out.println("            ║             ██  ██                ██   ██     ██████  ████████              ║");
        System.out.println("            ║             ██             ██  ██        ██     ██                          ║");
        System.out.println("            ║                    ████████████████      ██                                 ║");
        System.out.println("            ║                ████████                  ████               ██              ║");
        System.out.println("            ║             ██████████████    ██████            ████████████                ║");
        System.out.println("            ║             ██          ██    ██      ████      ██      ██                  ║");
        System.out.println("            ║             ██  ██████  ██  ██        ██        ██  ██  ██                  ║");
        System.out.println("            ║             ██  ██████  ██            ██        ██      ██                  ║");
        System.out.println("            ║             ██  ██████  ██      ██      ██     ████████████                 ║");
        System.out.println("            ║             ██          ██    ██  ██  ██        ██      ████                ║");
        System.out.println("            ║             ██████████████    ████              ████    ██████              ║");
        System.out.println("            ║                                                                             ║");
        System.out.println("            ║                                                                             ║");
        System.out.println("            ║                  S C A N   T O   C A S H   I N   M O N E Y                  ║");
        System.out.println("            ╚═════════════════════════════════════════════════════════════════════════════╝");
        utilities.pause(1000);
    }

    

    public static void showLoadingAnimation(String message, int durationMs) {
        String[] frames = { "-", "\\", "|", "/" };
        int totalFrames = (durationMs / 100); // Update every 100ms

        // Hide the cursor and save position
        System.out.print(message + " ");

        try {
            for (int i = 0; i < totalFrames; i++) {
                String frame = frames[i % frames.length];
                System.out.print("\r" + message + " " + frame); // \r moves cursor to start of line
                Thread.sleep(100);
            }
            // Clear the animation and show completion
            System.out.print("\r" + message + " Done!     \n");

        } catch (InterruptedException e) {
            // In case of interruption, ensure we still clear the line
            System.out.print("\r" + message + " Done!     \n");
            Thread.currentThread().interrupt();
        }
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

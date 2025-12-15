package games.Unlucky9;

import utilities.InputValidator;
import utilities.Formatter;

public class Unlucky9UI {

    // ===== BOXED INPUT METHODS =====
    public int boxedIntInput(String label, int min, int max) {
        printTop();
        printLine(label);
        System.out.print(Unlucky9Constants.LEFT_MARGIN + "║ > ");
        int value = InputValidator.readInt(min, max);
        printBot();
        return value;
    }

    public double boxedDoubleInput(String label, double min, double max) {
        while (true) {
            printTop();
            printLine(label);
            printLine("Bet Range: $" + String.format("%.1f", min) + " - " + Formatter.formatCurrency(max));
            System.out.print(Unlucky9Constants.LEFT_MARGIN + "║ > ");
            
            try {
                String input = InputValidator.readString();
                double value = Double.parseDouble(input);
                
                if (value >= min && value <= max) {
                    printBot();
                    return value;
                } else {
                    printBot();
                    boxedMessage("ERROR: Bet must be between $" + String.format("%.1f", min) + 
                                " and " + Formatter.formatCurrency(max));
                    waitForInput("Press Enter...");
                }
            } catch (NumberFormatException e) {
                printBot();
                boxedMessage("ERROR: Invalid input. Please enter a valid number.");
                waitForInput("Press Enter...");
            }
        }
    }

    public boolean boxedYesNoInput(String label) {
        printTop();
        printLine(label);
        System.out.print(Unlucky9Constants.LEFT_MARGIN + "║ > ");
        boolean value = InputValidator.readYesNo();
        printBot();
        return value;
    }

    public void boxedMessage(String msg) {
        printTop();
        printLine(msg);
        printBot();
    }

    public void waitForInput(String message) {
        InputValidator.waitForUserInput(Unlucky9Constants.LEFT_MARGIN + message);
    }

    // ===== DISPLAY =====
    public void displayHands(int[] p, int[] d, int pv, int dv) {
        printTop();
        printLine("PLAYER HAND");
        printLine(formatHand(p) + " => " + pv);
        printMid();
        printLine("DEALER HAND");
        printLine(formatHand(d) + " => " + dv);
        printBot();
    }

    public void displayPlayerWithOneDealer(int[] p, int[] d, int playerValue) {
        printTop();
        printLine("PLAYER HAND");
        printLine(formatHand(p) + " => " + playerValue);
        printMid();
        printLine("DEALER HAND");
        printLine("[" + d[0] + "] [?]");
        printBot();
    }

    private String formatHand(int[] h) {
        StringBuilder sb = new StringBuilder();
        for (int v : h) sb.append("[").append(v).append("] ");
        return sb.toString().trim();
    }

    // ===== UI CORE =====
    public void printTop() { 
        System.out.println(Unlucky9Constants.LEFT_MARGIN + "╔" + Unlucky9Constants.H_LINE + "╗"); 
    }

    public void printMid() { 
        System.out.println(Unlucky9Constants.LEFT_MARGIN + "╠" + Unlucky9Constants.H_LINE + "╣"); 
    }

    public void printBot() { 
        System.out.println(Unlucky9Constants.LEFT_MARGIN + "╚" + Unlucky9Constants.H_LINE + "╝"); 
    }

    public void printLine(String text) {
        if (text.length() > Unlucky9Constants.BOX_WIDTH) text = text.substring(0, Unlucky9Constants.BOX_WIDTH);
        System.out.printf(Unlucky9Constants.LEFT_MARGIN + "║ %-"+Unlucky9Constants.BOX_WIDTH+"s ║%n", text);
    }

    public void loadingAnimation(String msg, int cycles, int delay) {
        String[] frames = { ".", "..", "...", " ..", "  ." };
        for (int i = 0; i < cycles; i++) {
            System.out.print("\r" + Unlucky9Constants.LEFT_MARGIN + msg + frames[i % frames.length]);
            try { Thread.sleep(delay); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        System.out.println();
    }

    public void displayNewBalance(double balance) {
        System.out.println(Unlucky9Constants.LEFT_MARGIN + "New Balance: " + 
            utilities.Formatter.formatCurrency(balance));
    }
}
package utilities;

public class Formatter {

    public static String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }

    public static void showProgressBar(String message, int durationMs) {
        System.out.print(message + " [");
        int bars = 20;
        for (int i = 0; i < bars; i++) {
            System.out.print("=");
            ConsoleDisplay.pause(durationMs / bars);
        }
        System.out.println("] Complete!");
    }
}

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

    public static String numToRoman(int num) {
        // valid for 1..6 only
        switch (num) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            default:
                return "";
        }
    }
}

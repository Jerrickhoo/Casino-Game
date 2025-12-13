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

    public static String numToDice(int num) {
        // valid for 1..6 only
        switch (num) {
            case 1:
                System.out.println("┌─────────┐");
                System.out.println("│         │");
                System.out.println("│    ●    │");
                System.out.println("│         │");
                return "└─────────┘";
            case 2:
                System.out.println("┌─────────┐");
                System.out.println("│  ●      │");
                System.out.println("│         │");
                System.out.println("│      ●  │");
                return "└─────────┘";
            case 3:
                System.out.println("┌─────────┐");
                System.out.println("│  ●      │");
                System.out.println("│    ●    │");
                System.out.println("│      ●  │");
                return "└─────────┘";
            case 4:
                System.out.println("┌─────────┐");
                System.out.println("│  ●   ●  │");
                System.out.println("│         │");
                System.out.println("│  ●   ●  │");
                return "└─────────┘";
            case 5:
                System.out.println("┌─────────┐");
                System.out.println("│  ●   ●  │");
                System.out.println("│    ●    │");
                System.out.println("│  ●   ●  │");
                return "└─────────┘";
            case 6:
                System.out.println("┌─────────┐");
                System.out.println("│  ●   ●  │");
                System.out.println("│  ●   ●  │");
                System.out.println("│  ●   ●  │");
                return "└─────────┘";
            default:
                return"";
                
        }
    }
}

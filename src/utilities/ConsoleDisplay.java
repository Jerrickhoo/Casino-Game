package utilities;

public class ConsoleDisplay {

    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name").toLowerCase();

            if (operatingSystem.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static void pause(long milliseconds, String message) {
        if (milliseconds <= 0) {
            // Wait for Enter key
            System.out.print(message != null ? message : "Press Enter to continue...");
            InputValidator.waitForUserInput();
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
}

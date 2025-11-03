
import utilities.utilities;

public class CasinoMain {
    public static void main(String[] args) throws Exception {

        showMainMenu();

        utilities.closeScanner();
    }

    private static void showMainMenu() {
        while (true) {
            utilities.clearConsole();
            System.out.println("\n\n");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║     _____           _                 _____              ║");
            System.out.println("            ║    /  __ \\         (_)               |  __ \\             ║");
            System.out.println("            ║    | /  \\/ __ _ ___ _ _ __   ___    | |  \\/ __ _ _ __    ║");
            System.out.println("            ║    | |    / _` / __| | '_ \\ / _ \\   | | __ / _` | '_ \\   ║");
            System.out.println("            ║    | \\__/\\ (_| \\__ \\ | | | | (_) |  | |_\\ \\ (_| | | | |  ║");
            System.out.println("            ║     \\____/\\__,_|___/_|_| |_|\\___/    \\____/\\__,_|_| |_|  ║");
            System.out.println("            ║                                                          ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║     1. LOGIN         ║ ║    2. REGISTER       ║");
            System.out.println("            ║    ┌─────────┐       ║ ║     ┌───────┐        ║");
            System.out.println("            ║    │   A  K  │       ║ ║     │   /\\  │        ║");
            System.out.println("            ║    │   Q  J  │       ║ ║     │  /__\\ │        ║");
            System.out.println("            ║    └─────────┘       ║ ║     └───────┘        ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║   3. LEADERBOARD     ║ ║      4. EXIT         ║");
            System.out.println("            ║      ┌─────┐         ║ ║       ┌────┐         ║");
            System.out.println("            ║      │ [#1]│         ║ ║       │ XX │         ║");
            System.out.println("            ║      │ TOP │         ║ ║       │Exit│         ║");
            System.out.println("            ║      └─────┘         ║ ║       └────┘         ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.print("                 Enter your choice (1-4): ");

            int choice = utilities.readInt(1, 4);

            switch (choice) {
                case 1:
                    // login();
                    break;
                case 2:
                    // register();
                    break;
                case 3:
                    // displayLeaderboard();
                    break;
                case 4:
                    // saveAndExit();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}

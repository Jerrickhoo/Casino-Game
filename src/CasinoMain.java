import utilities.utilities;
import Core.Player;
import Core.PlayerDatabase;

public class CasinoMain {
    private static PlayerDatabase playerDB;
    private static Player currentPlayer;


    public static void main(String[] args) throws Exception {
        // Initialize database
        playerDB = new PlayerDatabase();

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

            if (currentPlayer != null) {
                System.out.println("            ╔══════════════════════════════════════════════════════════╗");
                System.out.println("            ║                  WELCOME BACK, "
                        + String.format("%-20s", currentPlayer.getUsername().toUpperCase()) + "║");
                System.out.println("            ║                  BALANCE: "
                        + String.format("%-25s", utilities.formatCurrency(currentPlayer.getBalance())) + "║");
                System.out.println("            ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");
            }

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
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    displayLeaderboard();
                    break;
                case 4:
                    saveAndExit();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void login() {
        utilities.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                        PLAYER LOGIN                      ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        System.out.print("                 Username: ");
        String username = utilities.readString();

        System.out.print("                 Password: ");
        String password = utilities.readString();

        Player player = playerDB.getPlayer(username);
        if (player != null && player.verifyPassword(password)) {
            currentPlayer = player;
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║                   ✅ LOGIN SUCCESSFUL!                    ║");
            System.out.println(
                    "            ║                   Welcome back, " + String.format("%-25s", player.getUsername())
                            + "║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
            showGameMenu();
        } else {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ❌ INVALID CREDENTIALS!                     ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
        }
    }

    private static void register() {
        utilities.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                      NEW ACCOUNT                         ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        System.out.print("                 Choose username: ");
        String username = utilities.readString();

        if (!Player.isValidUsername(username)) {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║    ❌ Username must be 3-20 chars (no : allowed)         ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
            return;
        }

        if (playerDB.playerExists(username)) {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ❌ USERNAME ALREADY EXISTS!                 ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
            return;
        }

        System.out.print("                 Choose password(4-30 chars): ");
        String password = utilities.readString();

        if (!Player.isValidPassword(password)) {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║     ❌ Password must be 4-30 chars (no : allowed)        ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
            return;
        }

        Player newPlayer = new Player(username, password, 1000.0);
        if (playerDB.addPlayer(newPlayer)) {
            currentPlayer = newPlayer;
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ✅ ACCOUNT CREATED!                         ║");
            System.out.println("            ║        Starting balance: "
                    + String.format("%-25s", utilities.formatCurrency(1000.0)) + "       ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
            showGameMenu();
        } else {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ❌ REGISTRATION FAILED!                    ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            utilities.pause(2000);
        }
    }

    private static void displayLeaderboard() {
        utilities.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                     TOP PLAYERS                          ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        // Simple leaderboard display
        java.util.List<Player> leaderboard = playerDB.getLeaderboard();

        if (leaderboard.isEmpty()) {
            System.out.println("                 No players yet. Be the first to register!");
        } else {
            System.out.println("                 ┌──────┬────────────────────┬───────────────┐");
            System.out.println("                 │ Rank │ Player             │ Balance       │");
            System.out.println("                 ├──────┼────────────────────┼───────────────┤");

            for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
                Player player = leaderboard.get(i);
                String rank = (i == 0) ? "🥇 " : (i == 1) ? "🥈 " : (i == 2) ? "🥉 " : "  ";
                System.out.printf("                 │ %-4s │ %-18s │ %-13s │\n",
                        rank + (i + 1),
                        player.getUsername(),
                        utilities.formatCurrency(player.getBalance()));
            }
            System.out.println("                 └──────┴────────────────────┴───────────────┘");
        }

        System.out.println("");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.print("                 Press Enter to continue... ");
        utilities.readString();
    }

    private static void saveAndExit() {
        System.out.println("");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║               🎰 Thanks for playing!                    ║");
        System.out.println("            ║               Data saved successfully!                  ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        utilities.pause(1000);
    }

    private static void showGameMenu() {
        while (currentPlayer != null) {
            utilities.clearConsole();
            System.out.println("\n\n");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║                     GAME SELECTION                       ║");
            System.out.println("            ╠══════════════════════════════════════════════════════════╣");
            System.out.println("            ║ Player: " + String.format("%-40s", currentPlayer.getUsername()) + "         ║");
            System.out.println("            ║ Balance: "
                    + String.format("%-39s", utilities.formatCurrency(currentPlayer.getBalance())) + "         ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║    1. LUCKY 9        ║ ║    2. BLACKJACK      ║");
            System.out.println("            ║    ┌─────────┐       ║ ║    ┌─────────┐       ║");
            System.out.println("            ║    │ 9   9   │       ║ ║    │ A   K   │       ║");
            System.out.println("            ║    │   WIN   │       ║ ║    │  BJACK  │       ║");
            System.out.println("            ║    └─────────┘       ║ ║    └─────────┘       ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║   3. SLOT MACHINE    ║ ║   4. CHUCK-A-LUCK    ║");
            System.out.println("            ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
            System.out.println("            ║     │ 7 7 7   │      ║ ║     │ ⚀ ⚁ ⚂   │      ║");
            System.out.println("            ║     │ JACKPOT │      ║ ║     │  DICE   │      ║");
            System.out.println("            ║     └─────────┘      ║ ║     └─────────┘      ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║   5. PLAYER STATS    ║ ║   6. LOGOUT          ║");
            System.out.println("            ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
            System.out.println("            ║     │  📊      │      ║ ║     │   🚪     │      ║");
            System.out.println("            ║     │ STATS   │      ║ ║     │ EXIT    │      ║");
            System.out.println("            ║     └─────────┘      ║ ║     └─────────┘      ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.print("                 Enter your choice (1-6): ");

            int choice = utilities.readInt(1, 6);

            switch (choice) {
                case 1:
                    // playLucky9();
                    break;
                case 2:
                    // playBlackjack();
                    break;
                case 3:
                    // playSlots();
                    break;
                case 4:
                    // playChuckALuck();
                    break;
                case 5:
                    showPlayerStats();
                    break;
                case 6:
                    currentPlayer = null;
                    System.out.println("");
                    System.out.println("            ╔══════════════════════════════════════════════════════════╗");
                    System.out.println("            ║                  ✅ Logged out successfully!            ║");
                    System.out.println("            ╚══════════════════════════════════════════════════════════╝");
                    utilities.pause(1500);
                    return;
            }
        }
    }

    private static void showPlayerStats() {
        utilities.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                    PLAYER STATISTICS                    ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        String[] stats = currentPlayer.getPlayerStats().split("\n");
        for (String stat : stats) {
            System.out.println("                 " + stat);
        }

        System.out.println("");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.print("                 Press Enter to continue... ");
        utilities.readString();
    }
}
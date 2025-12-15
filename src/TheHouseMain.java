import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import ui.AnimationDisplay;
import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import games.Game;
import games.AceRoll.AceRoll;
import games.MosesBonanza.MosesBonanza;
import games.TwentyWon.TwentyWon;
import games.Unlucky9.Unlucky9;

public class TheHouseMain {
        private static PlayerDatabase playerDatabase;
        private static Player currentPlayer;
        public static void main(String[] args) throws Exception {
                // Initialize database
                playerDatabase = new PlayerDatabase();
                AnimationDisplay.startUpGameAnimation(false); // put it false if animation annoying
                showMainMenu();
                InputValidator.closeScanner();
        }

        private static void showMainMenu() {
                while (true) {
                        ConsoleDisplay.clearConsole();
                        System.out.println("\n\n");
                        System.out.println(
                                        "                                      ╔════════════════════════════════════════════════════════════════════════════════╗");
                        System.out.println(
                                        "                                      ║                           ████████ ██    ██ ██████                             ║");
                        System.out.println(
                                        "                                      ║                              ██    ██    ██ ██                                 ║");
                        System.out.println(
                                        "                                      ║                              ██    ████████ ████                               ║");
                        System.out.println(
                                        "                                      ║                              ██    ██    ██ ██                                 ║");
                        System.out.println(
                                        "                                      ║                              ██    ██    ██ ██████                             ║");
                        System.out.println(
                                        "                                      ║                                                                                ║");
                        System.out.println(
                                        "                                      ║                ██    ██    ████    ██    ██    ██████  ██████                  ║");
                        System.out.println(
                                        "                                      ║                ██    ██  ██    ██  ██    ██  ██        ██                      ║");
                        System.out.println(
                                        "                                      ║                ████████  ██    ██  ██    ██    ████    ████                    ║");
                        System.out.println(
                                        "                                      ║                ██    ██  ██    ██  ██    ██        ██  ██                      ║");
                        System.out.println(
                                        "                                      ║                ██    ██    ████    ████████   ██████   ██████                  ║");
                        System.out.println(
                                        "                                      ║                                                                                ║");
                        System.out.println(
                                        "                                      ╚════════════════════════════════════════════════════════════════════════════════╝");
                        System.out.println("");

                        System.out.println(
                                        "                                                ╔════════════════════════════╗ ╔═══════════════════════════╗");
                        System.out.println(
                                        "                                                ║         1. LOGIN           ║ ║        2. REGISTER        ║");
                        System.out.println(
                                        "                                                ║    ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║ ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║");
                        System.out.println(
                                        "                                                ║    █                   █   ║ ║   █                   █   ║");
                        System.out.println(
                                        "                                                ║    █    █▀▀▀▀▀▀▀▀▀▀█   █   ║ ║   █    █▀▀▀▀▀▀▀▀▀▀█   █   ║");
                        System.out.println(
                                        "                                                ║    █    █  ACCESS  █   █   ║ ║   █    █  SIGN UP █   █   ║");
                        System.out.println(
                                        "                                                ║    █    █▄▄▄▄▄▄▄▄▄▄█   █   ║ ║   █    █▄▄▄▄▄▄▄▄▄▄█   █   ║");
                        System.out.println(
                                        "                                                ║    █                   █   ║ ║   █                   █   ║");
                        System.out.println(
                                        "                                                ║    █  ┌─────────────┐  █   ║ ║   █  ┌─────────────┐  █   ║");
                        System.out.println(
                                        "                                                ║    █  │ USER: █████ │  █   ║ ║   █  │  NEW PLAYER │  █   ║");
                        System.out.println(
                                        "                                                ║    █                   █   ║ ║   █  │   ACCOUNT   │  █   ║");
                        System.out.println(
                                        "                                                ║    █  │ PASS: █████ │  █   ║ ║   █  └─────────────┘  █   ║");
                        System.out.println(
                                        "                                                ║    █  └─────────────┘  █   ║ ║   █                   █   ║");
                        System.out.println(
                                        "                                                ║    █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║ ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║");
                        System.out.println(
                                        "                                                ╚════════════════════════════╝ ╚═══════════════════════════╝");
                        System.out.println(
                                        "                                                                                  ");
                        System.out.println(
                                        "                                                ╔═══════════════════════════╗ ╔═══════════════════════════╗");
                        System.out.println(
                                        "                                                ║      3. LEADERBOARD       ║ ║         4. EXIT           ║");
                        System.out.println(
                                        "                                                ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║ ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║");
                        System.out.println(
                                        "                                                ║   █                   █   ║ ║   █                   █   ║");
                        System.out.println(
                                        "                                                ║   █    ▄▄▄▄▄▄▄▄▄▄▄    █   ║ ║   █     █▀▀▀▀▀▀▀█     █   ║");
                        System.out.println(
                                        "                                                ║   █    █ LEADER  █    █   ║ ║   █     █ QUIT  █     █   ║");
                        System.out.println(
                                        "                                                ║   █    ▀▀▀▀▀▀▀▀▀▀▀    █   ║ ║   █     █▄▄▄▄▄▄▄█     █   ║");
                        System.out.println(
                                        "                                                ║   █   █ 1 █ 2 █ 3 █   █   ║ ║   █   ┌───────────┐   █   ║");
                        System.out.println(
                                        "                                                ║   █   ███ ███ ███ █   █   ║ ║   █   │   GOOD    │   █   ║");
                        System.out.println(
                                        "                                                ║   █   TOP PLAYERS     █   ║ ║   █   │   BYE!    │   █   ║");
                        System.out.println(
                                        "                                                ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║ ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║");
                        System.out.println(
                                        "                                                ╚═══════════════════════════╝ ╚═══════════════════════════╝");
                        System.out.println(
                                        "                                                                                  ");
                        System.out.println(
                                        "                                                ╔═════════════════════════════════════════════════════════╗");
                        System.out.print("                                                  Enter your choice (1-4): ");
                        int choice = InputValidator.readInt(1, 4);
                        switch (choice) {
                                case 1:
                                        login();
                                        break;
                                case 2:
                                        register();
                                        break;
                                case 3:
                                        playerDatabase.displayLeaderboard();
                                        break;
                                case 4:
                                        exitProgramMessage();
                                        return;
                                default:
                                        System.out.println("Invalid option!");
                        }
                }
        }

        private static void login() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║                       PLAYER LOGIN                       ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");
                System.out.print("                                                 Username: ");
                String username = InputValidator.readString();
                System.out.print("                                                 Password: ");
                String password = InputValidator.readString();

                Player player = playerDatabase.getPlayer(username);
                if (player != null && player.verifyPassword(password)) {
                        currentPlayer = player;
                        ConsoleDisplay.clearConsole();
                        AnimationDisplay.loginAnimation(true);
                        System.out.println("\n");
                        System.out.println(
                                        "                                                ╔══════════════════════════════════════════════════════════╗");
                        System.out.println(
                                        "                                                ║                     LOGIN SUCCESSFUL!                    ║");
                        System.out.println(
                                        "                                                ║                   Welcome back, "
                                                        + String.format("%-25s", player.getUsername()) + "║");
                        System.out.println(
                                        "                                                ╚══════════════════════════════════════════════════════════╝");
                        ConsoleDisplay.pause(4000);
                        showGameMenu();
                } else {
                        System.out.println("");
                        System.out.println("                                                ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                ║               ERROR: INVALID CREDENTIALS!                ║");
                        System.out.println("                                                ╚══════════════════════════════════════════════════════════╝");
                        InputValidator.waitForUserInput("                                                Press Enter to continue...");
                }
        }

        private static void register() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║                       NEW ACCOUNT                        ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");

                System.out.print("                                                 Choose username: ");
                String username = InputValidator.readString();

                if (!Player.isValidUsername(username)) {
                        System.out.println("");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                 ║    ERROR: Username must be 3-20 chars (no : allowed)     ║");
                        System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                        return;
                }

                if (playerDatabase.playerExists(username)) {
                        System.out.println("");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                 ║               ERROR: USERNAME ALREADY EXISTS!            ║");
                        System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                        return;
                }

                System.out.print("                                                 Choose password(4-30 chars): ");
                String password = InputValidator.readString();

                if (!Player.isValidPassword(password)) {
                        System.out.println("");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                 ║     ERROR: Password must be 4-30 chars (no : allowed)    ║");
                        System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                        return;
                }

                Player newPlayer = new Player(username, password, 100.0);
                if (playerDatabase.addPlayer(newPlayer)) {
                        System.out.println("");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                 ║                 SUCCESS: ACCOUNT CREATED!                ║");
                        System.out.println("                                                 ║  Starting balance: "
                                        + String.format("%-25s", Formatter.formatCurrency(100.0)) + "             ║");
                        System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                        ConsoleDisplay.pause(3000);
                        showMainMenu();
                } else {
                        System.out.println("");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                 ║               ERROR: REGISTRATION FAILED!                ║");
                        System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                }
        }

        private static void exitProgramMessage() {
                ConsoleDisplay.clearConsole();
                System.out.println("");
                System.out.println("                                                ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                ║               THE HOUSE: Thanks for playing!             ║");
                System.out.println("                                                ║               Data saved successfully!                   ║");
                System.out.println("                                                ╚══════════════════════════════════════════════════════════╝");
                ConsoleDisplay.pause(3000);
        }

        private static void showGameMenu() {
                while (currentPlayer != null) {
                        ConsoleDisplay.clearConsole();
                        System.out.println("\n\n");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.println("                                                 ║                      GAME SELECTION                      ║");
                        System.out.println("                                                 ╠══════════════════════════════════════════════════════════╣");
                        System.out.println("                                                 ║ Player: " + String.format("%-40s", currentPlayer.getUsername()) + "         ║");
                        System.out.println("                                                 ║ Balance: " + String.format("%-39s", Formatter.formatCurrency(currentPlayer.getBalance())) + "         ║");
                        System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                        System.out.println("");
                        System.out.println("                                                      ╔══════════════════════╗ ╔══════════════════════╗");
                        System.out.println("                                                      ║    1. UNLUCKY        ║ ║    2. TWENTY WON     ║");
                        System.out.println("                                                      ║    ┌─────────┐       ║ ║    ┌─────────┐       ║");
                        System.out.println("                                                      ║    │    9    │       ║ ║    │ K Q J A │       ║");
                        System.out.println("                                                      ║    │   WIN   │       ║ ║    │  BJACK  │       ║");
                        System.out.println("                                                      ║    └─────────┘       ║ ║    └─────────┘       ║");
                        System.out.println("                                                      ╚══════════════════════╝ ╚══════════════════════╝");
                        System.out.println("");
                        System.out.println("                                                      ╔══════════════════════╗ ╔══════════════════════╗");
                        System.out.println("                                                      ║   3. MOSES BONANZA   ║ ║   4. Ace Roll        ║");
                        System.out.println("                                                      ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
                        System.out.println("                                                      ║     │  7 7 7  │      ║ ║     │I II III │      ║");
                        System.out.println("                                                      ║     │ JACKPOT │      ║ ║     │  DICE   │      ║");
                        System.out.println("                                                      ║     └─────────┘      ║ ║     └─────────┘      ║");
                        System.out.println("                                                      ╚══════════════════════╝ ╚══════════════════════╝");
                        System.out.println("");
                        System.out.println("                                                      ╔══════════════════════╗ ╔══════════════════════╗");
                        System.out.println("                                                      ║   5.   PLAYER        ║ ║   6. Transaction     ║");
                        System.out.println("                                                      ║        STATS         ║ ║        History       ║");
                        System.out.println("                                                      ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
                        System.out.println("                                                      ║     │  STATS  │      ║ ║     │ HISTORY │      ║");
                        System.out.println("                                                      ║     └─────────┘      ║ ║     └─────────┘      ║");
                        System.out.println("                                                      ╚══════════════════════╝ ╚══════════════════════╝");
                        System.out.println("");
                        System.out.println("                                                      ╔══════════════════════╗ ╔══════════════════════╗");
                        System.out.println("                                                      ║   7.  Cash In        ║ ║   8.  Cash Out       ║");
                        System.out.println("                                                      ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
                        System.out.println("                                                      ║     │  CASH   │      ║ ║     │  CASH   │      ║");
                        System.out.println("                                                      ║     │   IN    │      ║ ║     │   OUT   │      ║");
                        System.out.println("                                                      ║     └─────────┘      ║ ║     └─────────┘      ║");
                        System.out.println("                                                      ╚══════════════════════╝ ╚══════════════════════╝");
                        System.out.println("");
                        System.out.println("                                                                  ╔══════════════════════╗              ");
                        System.out.println("                                                                  ║   9. LOGOUT          ║              ");
                        System.out.println("                                                                  ║     ┌─────────┐      ║              ");
                        System.out.println("                                                                  ║     │   EXIT  │      ║              ");
                        System.out.println("                                                                  ║     └─────────┘      ║              ");
                        System.out.println("                                                                  ╚══════════════════════╝              ");
                        System.out.println("");
                        System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                        System.out.print("                                                           Enter your choice (1-9): ");

                        int choice = InputValidator.readInt(1, 9);

                        switch (choice) {
                                case 1:
                                        playGame(new Unlucky9());
                                        break;
                                case 2:
                                        playGame(new TwentyWon());
                                        break;
                                case 3:
                                        playGame(new MosesBonanza());
                                        break;
                                case 4:
                                        playGame(new AceRoll());
                                        break;
                                case 5:
                                        showPlayerStats();
                                        break;
                                case 6:
                                        showTransactionHistory();
                                        break;
                                case 7:
                                        handleCashIn();
                                        break;
                                case 8:
                                        handleCashOut();
                                        break;
                                case 9:
                                        handleLogout();
                                        return;
                        }
                }
        }

        /**
         * Polymorphic method to start any game using the Game interface
         * Provides consistent game launching across all game types
         */
        private static void playGame(Game game) {
                game.startGame(currentPlayer, playerDatabase);
        }

        private static void showPlayerStats() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║                    PLAYER STATISTICS                     ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");

                String[] stats = currentPlayer.getPlayerStats().split("\n");
                for (String stat : stats) {
                        System.out.println("                                                 " + stat);
                }

                System.out.println("");
                // Offer delete option here with strong warnings
                System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║    Options:                                              ║");
                System.out.println("                                                 ║      1. Delete Account (PERMANENT)                       ║");
                System.out.println("                                                 ║      2. Back                                             ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.print("                                                 Choose option (1-2): ");
                int opt = InputValidator.readInt(1, 2);

                if (opt == 1) {
                        deleteAccount();
                }
        }

        private static void deleteAccount() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║                  WARNING: DELETE ACCOUNT                 ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");

                // Display current player's information
                System.out.println("                                                 Account to be deleted:");
                System.out.println("                                                 Username: " + currentPlayer.getUsername());
                System.out.println("                                                 Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
                System.out.println("");

                // Show warning and consequences
                System.out.println("                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║                         WARNING                          ║");
                System.out.println("                                                 ╠══════════════════════════════════════════════════════════╣");
                System.out.println("                                                 ║  - All game statistics will be permanently lost          ║");
                System.out.println("                                                 ║  - All balance and transaction history will be deleted   ║");
                System.out.println("                                                 ║  - This action cannot be undone                          ║");
                System.out.println("                                                 ║  - You will need to create a new account to play again   ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");

                // First confirmation
                System.out.print("                                                 Are you sure you want to delete your account? (yes/no): ");
                String confirmation1 = InputValidator.readString().toLowerCase();

                if (!confirmation1.equals("yes")) {
                        System.out.println("\n                                                 Account deletion cancelled.");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                        return;
                }

                // Second confirmation - require password
                System.out.print("\n                                                 Please enter your password to confirm deletion: ");
                String passwordConfirm = InputValidator.readString();
                ConsoleDisplay.clearConsole();

                if (!currentPlayer.verifyPassword(passwordConfirm)) {
                        System.out.println("\n                                                 ERROR: Password incorrect. Account deletion cancelled.");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                        return;
                }

                // Final confirmation with strong warning
                System.out.println("\n                                                 ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                                 ║                      FINAL WARNING                       ║");
                System.out.println("                                                 ╠══════════════════════════════════════════════════════════╣");
                System.out.println("                                                 ║      Type 'DELETE MY ACCOUNT' to permanently delete      ║");
                System.out.println("                                                 ║    all your data including your balance of " +
                                String.format("%-13s", Formatter.formatCurrency(currentPlayer.getBalance()))
                                + " ║");
                System.out.println("                                                 ╚══════════════════════════════════════════════════════════╝");
                System.out.print("\n                                                 Enter confirmation phrase: ");
                String finalConfirmation = InputValidator.readString();

                if (!finalConfirmation.equals("DELETE MY ACCOUNT")) {
                        System.out.println("\n                                                 Account deletion cancelled.");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                        return;
                }

                // Perform account deletion via DB API (verifies again inside)
                boolean deleted = playerDatabase.deleteAccount(currentPlayer, passwordConfirm);

                if (deleted) {
                        ConsoleDisplay.clearConsole();
                        System.out.println("\n                                                 SUCCESS: Account '" + currentPlayer.getUsername()
                                        + "' has been permanently deleted.\n");
                        currentPlayer = null; // Clear current player

                        // Show animation or message
                        AnimationDisplay.deletionAnimation(true);

                        InputValidator.waitForUserInput("\n                                                 Press Enter to return to main menu...");
                } else {
                        System.out.println(
                                        "\n                                                 ERROR: Failed to delete account. Please try again later.");
                        InputValidator.waitForUserInput("                                                 Press Enter to continue...");
                }
        }

        /**
         * Display transaction history for the current player
         */
        private static void showTransactionHistory() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println(" ╔══════════════════════════════════════════════════════════╗");
                System.out.println(" ║                     TRANSACTION HISTORY                  ║");
                System.out.println(" ╚══════════════════════════════════════════════════════════╝");
                System.out.println("");
                Transaction.displayForPlayer(currentPlayer, 100);
                System.out.println();
                InputValidator.waitForUserInput("                                         Press Enter to continue...");
        }

        /**
         * Handle cash in operation for the current player
         */
        private static void handleCashIn() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println("                                         ╔═════════════════════════════════════════════════════════════════════════════╗");
                System.out.println("                                         ║                                 CASH IN                                     ║");
                System.out.println("                                         ╚═════════════════════════════════════════════════════════════════════════════╝");
                AnimationDisplay.qrCodeCashIn();
                InputValidator.waitForUserInput(
                                "\n\n                                         Press Enter to continue...");
                ConsoleDisplay.clearConsole();
                System.out.println("");
                System.out.print(
                                "                                         Enter amount to add (type 'exit' to cancel): ");
                double amount = InputValidator.readDoubleOrExit(1);
                if (amount == Double.MIN_VALUE) {
                        System.out.println("\n\n                                         SUCCESS: Cash in cancelled.");
                } else if (playerDatabase.cashIn(currentPlayer, amount)) {
                        System.out.println("\n");
                        System.out.println("                                         SUCCESS: Cash in successful!");
                        System.out.println("                                         New Balance: "
                                        + Formatter.formatCurrency(currentPlayer.getBalance()));
                } else {
                        System.out.println("");
                        System.out.println("                                         ERROR: Invalid amount.");
                }
                InputValidator.waitForUserInput(
                                "\n\n                                         Press Enter to continue...");
        }

        private static void handleCashOut() {
                ConsoleDisplay.clearConsole();
                System.out.println("\n\n");
                System.out.println("                                         ╔═════════════════════════════════════════════════════════════════════════════╗");
                System.out.println("                                         ║                                 CASH OUT                                    ║");
                System.out.println("                                         ╚═════════════════════════════════════════════════════════════════════════════╝");
                System.out.println("");
                System.out.println("                                         Current Balance: "
                                + Formatter.formatCurrency(currentPlayer.getBalance()));
                System.out.println("");
                System.out.print("                                         Enter amount to cash out (0 for full, type 'exit' to cancel): ");
                double cashAmount = InputValidator.readDoubleOrExit(0);
                if (cashAmount == Double.MIN_VALUE) {
                        System.out.println("\n                                         SUCCESS: Cash out cancelled.");
                } else if (cashAmount == 0) {
                        double paid = playerDatabase.cashOutAll(currentPlayer);
                        if (paid > 0) {
                                System.out.println("\n                                         SUCCESS: Cash out successful. Amount paid: " + Formatter.formatCurrency(paid));
                                System.out.println("\n                                         New Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
                        } else {
                                System.out.println("\n                                         ERROR: Nothing to cash out.");
                        }
                } else {
                        if (playerDatabase.cashOut(currentPlayer, cashAmount)) {
                                System.out.println("\n                                         SUCCESS: Cash out successful. Amount paid: " + Formatter.formatCurrency(cashAmount));
                                System.out.println("\n                                         New Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
                        } else {
                                System.out.println("\n                                         ERROR: Invalid amount (must be >0 and <= balance).");
                        }
                }
                InputValidator.waitForUserInput(
                                "\n\n                                         Press Enter to continue...");
        }

        private static void handleLogout() {
                currentPlayer = null;
                ConsoleDisplay.clearConsole();
                System.out.println("");
                System.out.println("                                               ╔══════════════════════════════════════════════════════════╗");
                System.out.println("                                               ║             SUCCESS: Logged out successfully!            ║");
                System.out.println("                                               ╚══════════════════════════════════════════════════════════╝");
                ConsoleDisplay.pause(1500);
        }
}
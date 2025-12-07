import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import ui.AnimationDisplay;

import Core.Player;
import Core.PlayerDatabase;
import games.SlotMachine.SlotMachine;
import games.DicePoker.DicePoker;
import games.Lucky9.Lucky9;
import games.Blackjack.BlackJack;

public class CasinoMain {
    private static PlayerDatabase playerDB;
    private static Player currentPlayer;

    public static void main(String[] args) throws Exception {
        // Initialize database
        playerDB = new PlayerDatabase();

        AnimationDisplay.startUpGameAnimation(false); // put it false if animation annoying

        showMainMenu();

        InputValidator.closeScanner();
    }

    private static void showMainMenu() {
        while (true) {
            ConsoleDisplay.clearConsole();
            System.out.println("\n\n");
            System.out.println(
                    "            ╔════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println(
                    "            ║                  ██████  █████  ███████ ██ ███    ██   ████                    ║");
            System.out.println(
                    "            ║                 ██      ██   ██ ██      ██ ████   ██ ██    ██                  ║");
            System.out.println(
                    "            ║                 ██      ███████ ███████ ██ ██ ██  ██ ██    ██                  ║");
            System.out.println(
                    "            ║                 ██      ██   ██      ██ ██ ██  ██ ██ ██    ██                  ║");
            System.out.println(
                    "            ║                  ██████ ██   ██ ███████ ██ ██   ████   ████                    ║");
            System.out.println(
                    "            ║                                                                                ║");
            System.out.println(
                    "            ║                       ██████     █████     ██  ██   ██████                     ║");
            System.out.println(
                    "            ║                      ██         ██   ██  ██  ██  ██ ██                         ║");
            System.out.println(
                    "            ║                      ██   ████  ███████  ██  ██  ██ ████                       ║");
            System.out.println(
                    "            ║                      ██     ██  ██   ██  ██      ██ ██                         ║");
            System.out.println(
                    "            ║                       ██████    ██   ██  ██      ██ ██████                     ║");
            System.out.println(
                    "            ║                                                                                ║");
            System.out.println(
                    "            ╚════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println("");

            System.out.println("                       ╔════════════════════════════╗ ╔═══════════════════════════╗");
            System.out.println("                       ║         1. LOGIN           ║ ║        2. REGISTER        ║");
            System.out.println("                       ║    ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║ ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║");
            System.out.println("                       ║    █                   █   ║ ║   █                   █   ║");
            System.out.println("                       ║    █    █▀▀▀▀▀▀▀▀▀▀█   █   ║ ║   █    █▀▀▀▀▀▀▀▀▀▀█   █   ║");
            System.out.println("                       ║    █    █  ACCESS  █   █   ║ ║   █    █  SIGN UP █   █   ║");
            System.out.println("                       ║    █    █▄▄▄▄▄▄▄▄▄▄█   █   ║ ║   █    █▄▄▄▄▄▄▄▄▄▄█   █   ║");
            System.out.println("                       ║    █                   █   ║ ║   █                   █   ║");
            System.out.println("                       ║    █  ┌─────────────┐  █   ║ ║   █  ┌─────────────┐  █   ║");
            System.out.println("                       ║    █  │ USER: █████ │  █   ║ ║   █  │  NEW PLAYER │  █   ║");
            System.out.println("                       ║    █                   █   ║ ║   █  │   ACCOUNT   │  █   ║");
            System.out.println("                       ║    █  │ PASS: █████ │  █   ║ ║   █  └─────────────┘  █   ║");
            System.out.println("                       ║    █  └─────────────┘  █   ║ ║   █                   █   ║");
            System.out.println("                       ║    █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║ ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║");
            System.out.println("                       ╚════════════════════════════╝ ╚═══════════════════════════╝");
            System.out.println("                                                                                  ");
            System.out.println("                       ╔═══════════════════════════╗ ╔═══════════════════════════╗");
            System.out.println("                       ║      3. LEADERBOARD       ║ ║         4. EXIT           ║");
            System.out.println("                       ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║ ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║");
            System.out.println("                       ║   █                   █   ║ ║   █                   █   ║");
            System.out.println("                       ║   █    ▄▄▄▄▄▄▄▄▄▄▄    █   ║ ║   █     █▀▀▀▀▀▀▀█     █   ║");
            System.out.println("                       ║   █    █ LEADER  █    █   ║ ║   █     █ QUIT  █     █   ║");
            System.out.println("                       ║   █    ▀▀▀▀▀▀▀▀▀▀▀    █   ║ ║   █     █▄▄▄▄▄▄▄█     █   ║");
            System.out.println("                       ║   █   █ 1 █ 2 █ 3 █   █   ║ ║   █   ┌───────────┐   █   ║");
            System.out.println("                       ║   █   ███ ███ ███ █   █   ║ ║   █   │   GOOD    │   █   ║");
            System.out.println("                       ║   █   TOP PLAYERS     █   ║ ║   █   │   BYE!    │   █   ║");
            System.out.println("                       ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║ ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║");
            System.out.println("                       ╚═══════════════════════════╝ ╚═══════════════════════════╝");
            System.out.println("                                                                                  ");
            System.out.println("                       ╔═════════════════════════════════════════════════════════╗");
            System.out.print("                            Enter your choice (1-4): ");

            int choice = InputValidator.readInt(1, 4);

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    playerDB.displayLeaderboard();
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
        ConsoleDisplay.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                        PLAYER LOGIN                      ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        System.out.print("                 Username: ");
        String username = InputValidator.readString();

        System.out.print("                 Password: ");
        String password = InputValidator.readString();

        Player player = playerDB.getPlayer(username);
        if (player != null && player.verifyPassword(password)) {
            currentPlayer = player;
            ConsoleDisplay.clearConsole();
            AnimationDisplay.loginAnimation(true);
            System.out.println("\n");
            System.out.println(
                    "                                          ╔══════════════════════════════════════════════════════════╗");
            System.out.println(
                    "                                          ║                   ✅ LOGIN SUCCESSFUL!                    ║");
            System.out.println("                                          ║                   Welcome back, "
                    + String.format("%-25s", player.getUsername()) + "║");
            System.out.println(
                    "                                          ╚══════════════════════════════════════════════════════════╝");
            ConsoleDisplay.pause(3000);
            showGameMenu();
        } else {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ❌ INVALID CREDENTIALS!                     ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput("             Press Enter to continue...");
        }
    }

    private static void register() {
        ConsoleDisplay.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                      NEW ACCOUNT                         ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        System.out.print("                 Choose username: ");
        String username = InputValidator.readString();

        if (!Player.isValidUsername(username)) {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║    ❌ Username must be 3-20 chars (no : allowed)         ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput("             Press Enter to continue...");
            return;
        }

        if (playerDB.playerExists(username)) {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ❌ USERNAME ALREADY EXISTS!                 ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput("             Press Enter to continue...");
            return;
        }

        System.out.print("                 Choose password(4-30 chars): ");
        String password = InputValidator.readString();

        if (!Player.isValidPassword(password)) {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║     ❌ Password must be 4-30 chars (no : allowed)        ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput("             Press Enter to continue...");
            return;
        }

        Player newPlayer = new Player(username, password, 1000.0);
        if (playerDB.addPlayer(newPlayer)) {
            currentPlayer = newPlayer;
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ✅ ACCOUNT CREATED!                         ║");
            System.out.println("            ║        Starting balance: "
                    + String.format("%-25s", Formatter.formatCurrency(1000.0)) + "       ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            ConsoleDisplay.pause(3000);
            showMainMenu();
        } else {
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║               ❌ REGISTRATION FAILED!                    ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput("             Press Enter to continue...");
        }
    }

    // ...existing code...

    private static void saveAndExit() {
        ConsoleDisplay.clearConsole();
        System.out.println("");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║               🎰 Thanks for playing!                      ║");
        System.out.println("            ║               Data saved successfully!                   ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        ConsoleDisplay.pause(3000);
    }

    private static void showGameMenu() {
        while (currentPlayer != null) {
            ConsoleDisplay.clearConsole();
            System.out.println("\n\n");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.println("            ║                     GAME SELECTION                       ║");
            System.out.println("            ╠══════════════════════════════════════════════════════════╣");
            System.out.println(
                    "            ║ Player: " + String.format("%-40s", currentPlayer.getUsername()) + "         ║");
            System.out.println("            ║ Balance: "
                    + String.format("%-39s", Formatter.formatCurrency(currentPlayer.getBalance())) + "         ║");
            System.out.println("            ╚══════════════════════════════════════════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║    1. LUCKY 9        ║ ║    2. BLACKJACK      ║");
            System.out.println("            ║    ┌─────────┐       ║ ║    ┌─────────┐       ║");
            System.out.println("            ║    │    9    │       ║ ║    │ K Q J A │       ║");
            System.out.println("            ║    │   WIN   │       ║ ║    │  BJACK  │       ║");
            System.out.println("            ║    └─────────┘       ║ ║    └─────────┘       ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║   3. SLOT MACHINE    ║ ║   4. CHUCK-A-LUCK    ║");
            System.out.println("            ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
            System.out.println("            ║     │  7 7 7  │      ║ ║     │ ⚀ ⚁ ⚂   │      ║");
            System.out.println("            ║     │ JACKPOT │      ║ ║     │  DICE   │      ║");
            System.out.println("            ║     └─────────┘      ║ ║     └─────────┘      ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║   5.    PLAYER       ║ ║   6. Transaction     ║");
            System.out.println("            ║         STATS        ║ ║        History       ║");
            System.out.println("            ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
            System.out.println("            ║     │   📊     │      ║ ║     │   📃     │      ║");
            System.out.println("            ║     │  STATS  │      ║ ║     │ History │      ║");
            System.out.println("            ║     └─────────┘      ║ ║     └─────────┘      ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║   7.  Cash In        ║ ║   8.  Cash Out       ║");
            System.out.println("            ║     ┌─────────┐      ║ ║     ┌─────────┐      ║");
            System.out.println("            ║     │   💸     │      ║ ║     │   💸     │      ║");
            System.out.println("            ║     │  STATS  │      ║ ║     │ Money :>│      ║");
            System.out.println("            ║     └─────────┘      ║ ║     └─────────┘      ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.println("");
            System.out.println("                           ╔══════════════════════╗");
            System.out.println("                           ║   9. LOGOUT          ║");
            System.out.println("                           ║     ┌─────────┐      ║");
            System.out.println("                           ║     │    🚪    │      ║");
            System.out.println("                           ║     │   EXIT  │      ║");
            System.out.println("                           ║     └─────────┘      ║");
            System.out.println("                           ╚══════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════════════════════════════════════════╗");
            System.out.print("                 Enter your choice (1-9): ");

            int choice = InputValidator.readInt(1, 9);

            switch (choice) {
                case 1:
                    // Use Game polymorphism: start the Lucky9 game with current player + DB
                    new Lucky9().startGame(currentPlayer, playerDB);
                    break;
                case 2:
                    // Launch Blackjack via Game polymorphism (BlackJack will update player + DB)
                    new BlackJack().startGame(currentPlayer, playerDB);
                    break;
                case 3:
                    SlotMachine slotGame = new SlotMachine(currentPlayer, playerDB);
                    slotGame.startGame(currentPlayer, playerDB);
                    break;
                case 4:
                    DicePoker diceGame = new DicePoker();
                    diceGame.startGame(currentPlayer, playerDB);
                    break;
                case 5:
                    showPlayerStats();
                    break;
                case 6:
                    // TRANSACTION HISTORY (show only current player's transactions)
                    ConsoleDisplay.clearConsole();
                    System.out.println("\n\n");
                    System.out.println("            ╔══════════════════════════════════════════════════════════╗");
                    System.out.println("            ║                     TRANSACTION HISTORY                  ║");
                    System.out.println("            ╚══════════════════════════════════════════════════════════╝");
                    System.out.println("");
                    playerDB.displayTransactionsForPlayer(currentPlayer, 100);
                    System.out.println();
                    InputValidator.waitForUserInput("                 Press Enter to continue...");
                    break;

                case 7:
                    // CASH IN
                    ConsoleDisplay.clearConsole();
                    System.out.println("\n\n");
                    System.out.println(
                            "            ╔═════════════════════════════════════════════════════════════════════════════╗");
                    System.out.println(
                            "            ║                                    CASH IN                                  ║");
                    System.out.println(
                            "            ╚═════════════════════════════════════════════════════════════════════════════╝");
                    AnimationDisplay.qrCodeCashIn();
                    InputValidator.waitForUserInput("\n\n                 Press Enter to continue...");
                    ConsoleDisplay.clearConsole();
                    System.out.println("");
                    System.out.print("                 Enter amount to add (type 'exit' to cancel): ");
                    double amount = InputValidator.readDoubleOrExit(1);
                    if (amount == Double.MIN_VALUE) {
                        System.out.println("\n\n                 ✅ Cash in cancelled.");
                    } else if (playerDB.cashIn(currentPlayer, amount)) {
                        System.out.println("\n");
                        System.out.println("                 ✅ Cash in successful. New Balance: "
                                + Formatter.formatCurrency(currentPlayer.getBalance()));
                    } else {
                        System.out.println("");
                        System.out.println("                 ❌ Invalid amount.");
                    }
                    InputValidator.waitForUserInput("\n\n                 Press Enter to continue...");
                    break;

                case 8:
                    // CASH OUT
                    ConsoleDisplay.clearConsole();
                    System.out.println("\n\n");
                    System.out.println(
                            "            ╔═════════════════════════════════════════════════════════════════════════════╗");
                    System.out.println(
                            "            ║                                   CASH OUT                                  ║");
                    System.out.println(
                            "            ╚═════════════════════════════════════════════════════════════════════════════╝");
                    System.out.println("");
                    System.out.println("                 Current Balance: "
                            + Formatter.formatCurrency(currentPlayer.getBalance()));
                    System.out.println("");
                    System.out
                            .println("                 Enter amount to cash out (0 for full, type 'exit' to cancel): ");
                    double cashAmount = InputValidator.readDoubleOrExit(0);
                    if (cashAmount == Double.MIN_VALUE) {
                        System.out.println("\n                 ✅ Cash out cancelled.");
                    } else if (cashAmount == 0) {
                        double paid = playerDB.cashOutAll(currentPlayer);
                        if (paid > 0) {
                            System.out.println("\n                 ✅ Cash out successful. Amount paid: "
                                    + Formatter.formatCurrency(paid));
                            System.out.println("\n                 New Balance: "
                                    + Formatter.formatCurrency(currentPlayer.getBalance()));
                        } else {
                            System.out.println("\n                 ❌ Nothing to cash out.");
                        }
                    } else {
                        if (playerDB.cashOut(currentPlayer, cashAmount)) {
                            System.out.println("\n                 ✅ Cash out successful. Amount paid: "
                                    + Formatter.formatCurrency(cashAmount));
                            System.out.println("\n                 New Balance: "
                                    + Formatter.formatCurrency(currentPlayer.getBalance()));
                        } else {
                            System.out.println("\n                 ❌ Invalid amount (must be >0 and <= balance).");
                        }
                    }
                    InputValidator.waitForUserInput("\n\n                 Press Enter to continue...");
                    break;
                case 9:
                    currentPlayer = null;
                    ConsoleDisplay.clearConsole();
                    System.out.println("");
                    System.out.println("            ╔══════════════════════════════════════════════════════════╗");
                    System.out.println("            ║                  ✅ Logged out successfully!              ║");
                    System.out.println("            ╚══════════════════════════════════════════════════════════╝");
                    ConsoleDisplay.pause(1500);
                    return;
            }
        }
    }

    private static void showPlayerStats() {
        ConsoleDisplay.clearConsole();
        System.out.println("\n\n");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.println("            ║                    PLAYER STATISTICS                     ║");
        System.out.println("            ╚══════════════════════════════════════════════════════════╝");
        System.out.println("");

        String[] stats = currentPlayer.getPlayerStats().split("\n");
        for (String stat : stats) {
            System.out.println("                 " + stat);
        }

        System.out.println("");
        System.out.println("            ╔══════════════════════════════════════════════════════════╗");
        System.out.print("                 Press Enter to continue... ");
        InputValidator.waitForUserInput("");
    }
}
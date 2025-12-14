package games.SlotMachine;

import java.util.Random;
import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import utilities.InputValidator;
import games.Game;

public class SlotMachine extends Game {
    private Reel[] reels;
    private final String[] symbols = { "Moses", "John", "Hermosura" };
    private final int[] multipliers = { 2, 5, 2 };
    private final Random random = new Random();
    private String[][] lastGrid;
    private PlayerDatabase playerDatabase;

    public SlotMachine() {
    }

    public SlotMachine(Player player, PlayerDatabase playerDB) {
        super(player);
        this.playerDatabase = playerDB;
        setupReels();
    }

    private void setupReels() {
        this.reels = new Reel[3];
        for (int i = 0; i < 3; i++) {
            reels[i] = new Reel(symbols);
        }
    }

    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        this.player = player;
        this.balance = player.getBalance();
        this.playerDatabase = playerDB;
        this.gameName = "Moses Bonanza Slot Machine";

        if (reels == null)
            setupReels();

        while (true) {
            ConsoleDisplay.clearConsole();
            displayRules();

            System.out.println(
                    "                                                      ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println(
                    "                                                      ║      1. PLAY         ║ ║    2. EXIT GAME      ║");
            System.out.println(
                    "                                                      ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.print("\n                                                      Choose [1-2]: ");
            int choice = InputValidator.readInt(1, 2);
            if (choice == 2) {
                exitMessage();
                return;
            }

            if (balance <= 0) {
                System.out.println(
                        "                                                      You don't have any balance to place a bet! Go to the cash-in page...");
                ConsoleDisplay.pause(1000,
                        "                                                Press Enter to continue...");
                ConsoleDisplay.clearConsole();
                continue;
            }

            System.out.print("                                                      Place bet: $");
            double bet = InputValidator.readDoubleOrExit(1);

            if (bet == Double.MIN_VALUE) {
                exitMessage();
                return;
            }

            // Check if player can afford the bet using Player's canAfford method
            if (!player.canAfford(bet)) {
                System.out.println(
                        "                                                      You don't have enough money for that bet!");
                ConsoleDisplay.pause(5000);
                ConsoleDisplay.clearConsole();
                continue;
            }

            reelSpinAnimation();

            ConsoleDisplay.clearConsole();
            playRound();

            double winAmount = calculatePayout(bet);

            if (winAmount > 0) {
                System.out.println(
                        "\n                                                      ==========================================");
                System.out.println("                                                      Total Winnings: "
                        + Formatter.formatCurrency(winAmount));
                System.out.println(
                        "                                                      ==========================================");
                System.out.println("\n                                                      CONGRATS! PALDO! ");
                System.out.println();
            } else {
                System.out.println();
                System.out.println("                                                      No matches this round.");
                updateBalance(-bet);
                System.out.println();
            }

            updateBalance(winAmount);

            InputValidator.waitForUserInput(
                    "                                                      Press Enter to continue...");
            ConsoleDisplay.clearConsole();
        }
    }

    @Override
    public void playRound() {
        lastGrid = new String[3][3];

        for (int column = 0; column < 3; column++) {
            reels[column].spin();
            String center = reels[column].getSymbol();
            lastGrid[1][column] = center;

            lastGrid[0][column] = symbols[random.nextInt(symbols.length)];
            lastGrid[2][column] = symbols[random.nextInt(symbols.length)];
        }

        // Compute a uniform cell width for nicer alignment
        int cellWidth = 0;
        for (String s : symbols) {
            if (s != null && s.length() > cellWidth)
                cellWidth = s.length();
        }
        // add a bit of padding
        cellWidth += 2;

        // total inner width for border drawing
        // Each row: " ║ " + cell + " | " + cell + " | " + cell + " ║"
        // Inner characters between the vertical borders = 1 + cellWidth + 3 + cellWidth
        // + 3 + cellWidth + 1 = 8 + 3*cellWidth
        int totalInner = 3 * cellWidth + 8;

        StringBuilder border = new StringBuilder();
        for (int i = 0; i < totalInner; i++)
            border.append('═');

        String rowFmt = "                                                      ║ %" + "-" + cellWidth + "s | %" + "-"
                + cellWidth + "s | %" + "-" + cellWidth
                + "s ║\n";

        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        String[][] displayGrid = new String[3][3];
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                displayGrid[r][c] = "";

        for (int revealCol = 0; revealCol < 3; revealCol++) {
            for (int r = 0; r < 3; r++) {
                displayGrid[r][revealCol] = lastGrid[r][revealCol];
            }

            ConsoleDisplay.clearConsole();
            System.out.println("\n                                                      RESULTS");
            System.out.println("                                                      ╔" + border.toString() + "╗");

            for (int r = 0; r < 3; r++) {
                String c0 = displayGrid[r][0].isEmpty() ? "" : displayGrid[r][0];
                String c1 = displayGrid[r][1].isEmpty() ? "" : displayGrid[r][1];
                String c2 = displayGrid[r][2].isEmpty() ? "" : displayGrid[r][2];

                boolean fullyRevealed = (revealCol == 2);
                boolean isWinRow = lastGrid[r][0].equals(lastGrid[r][1]) && lastGrid[r][1].equals(lastGrid[r][2]);

                if (fullyRevealed && isWinRow) {
                    String pc0 = String.format("%-" + cellWidth + "s", c0);
                    String pc1 = String.format("%-" + cellWidth + "s", c1);
                    String pc2 = String.format("%-" + cellWidth + "s", c2);
                    System.out.printf(
                            "                                                      ║ %s%s%s | %s%s%s | %s%s%s ║\n",
                            ANSI_GREEN, pc0, ANSI_RESET,
                            ANSI_GREEN,
                            pc1, ANSI_RESET, ANSI_GREEN, pc2, ANSI_RESET);
                } else {
                    System.out.printf(rowFmt, c0, c1, c2);
                }

                if (r < 2)
                    System.out.println(
                            "                                                      ║" + border.toString() + "║");
            }
            System.out.println("                                                      ╚" + border.toString() + "╝");

            ConsoleDisplay.pause(300);
        }
    }

    public double calculatePayout(double bet) {
        double win = 0;
        boolean printedHeader = false;

        for (int r = 0; r < 3; r++) {
            if (lastGrid[r][0].equals(lastGrid[r][1]) && lastGrid[r][1].equals(lastGrid[r][2])) {
                if (!printedHeader) {
                    System.out.println();
                    System.out.println("                                                      Results:");
                    printedHeader = true;
                }

                int multi = multipliers[r];
                double lineWin = bet * multi;
                win += lineWin;

                System.out.println("                                                      Payline " + (r + 1)
                        + " matched! (x" + multi + ") = "
                        + Formatter.formatCurrency(lineWin));
            }
        }

        return win;
    }

    @Override
    public double calculatePayout() {
        return 0;
    }

    @Override
    public void updateBalance(double amount) {
        balance += amount;
        if (balance < 0)
            balance = 0;

        player.setBalance(balance);
        playerDatabase.updatePlayer(player);
        Transaction.log(player.getUsername(), player.getPlayerId(), getGameName(), "SPIN_RESULT", amount, balance);
    }

    @Override
    public void displayRules() {
        System.out.println(
                "\n                                                      ╔═══════════════════════════════════════════════╗");
        System.out.println(
                "                                                      ║                 MOSES BONANZA                 ║");
        System.out.println(
                "                                                      ╠═══════════════════════════════════════════════╣");
        System.out.println(
                "                                                      ║       Match all 3 symbols horizontally!       ║");
        System.out.println(
                "                                                      ╠-----------------------------------------------╣");
        System.out.println(
                "                                                      ║       Top [Payline 1] = x2 multiplier         ║");
        System.out.println(
                "                                                      ║      Middle [Payline 2] = x5 multiplier       ║");
        System.out.println(
                "                                                      ║      Bottom [Payline 3] = x2 multiplier       ║");
        System.out.println(
                "                                                      ╠-----------------------------------------------╣");
        System.out.println(
                "                                                      ║      Type EXIT when placing bet to quit       ║");
        System.out.println(
                "                                                      ╠═══════════════════════════════════════════════╣");
        System.out.println("                                                      ║ Player: "
                + String.format("%-30s", player.getUsername()) + "        ║");
        System.out.println(
                "                                                      ║ Balance: "
                        + String.format("%-29s", Formatter.formatCurrency(balance)) + "        ║");
        System.out.println(
                "                                                      ╚═══════════════════════════════════════════════╝");
        ;
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    private void reelSpinAnimation() {
        System.out.println();
        String[] frames = { "|", "/", "-", "\\" };
        System.out.println("                                                      Spinning reels...");

        for (int i = 0; i < 10; i++) {
            System.out.print("\r                                                      " + frames[i % 4]);
            ConsoleDisplay.pause(150);
        }

        System.out.print("\r                                                      Spin complete!\n");
        System.out.println();
    }

    private void exitMessage() {
        System.out.println(
                "\n                                                      Thanks for playing " + getGameName() + "!");
        System.out.println("                                                      Saving your balance...");
        playerDatabase.updatePlayer(player);
        ConsoleDisplay.pause(800, "                                                      Balance saved!");
        InputValidator
                .waitForUserInput("                                                      Press Enter to continue...");
        System.out.println("\n                                                      See you next time, gambler!");
    }
}

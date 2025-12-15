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
    private final Random rand = new Random();
    // Scanner was unused; using InputValidator instead
    private String[][] lastGrid;
    private PlayerDatabase db;

    public SlotMachine() {
    }

    public SlotMachine(Player player, PlayerDatabase playerDB) {
        super(player);
        this.db = playerDB;
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
        this.db = playerDB;
        this.gameName = "Moses Bonanza Slot Machine";

        if (reels == null)
            setupReels();

        ConsoleDisplay.clearConsole();
        displayRules();

        while (true) {
            System.out.println("\n\n");
            System.out.println("            ╔════════════════════════════════════════╗");
            System.out.println("            ║             MOSES BONANZA              ║");
            System.out.println("            ╠════════════════════════════════════════╣");
            System.out.println("            ║ Player: " + String.format("%-30s", player.getUsername()) + " ║");
            System.out.println("            ║ Balance: " + String.format("%-29s", Formatter.formatCurrency(balance)) + " ║");
            System.out.println("            ╚════════════════════════════════════════╝");
            System.out.println("");
            System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
            System.out.println("            ║      1. PLAY         ║ ║    2. EXIT GAME      ║");
            System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
            System.out.print("\n            Choose (1-2): ");
            int choice = InputValidator.readInt(1, 2);
            if (choice == 2) {
                exitMessage();
                return;
            }

            // Player chose to play
            if (balance <= 0) {
                System.out.println(
                        "            You don't have any balance to place a bet — return to Casino or deposit more.");
                ConsoleDisplay.pause(800, "            Press Enter to continue...");
                ConsoleDisplay.clearConsole();
                continue;
            }

            System.out.print("\n            Place bet (type EXIT to quit): $");
            double bet = InputValidator.readDoubleOrExit(1);

            if (bet == Double.MIN_VALUE) {
                exitMessage();
                return;
            }

            if (bet > balance) {
                System.out.println("            You don't have enough balance for that bet!");
                ConsoleDisplay.pause(800);
                continue;
            }

            reelSpinAnimation();
            playRound();

            double winAmount = calculatePayout(bet);

            if (winAmount > 0) {
                System.out.println("\n             CONGRATS YOU WON!!! ");
                System.out.println("            Total Winnings: " + Formatter.formatCurrency(winAmount));
                ConsoleDisplay.pause(1200, "            Enjoy your coins! Returning to Casino...");
            } else {
                System.out.println("            No matches this round...");
                updateBalance(-bet);
            }

            updateBalance(winAmount);

            System.out.print("\n            Spin again? (Y/N): ");
            if (!InputValidator.readYesNo()) {
                exitMessage();
                return;
            }

            ConsoleDisplay.clearConsole();
        }
    }

    @Override
    public void playRound() {
        lastGrid = new String[3][3];

        for (int c = 0; c < 3; c++) {
            reels[c].spin();
            String center = reels[c].getSymbol();
            lastGrid[1][c] = center;

            lastGrid[0][c] = symbols[rand.nextInt(symbols.length)];
            lastGrid[2][c] = symbols[rand.nextInt(symbols.length)];
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

        System.out.println("\n            RESULTS");
        System.out.println("            ╔" + border.toString() + "╗");

        String rowFmt = "            ║ %" + "-" + cellWidth + "s | %" + "-" + cellWidth + "s | %" + "-" + cellWidth
                + "s ║\n";

        // subtle ANSI color highlight; if not supported, it will appear as plain text
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        for (int r = 0; r < 3; r++) {
            boolean isWinRow = lastGrid[r][0].equals(lastGrid[r][1]) && lastGrid[r][1].equals(lastGrid[r][2]);
            if (isWinRow) {
                // Color the matching row but avoid adding explicit text markers
                String c0 = String.format("%-" + cellWidth + "s", lastGrid[r][0]);
                String c1 = String.format("%-" + cellWidth + "s", lastGrid[r][1]);
                String c2 = String.format("%-" + cellWidth + "s", lastGrid[r][2]);
                System.out.printf("            ║ %s%s%s | %s%s%s | %s%s%s ║\n", ANSI_GREEN, c0, ANSI_RESET, ANSI_GREEN,
                        c1,
                        ANSI_RESET, ANSI_GREEN, c2, ANSI_RESET);
            } else {
                System.out.printf(rowFmt, lastGrid[r][0], lastGrid[r][1], lastGrid[r][2]);
            }

            if (r < 2)
                System.out.println("            ║" + border.toString() + "║");
        }
        System.out.println("            ╚" + border.toString() + "╝");
    }

    public double calculatePayout(double bet) {
        double win = 0;

        for (int r = 0; r < 3; r++) {
            if (lastGrid[r][0].equals(lastGrid[r][1]) && lastGrid[r][1].equals(lastGrid[r][2])) {
                int multi = multipliers[r];
                double lineWin = bet * multi;
                win += lineWin;
                System.out.println("            Payline " + (r + 1) + " matched! (x" + multi + ") -> "
                        + Formatter.formatCurrency(lineWin));
            }
        }

        if (win > 0) {
            System.out.println("            You won this round: " + Formatter.formatCurrency(win));
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
        db.updatePlayer(player);
        Transaction.log(player.getUsername(), player.getPlayerId(), getGameName(), "SPIN_RESULT", amount, balance);
    }

    @Override
    public void displayRules() {
        System.out.println("\n            ╔════════════════════════════════════════╗");
        System.out.println("            ║             MOSES BONANZA              ║");
        System.out.println("            ╠════════════════════════════════════════╣");
        System.out.println("            ║   Match all 3 symbols horizontally!    ║");
        System.out.println("            ║  Top = x2  |  Mid = x5  |  Bottom = x2 ║");
        System.out.println("            ║   Type EXIT when placing bet to quit   ║");
        System.out.println("            ╚════════════════════════════════════════╝");
        ConsoleDisplay.pause(1000);
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    private void reelSpinAnimation() {
        String[] frames = { "|", "/", "-", "\\" };
        System.out.println("            🎰 Spinning reels...");

        for (int i = 0; i < 10; i++) {
            System.out.print("\r            " + frames[i % 4]);
            ConsoleDisplay.pause(90);
        }

        System.out.print("\r            ✅ Spin complete!\n");
        ConsoleDisplay.pause(600);
    }

    private void exitMessage() {
        System.out.println("\n            Thanks for playing " + getGameName() + "!");
        System.out.println("            Saving your balance...");
        db.updatePlayer(player);
        ConsoleDisplay.pause(800, "            Balance saved!");
        ConsoleDisplay.pause("            Press Enter to return to Casino...");
        System.out.println("\n            See you next time, gambler!");
    }
}

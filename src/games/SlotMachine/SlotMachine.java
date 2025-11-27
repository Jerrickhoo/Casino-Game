package games.SlotMachine;

import java.util.Random;
import java.util.Scanner;
import Core.Player;
import Core.PlayerDatabase;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import utilities.InputValidator;
import games.Game;

public class SlotMachine extends Game {
    private Reel[] reels;
    private final String[] symbols = {"Justin", "Joseph", "Moses", "Gian", "Marque"};
    private final int[] multipliers = {2, 5, 2};
    private final Random rand = new Random();
    private final Scanner sc = new Scanner(System.in);
    private String[][] lastGrid;
    private PlayerDatabase db;

    public SlotMachine() {}

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
        
        if (reels == null) setupReels();

        ConsoleDisplay.clearConsole();
        displayRules();

        while (true) {
            System.out.println("            ╔════════════════════════════════════════╗");
            System.out.println("            ║ Player: " + String.format("%-30s", player.getUsername()) + " ║");
            System.out.println("            ║ Balance: " + String.format("%-29s", Formatter.formatCurrency(balance)) + " ║");
            System.out.println("            ╚════════════════════════════════════════╝");

            System.out.print("            Place bet (type EXIT to quit): $");
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
                balance -= bet;
                updateBalance(-bet);
            }

            updateBalance(winAmount);

            System.out.println("\n            Spin again? (Y/N): ");
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

        System.out.println("\n            RESULTS");
        System.out.println("            ╔═══════════════════════════════╗");
        for (int r = 0; r < 3; r++) {
            System.out.printf("             %s | %s | %s\n", lastGrid[r][0], lastGrid[r][1], lastGrid[r][2]);
            if (r < 2) System.out.println("            ║═══════════════════════════════║");
        }
        System.out.println("            ╚═══════════════════════════════╝");
    }

    public double calculatePayout(double bet) {
        double win = 0;

        for (int r = 0; r < 3; r++) {
            if (lastGrid[r][0].equals(lastGrid[r][1]) && lastGrid[r][1].equals(lastGrid[r][2])) {
                int multi = multipliers[r];
                double lineWin = bet * multi;
                win += lineWin;
                System.out.println("            Payline " + (r + 1) + " matched! (x" + multi + ") -> " + Formatter.formatCurrency(lineWin));
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
        if (balance < 0) balance = 0;

        player.setBalance(balance);
        db.updatePlayer(player);
        db.logTransaction(player.getUsername(), getGameName(), "SPIN_RESULT", amount, balance);
    }

    @Override
    public void displayRules() {
        System.out.println("\n            ╔════════════════════════════════════════╗");
        System.out.println("            ║             MOSES BONANZA              ║");
        System.out.println("            ╠════════════════════════════════════════╣");
        System.out.println("            ║   Match all 3 symbols horizontally!    ║");
        System.out.println("            ║   Top = x2  |  Mid = x5  |  Bottom = x2║");
        System.out.println("            ║   Type EXIT when placing bet to quit  ║");
        System.out.println("            ╚════════════════════════════════════════╝");
        ConsoleDisplay.pause(1000);
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    private void reelSpinAnimation() {
        String[] frames = {"|", "/", "-", "\\"};
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
        sc.close();
        System.out.println("\n            See you next time, gambler!");
    }
}

package games.AceRoll;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import Core.Player;
import Core.PlayerDatabase;
import games.Game;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import utilities.InputValidator;

/**
 * Top-level game orchestrator for Dice Poker. This class coordinates
 * rounds, reads player choices (via InputValidator), and compares hands.
 * It no longer performs dice-hand evaluation directly — that lives in DiceSet.
 */
public class AceRoll extends Game {
    private double botBalance = 1000.0;
    private PlayerDatabase db;

    public AceRoll() {
        super();
    }

    public AceRoll(Player player) {
        super(player);
    }

    // simple bot balance tracked locally for betting rounds

    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        this.player = player;
        this.balance = player.getBalance();
        this.db = playerDB;

        ConsoleDisplay.clearConsole();
        displayRules();

        while (mainMenu()) {
            ConsoleDisplay.clearConsole();
            System.out.println("                                     ╔═════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("                                     ║               Welcome to Ace Roll! Your round will be starting...               ║");
            System.out.println("                                     ╚═════════════════════════════════════════════════════════════════════════════════╝");
            ConsoleDisplay.pause(1500);
            playRound(player);
        }
    }

    public boolean mainMenu() {
        System.out.println("\n\n");
        System.out.println("                                     ╔══════════════════════════════════════════════╗");
        System.out.println("                                     ║                   ACE ROLL                   ║");
        System.out.println("                                     ╠══════════════════════════════════════════════╣");
        System.out.println("                                     ║ Player: " + String.format("%-36s", player.getUsername()) + " ║");
        System.out.println("                                     ║ Balance: " + String.format("%-35s", Formatter.formatCurrency(player.getBalance())) + " ║");
        System.out.println("                                     ╚══════════════════════════════════════════════╝");
        System.out.println("");
        System.out.println("                                     ╔══════════════════════╗ ╔══════════════════════╗");
        System.out.println("                                     ║      1. PLAY         ║ ║    2. EXIT GAME      ║");
        System.out.println("                                     ╚══════════════════════╝ ╚══════════════════════╝");
        System.out.println("                                     ╔═══════════════════════════════════════════════╗");
        System.out.print("                                     Choose (1-2): ");
        int choice = InputValidator.readInt(1, 2);
        return choice == 1;
    }

    @Override
    public void displayRules() {
        System.out.println("\n\n");
        System.out.println("                                                  ╔════════════════════════════════════════════════════════╗");
        System.out.println("                                                  ║                     HOW TO PLAY                        ║");
        System.out.println("                                                  ╠════════════════════════════════════════════════════════╣");
        System.out.println("                                                  ║  1) Both players ante to start the round.              ║");
        System.out.println("                                                  ║  2) Both roll all dice and see their own hand.         ║");
        System.out.println("                                                  ║                                                        ║");
        System.out.println("                                                  ║  3) BETTING PHASE 1:                                   ║");
        System.out.println("                                                  ║     - You may CHECK / CALL / RAISE / FOLD.             ║");
        System.out.println("                                                  ║     - You may also SORT your hand to view it easily    ║");
        System.out.println("                                                  ║     - Opponent responds (CHECK / CALL / RAISE / FOLD). ║");
        System.out.println("                                                  ║     - If someone folds, the other wins the pot.        ║");
        System.out.println("                                                  ║                                                        ║");
        System.out.println("                                                  ║  4) If both players are still in, you may REROLL       ║");
        System.out.println("                                                  ║     any dice to try improving your hand.               ║");
        System.out.println("                                                  ║                                                        ║");
        System.out.println("                                                  ║  5) BETTING PHASE 2 (same rules as Phase 1).           ║");
        System.out.println("                                                  ║                                                        ║");
        System.out.println("                                                  ║  6) If nobody folds, hands are revealed and            ║");
        System.out.println("                                                  ║     the higher hand wins the pot.                      ║");
        System.out.println("                                                  ║                                                        ║");
        System.out.println("                                                  ║  7) Play another round?                                ║");
        System.out.println("                                                  ╚════════════════════════════════════════════════════════╝");
        System.out.print("                                                  ");
        InputValidator.waitForUserInput();
        ConsoleDisplay.clearConsole();

    }

    @Override
    public void playRound(Player player) {

        if (!checkBankrupt(player, botBalance)) {
            return;
        }

        // show balances
        System.out.println("            ╔═════════════════════════════════════════════════════╗");
        System.out.println("            ║ Your balance: "
                + String.format("%-37s", utilities.Formatter.formatCurrency(player.getBalance())) + " ║");
        System.out.println("            ║ Opponent balance: "
                + String.format("%-33s", utilities.Formatter.formatCurrency(botBalance)) + " ║");
        System.out.println("            ╚═════════════════════════════════════════════════════╝");
        System.out.println("            ╔═════════════════════════════════════════════════════╗");
        System.out.print("             Enter your bet amount (minimum 1.0): ");
        double bet = InputValidator.readDouble(1.0, player.getBalance());

        // initialize pot and deduct ante from both
        double pot = 0.0;
        player.setBalance(player.getBalance() - bet);
        double botAnte = Math.min(bet, botBalance);
        botBalance -= botAnte;
        pot += bet + botAnte;

        ConsoleDisplay.clearConsole();
        System.out.println("            ╔═════════════════════════════════════════════════════╗");
        System.out.println("            ║   Pot initialized at: "
                + String.format("%-29s", utilities.Formatter.formatCurrency(pot)) + " ║");
        System.out.println("            ╚═════════════════════════════════════════════════════╝");
        ConsoleDisplay.pause(1000);

        System.out.println("            ╔═════════════════════════════════════════════════════╗");
        System.out.println("              Now Rolling Dice...");
        ConsoleDisplay.pause(1500);

        // create hands
        DiceSet playerHand = new DiceSet();
        DiceSet botHand = new DiceSet();

        // initial roll for both
        playerHand.rollAll();
        botHand.rollAll();

        ConsoleDisplay.clearConsole();

        // Run a full betting round (pre-reroll)
        BettingRound bettingRoundPre = new BettingRound(player, playerHand, botHand, pot, botBalance);
        BettingResult pre = bettingRoundPre.run();
        pot = pre.pot;
        botBalance = bettingRoundPre.getBotBalance();
        if (pre.folded) {
            // fold resolved: award pot to winner
            if (pre.foldWinner == 1) {
                // player wins
                player.setBalance(player.getBalance() + pot);
                System.out.println("            ╔═════════════════════════════════════════════════════════╗");
                System.out.println("            ║   Opponent folded. You win the pot of "
                        + String.format("%-17s", utilities.Formatter.formatCurrency(pot)) + " ║");
                System.out.println("            ╚═════════════════════════════════════════════════════════╝");
                InputValidator.waitForUserInput();
            } else if (pre.foldWinner == -1) {
                botBalance += pot;
                System.out.println("            ╔═════════════════════════════════════════════════════════╗");
                System.out.println("            ║   You folded. Opponent wins the pot of "
                        + String.format("%-16s", utilities.Formatter.formatCurrency(pot)) + " ║");
                System.out.println("            ╚═════════════════════════════════════════════════════════╝");
                InputValidator.waitForUserInput();
            }
            ConsoleDisplay.clearConsole();
            return;
        }

        // apply exactly one reroll phase for player and bot
        ConsoleDisplay.clearConsole();
        showHandUI(playerHand);
        System.out
                .println("            ╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.print("             Enter dice positions to reroll (1-5, space-separated), or 0 to keep: ");
        String line = InputValidator.readString();
        List<Integer> sel = parseSelectionLine(line);
        playerHand.reroll(sel);

        // bot decides what to reroll using its AI and then rerolls
        List<Integer> botSel = DiceBotAI.selectReroll(botHand);
        botHand.reroll(botSel);

        ConsoleDisplay.clearConsole();

        // Run a full betting round (pre-reroll)
        BettingRound bettingRoundPost = new BettingRound(player, playerHand, botHand, pot, botBalance);
        BettingResult post = bettingRoundPost.run();
        pot = post.pot;
        botBalance = bettingRoundPost.getBotBalance();
        if (post.folded) {
            // fold resolved: award pot to winner
            if (post.foldWinner == 1) {
                // player wins
                player.setBalance(player.getBalance() + pot);
                System.out.println("            ╔═════════════════════════════════════════════════════════╗");
                System.out.println("            ║   Opponent folded. You win the pot of "
                        + String.format("%-17s", utilities.Formatter.formatCurrency(pot)) + " ║");
                System.out.println("            ╚═════════════════════════════════════════════════════════╝");
                InputValidator.waitForUserInput();
            } else if (post.foldWinner == -1) {
                botBalance += pot;
                System.out.println("            ╔═════════════════════════════════════════════════════════╗");
                System.out.println("            ║   You folded. Opponent wins the pot of "
                        + String.format("%-16s", utilities.Formatter.formatCurrency(pot)) + " ║");
                System.out.println("            ╚═════════════════════════════════════════════════════════╝");
                InputValidator.waitForUserInput();
            }
            ConsoleDisplay.clearConsole();
            return;
        }

        // evaluate
        DiceRank pRank = playerHand.evaluateHand();
        DiceRank bRank = botHand.evaluateHand();
        ConsoleDisplay.clearConsole();
        showHandUI(playerHand);
        System.out.println("            ╔═══════════════════════╗");
        System.out.println("            ║    OPPONENT'S HAND    ║");
        System.out.println("            ╚═══════════════════════╝");
        System.out.println("            ╔═══════════════════════╗");
        botHand.showHand();
        System.out.println("            ╚═══════════════════════╝");

        System.out.println("        ╔════════════════════════════════╗");
        System.out.println("        ║    You: " + String.format("%-22s", pRank) + " ║");
        System.out.println("        ║    Opponent: " + String.format("%-6s", bRank) + " ║");
        System.out.println("        ╚════════════════════════════════╝");

        int cmp = pRank.compareTo(bRank);
        int winner = 0; // 1=player, -1=bot, 0=tie
        if (cmp > 0) {
            System.out.println("             ╔════════════════════════╗");
            System.out.println("             ║      Player Wins!      ║");
            System.out.println("             ╚════════════════════════╝");
            winner = 1;
        } else if (cmp < 0) {
            System.out.println("             ╔════════════════════════╗");
            System.out.println("             ║     Opponent Wins!     ║");
            System.out.println("             ╚════════════════════════╝");
            winner = -1;
        } else {
            // tie-breaker: compare sorted dice descending
            int[] pSorted = playerHand.getSortedDescending();
            int[] bSorted = botHand.getSortedDescending();
            boolean tie = true;
            for (int i = 0; i < pSorted.length; i++) {
                if (pSorted[i] > bSorted[i]) {
                    System.out.println("             ╔═════════════════════════╗");
                    System.out.println("             ║       Player Wins!      ║");
                    System.out.println("             ║       (tie-break)       ║");
                    System.out.println("             ╚═════════════════════════╝");

                    winner = 1;
                    tie = false;
                    break;
                }
                if (pSorted[i] < bSorted[i]) {
                    System.out.println("             ╔═════════════════════════╗");
                    System.out.println("             ║      Opponent Wins!     ║");
                    System.out.println("             ║       (tie-break)       ║");
                    System.out.println("             ╚═════════════════════════╝");
                    winner = -1;
                    tie = false;
                    break;
                }
            }
            if (tie) {
                System.out.println("             ╔═══════════════════════╗");
                System.out.println("             ║          Tie!         ║");
                System.out.println("             ╚═══════════════════════╝");
                winner = 0;
            }
        }

        // award pot
        if (winner == 1) {
            player.setBalance(player.getBalance() + pot);
            System.out.println("       ╔══════════════════════════════════╗");
            System.out.println("       ║  You win " + String.format("%-23s", utilities.Formatter.formatCurrency(pot)) + " ║");
            System.out.println("       ║  New balance: " + String.format("%-18s", utilities.Formatter.formatCurrency(player.getBalance())) + " ║");
            System.out.println("       ╚══════════════════════════════════╝");
        } else if (winner == -1) {
            botBalance += pot;
            System.out.println("       ╔══════════════════════════════════╗");
            System.out.println("       ║  Opponent wins " + String.format("%-17s", utilities.Formatter.formatCurrency(pot)) + " ║");
            System.out.println("       ║  Opponent balance: " + String.format("%-13s", utilities.Formatter.formatCurrency(botBalance)) + " ║");
            System.out.println("       ╚══════════════════════════════════╝");
        } else {
            double half = Math.floor(pot * 100.0 / 2.0) / 100.0; // split cents evenly
            player.setBalance(player.getBalance() + half);
            botBalance += (pot - half);
            System.out.println("       ╔══════════════════════════════════╗");
            System.out.println("       ║  Pot split. You get " + String.format("%-12s", utilities.Formatter.formatCurrency(half)) + " ║");
            System.out.println("       ║  New balance: " + String.format("%-18s", utilities.Formatter.formatCurrency(player.getBalance())) + " ║");
            System.out.println("       ╚══════════════════════════════════╝");
        }

        db.updatePlayer(player);
        InputValidator.waitForUserInput();

        ConsoleDisplay.clearConsole();
    }

    public static void showHandUI(DiceSet playerHand) {
        System.out.println("            ╔═══════════════════════╗");
        System.out.println("            ║   YOUR CURRENT HAND   ║");
        System.out.println("            ╚═══════════════════════╝");
        System.out.println("            ╔═══════════════════════╗");
        playerHand.showHand();
        System.out.println("            ╚═══════════════════════╝");
    }

    public static void showSortedHand(DiceSet playerHand) {
        ConsoleDisplay.clearConsole();
        System.out.println("            ╔═══════════════════════╗");
        System.out.println("            ║   YOUR CURRENT HAND   ║");
        System.out.println("            ║     (SORTED DESC)     ║");
        System.out.println("            ╚═══════════════════════╝");
        System.out.println("            ╔═══════════════════════╗");
        int[] sorted = playerHand.getSortedDescending();
        for (int i = 0; i < sorted.length; i++) {
            System.out.println(utilities.Formatter.numToDice(sorted[i], i + 1));
        }
        System.out.println("            ╚═══════════════════════╝");
    }

    private List<Integer> parseSelectionLine(String line) {
        List<Integer> selectedPositions = new ArrayList<>();
        if (line == null)
            return selectedPositions;
        String[] parts = line.trim().split("\\s+");
        for (String part : parts) {
            try {
                int v = Integer.parseInt(part);
                if (v == 0 && parts.length == 1)
                    return new ArrayList<>(); // keep all
                if (v == 0)
                    continue;
                if (v >= 1 && v <= 5)
                    selectedPositions.add(v - 1);
            } catch (NumberFormatException e) {
                // ignore invalid token
            }
        }
        return selectedPositions;
    }

    @Override
    public double calculatePayout() {
        return 0;
    }

    public boolean checkBankrupt(Player player, double botBalance) {
        if (player.getBalance() <= 0.0) {
            System.out.println("            ╔═════════════════════════════════════════════════════════════════╗");
            System.out.println("            ║  You have no funds to start another round. Returning to lobby.  ║");
            System.out.println("            ╚═════════════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput();
            return false;

        } else if (botBalance <= 0.0) {
            System.out.println("            ╔═════════════════════════════════════════════════════════════════╗");
            System.out.println("            ║      Opponent has no funds to continue. Returning to lobby.     ║");
            System.out.println("            ╚═════════════════════════════════════════════════════════════════╝");
            InputValidator.waitForUserInput();
            return false;
        } else {
            ConsoleDisplay.clearConsole();
            return true;
        }
    }

    @Override
    public String getGameName() {
        return "AceRoll";
    }

    @Override
    public void updateBalance(double amount) {
        /* stub */ }

    @Override
    public void playRound() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playRound'");
    }
}

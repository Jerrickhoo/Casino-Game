package games.DicePoker;

import java.util.ArrayList;
import java.util.List;

import Core.Player;
import Core.PlayerDatabase;
import games.Game;
import utilities.ConsoleDisplay;
import utilities.InputValidator;

/**
 * Top-level game orchestrator for Dice Poker. This class coordinates
 * rounds, reads player choices (via InputValidator), and compares hands.
 * It no longer performs dice-hand evaluation directly — that lives in DiceSet.
 */
public class DicePoker extends Game {
    public DicePoker() {
        super();
    }

    public DicePoker(Player player) {
        super(player);
    }

    // simple bot balance tracked locally for betting rounds
    private double botBalance = 1000.0;

    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        ConsoleDisplay.clearConsole();
        System.out.println("");
        displayRules();
        System.out.println("Welcome to Dice Poker! Your first round will be starting...");
        ConsoleDisplay.pause(1500);

        boolean keepPlaying = true;
        while (keepPlaying) {
            if (player == null) {
                System.out.println("No player account provided. Returning to lobby.");
                return;
            }

            // show balances
            System.out.println("Your balance: " + utilities.Formatter.formatCurrency(player.getBalance()));
            System.out.println("Opponent balance: " + utilities.Formatter.formatCurrency(botBalance));

            // Check if player can afford the minimum bet
            if (!player.canAfford(1.0)) {
                System.out.println("You cannot afford the minimum bet of 1.0. Returning to casino.");
                return;
            }

            // ask for ante / bet amount
            System.out.print("Enter your bet amount (minimum 1.0): ");
            double bet = InputValidator.readDouble(1.0, player.getBalance());

            // Verify player can afford the chosen bet
            if (!player.canAfford(bet)) {
                System.out.println("You cannot afford that bet. Please enter a valid amount.");
                continue;
            }

            // initialize pot and deduct ante from both
            double pot = 0.0;
            player.setBalance(player.getBalance() - bet);
            double botAnte = Math.min(bet, botBalance);
            botBalance -= botAnte;
            pot += bet + botAnte;
            ConsoleDisplay.clearConsole();
            System.out.println("Pot initialized at: " + utilities.Formatter.formatCurrency(pot));
            ConsoleDisplay.pause(1500, "Now Rolling Dice...");
            // create hands
            DiceSet playerHand = new DiceSet();
            DiceSet botHand = new DiceSet();

            // initial roll for both
            playerHand.rollAll();
            botHand.rollAll();

            System.out.println("Your initial roll:");
            playerHand.showHand();

            // Run a full betting round (pre-reroll)
            BettingResult preRound = runBettingRound(player, playerHand, botHand, pot);
            pot = preRound.pot;
            if (preRound.folded) {
                // fold resolved: award pot to winner
                if (preRound.foldWinner == 1) {
                    // player wins
                    player.setBalance(player.getBalance() + pot);
                    System.out
                            .println("Opponent folded. You win the pot of " + utilities.Formatter.formatCurrency(pot));
                } else if (preRound.foldWinner == -1) {
                    botBalance += pot;
                    System.out
                            .println("You folded. Opponent wins the pot of " + utilities.Formatter.formatCurrency(pot));
                }
                System.out.print("Play again? (Y/N): ");
                keepPlaying = InputValidator.readYesNo();
                continue;
            }

            // apply exactly one reroll phase for player and bot
            System.out.print("Enter dice positions to reroll (1-5, space-separated), or 0 to keep: ");
            String line = InputValidator.readString();
            List<Integer> selectedPositions = parseSelectionLine(line);
            playerHand.reroll(selectedPositions);

            // bot decides what to reroll using its AI and then rerolls
            List<Integer> botSelectedPositions = DiceBotAI.selectReroll(botHand);
            botHand.reroll(botSelectedPositions);

            System.out.println("After your reroll:");
            playerHand.showHand();

            // Run a full betting round (pre-reroll)
            BettingResult postRound = runBettingRound(player, playerHand, botHand, pot);
            pot = postRound.pot;
            if (postRound.folded) {
                // fold resolved: award pot to winner
                if (postRound.foldWinner == 1) {
                    // player wins
                    player.setBalance(player.getBalance() + pot);
                    System.out
                            .println("Opponent folded. You win the pot of " + utilities.Formatter.formatCurrency(pot));
                } else if (postRound.foldWinner == -1) {
                    botBalance += pot;
                    System.out
                            .println("You folded. Opponent wins the pot of " + utilities.Formatter.formatCurrency(pot));
                }
                System.out.print("Play again? (Y/N): ");
                keepPlaying = InputValidator.readYesNo();
                continue;
            }
            // evaluate
            DiceRank playerRank = playerHand.evaluateHand();
            DiceRank botRank = botHand.evaluateHand();

            System.out.println("\nOpponent's hand:");
            botHand.showHand();

            System.out.println("You: " + playerRank + "  Opponent: " + botRank);
            int comparison = playerRank.compareTo(botRank);
            int winner = 0; // 1=player, -1=bot, 0=tie
            if (comparison > 0) {
                System.out.println("Player Wins!");
                winner = 1;
            } else if (comparison < 0) {
                System.out.println("Opponent Wins!");
                winner = -1;
            } else {
                // tie-breaker: compare sorted dice descending
                int[] playerSorted = playerHand.getSortedDescending();
                int[] botSorted = botHand.getSortedDescending();
                boolean tie = true;
                for (int i = 0; i < playerSorted.length; i++) {
                    if (playerSorted[i] > botSorted[i]) {
                        System.out.println("Player Wins (tie-break)");
                        winner = 1;
                        tie = false;
                        break;
                    }
                    if (playerSorted[i] < botSorted[i]) {
                        System.out.println("Opponent Wins (tie-break)");
                        winner = -1;
                        tie = false;
                        break;
                    }
                }
                if (tie) {
                    System.out.println("Tie");
                    winner = 0;
                }
            }

            // award pot
            if (winner == 1) {
                player.setBalance(player.getBalance() + pot);
                System.out.println("You win " + utilities.Formatter.formatCurrency(pot) + "! New balance: "
                        + utilities.Formatter.formatCurrency(player.getBalance()));
            } else if (winner == -1) {
                botBalance += pot;
                System.out.println("Opponent wins the pot of " + utilities.Formatter.formatCurrency(pot)
                        + ". Opponent balance: " + utilities.Formatter.formatCurrency(botBalance));
            } else {
                double half = Math.floor(pot * 100.0 / 2.0) / 100.0; // split cents evenly
                player.setBalance(player.getBalance() + half);
                botBalance += (pot - half);
                System.out.println("Pot split. You get " + utilities.Formatter.formatCurrency(half) + ". New balance: "
                        + utilities.Formatter.formatCurrency(player.getBalance()));
            }

            System.out.print("Play again? (Y/N): ");
            keepPlaying = InputValidator.readYesNo();
            ConsoleDisplay.clearConsole();
        }
    }

    private List<Integer> parseSelectionLine(String line) {
        List<Integer> selectedPositions = new ArrayList<>();
        if (line == null)
            return selectedPositions;
        String[] parts = line.trim().split("\\s+");
        for (String p : parts) {
            try {
                int v = Integer.parseInt(p);
                if (v == 0)
                    return new ArrayList<>(); // keep all
                if (v >= 1 && v <= 5)
                    selectedPositions.add(v - 1);
            } catch (NumberFormatException e) {
                // ignore invalid token
            }
        }
        return selectedPositions;
    }

    // Helper result for betting rounds
    private static class BettingResult {
        double pot;
        boolean folded;
        // foldWinner: 1 = player wins (bot folded), -1 = bot wins (player folded), 0 =
        // none
        int foldWinner;

        BettingResult(double pot, boolean folded, int foldWinner) {
            this.pot = pot;
            this.folded = folded;
            this.foldWinner = foldWinner;
        }
    }

    /**
     * Run a two-player betting round between the human player and the bot.
     * Both players have already posted ante (included in pot). This method
     * allows multiple raises (capped) and returns the updated pot and whether
     * someone folded.
     */
    private BettingResult runBettingRound(Player player, DiceSet playerHand, DiceSet botHand, double pot) {
        double playerContrib = 0.0;
        double botContrib = 0.0;
        int raises = 0;
        final int maxRaises = 3;

        boolean playerDone = false;
        boolean botDone = false;
        boolean playerTurn = true; // player acts first

        while (true) {
            if (playerTurn) {
                double toCall = botContrib - playerContrib;
                if (toCall <= 0) {
                    // can check or bet
                    System.out.println("Your action: 1) Check 2) Bet/Raise");
                    int choice = InputValidator.readInt(1, 2);
                    if (choice == 1) {
                        playerDone = true;
                    } else {
                        // raise amount
                        System.out.print("Enter raise amount: ");
                        double amount = InputValidator.readDouble(1.0, player.getBalance());
                        // Check if player can afford the raise
                        if (!player.canAfford(amount)) {
                            System.out.println("You cannot afford that raise amount. Your action is forfeited.");
                            return new BettingResult(pot, true, -1);
                        }
                        // pay amount
                        double pay = Math.min(amount, player.getBalance());
                        player.setBalance(player.getBalance() - pay);
                        playerContrib += pay;
                        pot += pay;
                        raises++;
                        // after raise, other side must respond
                        botDone = false;
                        playerDone = true;
                        System.out.println("Opponent is Thinking...");
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                        playerHand.showHand();
                    }
                } else {
                    // must call, raise, or fold
                    System.out.println(
                            "Your action: 1) Call " + utilities.Formatter.formatCurrency(toCall) + " 2) Raise 3) Fold");
                    int choice = InputValidator.readInt(1, 3);
                    if (choice == 1) {
                        double pay = Math.min(toCall, player.getBalance());
                        player.setBalance(player.getBalance() - pay);
                        playerContrib += pay;
                        pot += pay;
                        playerDone = true;
                    } else if (choice == 2) {
                        // raise additional
                        System.out.print("Enter raise amount (additional over call): ");
                        double extra = InputValidator.readDouble(1.0, player.getBalance());
                        // Check if player can afford the total raise (toCall + extra)
                        double totalRaise = toCall + extra;
                        if (!player.canAfford(totalRaise)) {
                            System.out.println("You cannot afford that raise amount. You fold.");
                            return new BettingResult(pot, true, -1);
                        }
                        double pay = Math.min(player.getBalance(), toCall + extra);
                        player.setBalance(player.getBalance() - pay);
                        playerContrib += pay;
                        pot += pay;
                        raises++;
                        botDone = false;
                        playerDone = true;
                    } else {
                        // fold -> bot wins
                        return new BettingResult(pot, true, -1);
                    }
                }
            } else {
                // bot's turn
                double toCall = playerContrib - botContrib;
                DiceRank botRank = botHand.evaluateHand();
                double botDecision = DiceBotAI.decideBet(toCall, pot, botBalance, botRank, player.getBalance(),
                        maxRaises - raises);
                if (toCall <= 0) {
                    // toCall==0, botDecision>0 => raise, 0 => check
                    if (botDecision > 0 && raises < maxRaises) {
                        double raise = Math.min(botDecision, botBalance);
                        botBalance -= raise;
                        botContrib += raise;
                        pot += raise;
                        raises++;
                        playerDone = false;
                        botDone = true;
                        System.out.println("Opponent bets " + utilities.Formatter.formatCurrency(raise));
                    } else {
                        botDone = true;
                        System.out.println("Opponent checks.");
                    }
                } else {
                    // toCall > 0
                    if (botDecision < 0) {
                        // fold
                        System.out.println("Opponent folds.");
                        return new BettingResult(pot, true, 1);
                    } else if (botDecision == 0) {
                        // call
                        double pay = Math.min(toCall, botBalance);
                        botBalance -= pay;
                        botContrib += pay;
                        pot += pay;
                        botDone = true;
                        System.out.println("Opponent calls " + utilities.Formatter.formatCurrency(pay));
                    } else {
                        // raise: botDecision is extra over call
                        double pay = Math.min(botBalance, toCall + botDecision);
                        botBalance -= pay;
                        botContrib += pay;
                        pot += pay;
                        raises++;
                        playerDone = false;
                        botDone = true;
                        System.out.println("Opponent raises to add " + utilities.Formatter.formatCurrency(pay));
                    }
                }
            }

            // check termination: both have acted and no outstanding call
            double outstanding = Math.abs(playerContrib - botContrib);
            if (playerDone && botDone && outstanding == 0)
                break;

            // switch turn
            playerTurn = !playerTurn;
        }

        return new BettingResult(pot, false, 0);
    }

    @Override
    public void playRound() {
        /* not used */ }

    @Override
    public double calculatePayout() {
        return 0; // placeholder — integrate with betting system later
    }

    @Override
    public void displayRules() {
        System.out.println("\n\n");
        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│                      HOW TO PLAY                       │");
        System.out.println("├────────────────────────────────────────────────────────┤");
        System.out.println("│  1) Both players ante to start the round.              │");
        System.out.println("│  2) Both roll all dice and see their own hand.         │");
        System.out.println("│                                                        │");
        System.out.println("│  3) BETTING PHASE 1:                                   │");
        System.out.println("│     - You may CHECK or RAISE.                          │");
        System.out.println("│     - Opponent responds (CHECK / CALL / RAISE / FOLD). │");
        System.out.println("│     - If someone folds, the other wins the pot.        │");
        System.out.println("│                                                        │");
        System.out.println("│  4) If both players are still in, you may REROLL       │");
        System.out.println("│     any dice to try improving your hand.               │");
        System.out.println("│                                                        │");
        System.out.println("│  5) BETTING PHASE 2 (same rules as Phase 1).           │");
        System.out.println("│                                                        │");
        System.out.println("│  6) If nobody folds, hands are revealed and            │");
        System.out.println("│     the higher hand wins the pot.                      │");
        System.out.println("│                                                        │");
        System.out.println("│  7) Play another round?                                │");
        System.out.println("└────────────────────────────────────────────────────────┘");
        InputValidator.waitForUserInput();
        ConsoleDisplay.clearConsole();

    }

    @Override
    public String getGameName() {
        return "Dice Poker";
    }

    @Override
    public void updateBalance(double amount) {
        /* stub */ }
}

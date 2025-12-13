package games.DicePoker;

import java.util.ArrayList;
import java.util.InputMismatchException;
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
        playRound(player);

    }

    private List<Integer> parseSelectionLine(String line) {
        List<Integer> sel = new ArrayList<>();
        if (line == null)
            return sel;
        String[] parts = line.trim().split("\\s+");
        for (String p : parts) {
            try {
                int v = Integer.parseInt(p);
                if (v == 0)
                    return new ArrayList<>(); // keep all
                if (v >= 1 && v <= 5)
                    sel.add(v - 1);
            } catch (NumberFormatException e) {
                // ignore invalid token
            }
        }
        return sel;

    }

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
                // show round summary so player can make an informed decision
                System.out.println("--------------------");
                System.out.println("Current pot: " + utilities.Formatter.formatCurrency(pot));
                System.out.println("Your balance: " + utilities.Formatter.formatCurrency(player.getBalance()));
                System.out.println("Opponent balance: " + utilities.Formatter.formatCurrency(botBalance));
                System.out.println("--------------------");
                System.out.println("Your current hand:");
                playerHand.showHand();

                double toCall = botContrib - playerContrib;

                // if player has no funds they cannot raise or call
                if (player.getBalance() <= 0.0) {
                    if (toCall > 0) {
                        System.out.println("You have no funds to call; you must fold.");
                        InputValidator.waitForUserInput();
                        return new BettingResult(pot, true, -1);
                    } else {
                        // only allow check or view hand
                        while (true) {
                            System.out.println("Your action: 1) Check 2) See Hand");
                            int c = InputValidator.readInt(1, 2);
                            if (c == 2) {
                                System.out.println("Your hand:");
                                playerHand.showHand();
                                continue;
                            }
                            // check
                            System.out.println("You checked. Your hand:");
                            playerHand.showHand();
                            playerDone = true;
                            break;
                        }
                    }
                }

                if (toCall <= 0) {
                    // can check or bet
                    if (player.getBalance() > 0.0) {
                        if (botBalance > 0.0) {
                            int choice;
                            while (true) {
                                System.out.println("Your action: 1) Check 2) Bet/Raise 3) See Hand");
                                choice = InputValidator.readInt(1, 3);
                                if (choice == 3) {
                                    System.out.println("Your hand:");
                                    playerHand.showHand();
                                    continue;
                                }
                                break;
                            }
                            if (choice == 1) {
                                System.out.println("You checked.");
                                System.out.println("Opponent is Thinking...");
                                ConsoleDisplay.pause(2000);
                                ConsoleDisplay.clearConsole();
                                playerDone = true;
                            } else {
                                // raise amount
                                System.out.print("Enter raise amount: ");
                                double amt = InputValidator.readDouble(1.0, player.getBalance());
                                double pay = Math.min(amt, player.getBalance());
                                player.setBalance(player.getBalance() - pay);
                                playerContrib += pay;
                                pot += pay;
                                raises++;
                                botDone = false;
                                playerDone = true;
                                System.out.println("Opponent is Thinking...");
                                ConsoleDisplay.pause(2000);
                                ConsoleDisplay.clearConsole();
                                System.out.println("Your hand:");
                                playerHand.showHand();
                            }
                        } else {
                            // bot has zero funds - disallow raising
                            while (true) {
                                System.out.println("Your action: 1) Check 2) See Hand");
                                int c = InputValidator.readInt(1, 2);
                                if (c == 2) {
                                    System.out.println("Your hand:");
                                    playerHand.showHand();
                                    continue;
                                }
                                System.out.println("You checked.");
                                System.out.println("Opponent is Thinking...");
                                ConsoleDisplay.pause(2000);
                                ConsoleDisplay.clearConsole();
                                playerDone = true;
                                break;
                            }
                        }
                    }
                } else {
                    // must call, raise, or fold (with See Hand option)
                    if (player.getBalance() <= 0.0) {
                        // Should have been handled earlier, but safeguard
                        System.out.println("You have no funds to call; you must fold.");
                        return new BettingResult(pot, true, -1);
                    }
                    if (botBalance > 0.0) {
                        int choice;
                        while (true) {
                            System.out.println("Your action: 1) Call " + utilities.Formatter.formatCurrency(toCall)
                                    + " 2) Raise 3) Fold 4) See Hand");
                            choice = InputValidator.readInt(1, 4);
                            if (choice == 4) {
                                System.out.println("Your hand:");
                                playerHand.showHand();
                                continue;
                            }
                            break;
                        }
                        if (choice == 1) {
                            double pay = Math.min(toCall, player.getBalance());
                            player.setBalance(player.getBalance() - pay);
                            playerContrib += pay;
                            pot += pay;
                            playerDone = true;
                            System.out.println("You called. Your hand:");
                            playerHand.showHand();
                        } else if (choice == 2) {
                            // raise additional
                            System.out.print("Enter raise amount (additional over call): ");
                            double extra = InputValidator.readDouble(1.0, player.getBalance());
                            double pay = Math.min(player.getBalance(), toCall + extra);
                            player.setBalance(player.getBalance() - pay);
                            playerContrib += pay;
                            pot += pay;
                            raises++;
                            botDone = false;
                            playerDone = true;
                            System.out.println("You raised. Your hand:");
                            playerHand.showHand();
                        } else {
                            // fold -> bot wins
                            return new BettingResult(pot, true, -1);
                        }
                    } else {
                        // bot has no funds — cannot raise, allow Call, Fold, See Hand
                        int choice;
                        while (true) {
                            System.out.println("Your action: 1) Call " + utilities.Formatter.formatCurrency(toCall)
                                    + " 2) Fold 3) See Hand");
                            choice = InputValidator.readInt(1, 3);
                            if (choice == 3) {
                                System.out.println("Your hand:");
                                playerHand.showHand();
                                continue;
                            }
                            break;
                        }
                        if (choice == 1) {
                            double pay = Math.min(toCall, player.getBalance());
                            player.setBalance(player.getBalance() - pay);
                            playerContrib += pay;
                            pot += pay;
                            playerDone = true;
                            System.out.println("You called. Your hand:");
                            playerHand.showHand();
                        } else {
                            // fold -> bot wins
                            return new BettingResult(pot, true, -1);
                        }
                    }
                }
            } else {
                // bot's turn
                // small pause and clear to show bot's thinking cleanly
                ConsoleDisplay.pause(700);
                ConsoleDisplay.clearConsole();
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
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                    } else {
                        botDone = true;
                        System.out.println("Opponent checks.");
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                    }
                } else {
                    // toCall > 0
                    if (botDecision < 0) {
                        // fold
                        System.out.println("Opponent folds.");
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                        return new BettingResult(pot, true, 1);
                    } else if (botDecision == 0) {
                        // call
                        double pay = Math.min(toCall, botBalance);
                        botBalance -= pay;
                        botContrib += pay;
                        pot += pay;
                        botDone = true;
                        System.out.println("Opponent calls " + utilities.Formatter.formatCurrency(pay));
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                    } else {
                        // raise: botDecision is extra over call
                        double pay = Math.min(botBalance, toCall + botDecision);
                        botBalance -= pay;
                        botContrib += pay;
                        pot += pay;
                        raises++;
                        playerDone = false;
                        botDone = true;
                        System.out.println("Opponent raises by " + utilities.Formatter.formatCurrency(botDecision));
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
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
    public void playRound(Player player) {

        boolean keepPlaying = checkBankrupt(player, botBalance);
        while (keepPlaying) {
            if (player == null) {
                System.out.println("No player account provided. Returning to lobby.");
                return;
            }

            // if opponent has no funds, we cannot start a new round

            checkBankrupt(player, botBalance);

            // show balances
            System.out.println("Your balance: " + utilities.Formatter.formatCurrency(player.getBalance()));
            System.out.println("Opponent balance: " + utilities.Formatter.formatCurrency(botBalance));

            // ask for ante / bet amount
            System.out.print("Enter your bet amount (minimum 1.0): ");
            double bet = InputValidator.readDouble(1.0, player.getBalance());

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

            // Present the initial roll clearly and let the player inspect
            ConsoleDisplay.clearConsole();

            // Run a full betting round (pre-reroll)
            BettingResult pre = runBettingRound(player, playerHand, botHand, pot);
            pot = pre.pot;
            if (pre.folded) {
                // fold resolved: award pot to winner
                if (pre.foldWinner == 1) {
                    // player wins
                    player.setBalance(player.getBalance() + pot);
                    System.out
                            .println("Opponent folded. You win the pot of " + utilities.Formatter.formatCurrency(pot));
                } else if (pre.foldWinner == -1) {
                    botBalance += pot;
                    System.out
                            .println("You folded. Opponent wins the pot of " + utilities.Formatter.formatCurrency(pot));
                }
                System.out.print("Play again? (Y/N): ");
                keepPlaying = InputValidator.readYesNo();
                if (keepPlaying) {
                    keepPlaying = checkBankrupt(player, botBalance);
                }
                continue;
            }

            // apply exactly one reroll phase for player and bot
            ConsoleDisplay.clearConsole();
            System.out.println("Your current hand:");
            playerHand.showHand();
            System.out.print("Enter dice positions to reroll (1-5, space-separated), or 0 to keep: ");
            String line = InputValidator.readString();
            List<Integer> sel = parseSelectionLine(line);
            playerHand.reroll(sel);

            // bot decides what to reroll using its AI and then rerolls
            List<Integer> botSel = DiceBotAI.selectReroll(botHand);
            botHand.reroll(botSel);

            System.out.println("After your reroll:");
            ConsoleDisplay.clearConsole();
            playerHand.showHand();
            InputValidator.waitForUserInput();
            ConsoleDisplay.clearConsole();

            // Run a full betting round (pre-reroll)
            BettingResult post = runBettingRound(player, playerHand, botHand, pot);
            pot = post.pot;
            if (post.folded) {
                // fold resolved: award pot to winner
                if (post.foldWinner == 1) {
                    // player wins
                    player.setBalance(player.getBalance() + pot);
                    System.out
                            .println("Opponent folded. You win the pot of " + utilities.Formatter.formatCurrency(pot));
                } else if (post.foldWinner == -1) {
                    botBalance += pot;
                    System.out
                            .println("You folded. Opponent wins the pot of " + utilities.Formatter.formatCurrency(pot));
                }
                System.out.print("Play again? (Y/N): ");
                keepPlaying = InputValidator.readYesNo();
                if (keepPlaying) {
                    keepPlaying = checkBankrupt(player, botBalance);
                }
                continue;
            }
            // evaluate
            DiceRank pRank = playerHand.evaluateHand();
            DiceRank bRank = botHand.evaluateHand();

            System.out.println("\nOpponent's hand:");
            botHand.showHand();

            System.out.println("You: " + pRank + "  Opponent: " + bRank);
            int cmp = pRank.compareTo(bRank);
            int winner = 0; // 1=player, -1=bot, 0=tie
            if (cmp > 0) {
                System.out.println("Player Wins!");
                winner = 1;
            } else if (cmp < 0) {
                System.out.println("Opponent Wins!");
                winner = -1;
            } else {
                // tie-breaker: compare sorted dice descending
                int[] pSorted = playerHand.getSortedDescending();
                int[] bSorted = botHand.getSortedDescending();
                boolean tie = true;
                for (int i = 0; i < pSorted.length; i++) {
                    if (pSorted[i] > bSorted[i]) {
                        System.out.println("Player Wins (tie-break)");
                        winner = 1;
                        tie = false;
                        break;
                    }
                    if (pSorted[i] < bSorted[i]) {
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
            if (keepPlaying) {
                keepPlaying = checkBankrupt(player, botBalance);
            }
            ConsoleDisplay.clearConsole();
        }
    }

    @Override
    public double calculatePayout() {
        return 0;
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

    public boolean checkBankrupt(Player player, double botBalance) {
        if (player.getBalance() <= 0.0) {
            System.out.println("You have no funds to start another round. Returning to lobby.");
            InputValidator.waitForUserInput();
            return false;

        } else if (botBalance <= 0.0) {
            System.out.println("Opponent has no funds to continue. Returning to lobby.");
            InputValidator.waitForUserInput();
            return false;
        } else {
            ConsoleDisplay.clearConsole();
            return true;
        }
    }

    @Override
    public String getGameName() {
        return "Dice Poker";
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

package games.AceRoll;

import Core.Player;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import utilities.InputValidator;

public class BettingRound {

    private final Player player;
    private final DiceSet playerHand;
    private final DiceSet botHand;

    private double pot;
    private double botBalance;

    public BettingRound(Player player, DiceSet playerHand, DiceSet botHand, double pot, double botBalance) {
        this.player = player;
        this.playerHand = playerHand;
        this.botHand = botHand;
        this.pot = pot;
        this.botBalance = botBalance;
    }

    public BettingResult run() {
        double playerContrib = 0.0;
        double botContrib = 0.0;
        int raises = 0;
        final int maxRaises = 3;

        boolean playerDone = false;
        boolean botDone = false;
        boolean playerTurn = true; // player ma una do action

        while (true) {
            if (playerTurn) {
                // summary sang pot kag player and opponent balance
                System.out.println("                                                  ╔═════════════════════════════════════╗");
                System.out.println("                                                  ║   Current pot: " + String.format("%-20s", Formatter.formatCurrency(pot)) + " ║");
                System.out.println("                                                  ║   Your balance: " + String.format("%-19s", Formatter.formatCurrency(player.getBalance())) + " ║");
                System.out.println("                                                  ║   Opponent balance: " + String.format("%-15s", Formatter.formatCurrency(botBalance)) + " ║");
                System.out.println("                                                  ╚═════════════════════════════════════╝");

                AceRoll.showHandUI(playerHand);

                double toCall = botContrib - playerContrib;

                // if bankrupt and player, bawal raise or call
                if (player.getBalance() < 1.0) {
                    if (toCall > 0) { // if may inog call, fold only
                        System.out.println("                                                  ╔═══════════════════════════════════════════════╗");
                        System.out.println("                                                  ║   You have no funds to call; you must fold.   ║");
                        System.out.println("                                                  ╚═══════════════════════════════════════════════╝");
                        System.out.println("                                                  ╔═══════════════════════════════════════════════╗");
                        InputValidator.waitForUserInput("                                                  Press Enter to Fold");
                        ConsoleDisplay.clearConsole();
                        return new BettingResult(pot, true, -1);
                    } else {
                        // if wala sang inog call, since bankrupt si player - check, fold, and sort only
                        while (true) {
                            System.out.println("                                                  ╔══════════════════════════════════════════════╗");
                            System.out.print("                                                    Your action: 1) Check 2) Fold 3) Sort Hand: ");
                            int c = InputValidator.readInt(1, 3);
                            if (c == 1) {
                                // check
                                ConsoleDisplay.clearConsole();
                                System.out.println("                                                  ╔════════════════════════════╗");
                                System.out.println("                                                  ║         You Checked        ║");
                                System.out.println("                                                  ╚════════════════════════════╝");
                                System.out.println("                                                  ╔════════════════════════════╗");
                                System.out.println("                                                  ║   Opponent is thinking...  ║");
                                System.out.println("                                                  ╚════════════════════════════╝");
                                ConsoleDisplay.pause(1500);
                                playerDone = true;
                                break;
                            } else if (c == 2) {
                                // fold
                                return new BettingResult(pot, true, -1);
                            } else {
                                // sort hand
                                AceRoll.showSortedHand(playerHand);
                                continue;
                            }
                        }
                    }
                }
                // if may money pa si player and wala inog call -- can check, bet, fold, and
                // sort
                if (toCall <= 0) {
                    // can check, bet, fold, sort
                    if (player.getBalance() >= 1.0) { // if may balance, proceed
                        if (botBalance > 0.0) { // if opponent has balance, proceed
                            int choice;
                            while (true) {
                                System.out.println("                                                  ╔════════════════════════════════════════════════════════╗");
                                System.out.print("                                                    Your action: 1) Check 2) Bet/Raise 3) Fold 4) Sort Hand: ");
                                choice = InputValidator.readInt(1, 4);
                                if (choice == 4) {
                                    // sort
                                    AceRoll.showSortedHand(playerHand);
                                    continue;
                                }
                                break;
                            }
                            if (choice == 1) {
                                // check
                                ConsoleDisplay.clearConsole();
                                System.out.println("                                                  ╔════════════════════════════╗");
                                System.out.println("                                                  ║         You Checked        ║");
                                System.out.println("                                                  ╚════════════════════════════╝");
                                System.out.println("                                                  ╔════════════════════════════╗");
                                System.out.println("                                                  ║   Opponent is thinking...  ║");
                                System.out.println("                                                  ╚════════════════════════════╝");
                                ConsoleDisplay.pause(1500);
                                playerDone = true;
                            } else if (choice == 2) {
                                // raise
                                ConsoleDisplay.clearConsole();
                                System.out.println("                                                  ╔════════════════════════════╗");
                                System.out.print("                                                    Enter raise amount: ");
                                double amt = InputValidator.readDouble(1.0, player.getBalance());
                                double pay = Math.min(amt, player.getBalance());
                                player.setBalance(player.getBalance() - pay);
                                playerContrib += pay;
                                pot += pay;
                                raises++;
                                botDone = false;
                                playerDone = true;
                                ConsoleDisplay.clearConsole();
                                System.out.println("                                                  ╔════════════════════════════╗");
                                System.out.println("                                                  ║   Opponent is thinking...  ║");
                                System.out.println("                                                  ╚════════════════════════════╝");
                                ConsoleDisplay.pause(2000);
                                ConsoleDisplay.clearConsole();
                            } else if (choice == 3) {
                                // fold
                                return new BettingResult(pot, true, -1);
                            }
                        } else {
                            // if bankrupt ang bot, bawal na mag raise
                            while (true) {
                                System.out.println("                                                  ╔═════════════════════════════════════════════╗");
                                System.out.print("                                                    Your action: 1) Check 2) Fold 3) Sort Hand: ");
                                int c = InputValidator.readInt(1, 3);
                                if (c == 1) {
                                    ConsoleDisplay.clearConsole();
                                    System.out.println("                                                  ╔════════════════════════════╗");
                                    System.out.println("                                                  ║         You Checked        ║");
                                    System.out.println("                                                  ╚════════════════════════════╝");
                                    System.out.println("                                                  ╔════════════════════════════╗");
                                    System.out.println("                                                  ║   Opponent is thinking...  ║");
                                    System.out.println("                                                  ╚════════════════════════════╝");
                                    ConsoleDisplay.pause(1500);
                                    playerDone = true;
                                    break;
                                } else if (c == 2) {
                                    // fold
                                    return new BettingResult(pot, true, -1);
                                } else {
                                    // sort hand
                                    AceRoll.showSortedHand(playerHand);
                                    continue;
                                }
                            }
                        }
                    }
                } else { // if may inog call
                    // either ma-call, raise, or fold
                    if (player.getBalance() <= 0.0) {
                        // if bankrupt ang player, fold only (for safe guard kay shouldve been handled
                        // earlier)
                        System.out.println("                                                  ╔═══════════════════════════════════════════════╗");
                        System.out.println("                                                  ║   You have no funds to call; you must fold.   ║");
                        System.out.println("                                                  ╚═══════════════════════════════════════════════╝");
                        System.out.println("                                                  ╔═══════════════════════════════════════════════╗");
                        InputValidator.waitForUserInput("                                               Press Enter to Fold");
                        return new BettingResult(pot, true, -1);
                    }
                    if (botBalance > 0.0) { // if may balance pa ang bot
                        int choice;
                        while (true) {
                            System.out.println("                                                  ╔═══════════════════════════════════════════════════════════════╗");
                            System.out.print("                                                  Your action: 1) Call " + Formatter.formatCurrency(toCall) + " 2) Raise 3) Fold 4) Sort Hand: ");
                            choice = InputValidator.readInt(1, 4);
                            if (choice == 4) {
                                // sort
                                AceRoll.showSortedHand(playerHand);
                                continue;
                            }
                            break;
                        }
                        if (choice == 1) {
                            // call the raised amount
                            double pay = Math.min(toCall, player.getBalance());
                            player.setBalance(player.getBalance() - pay);
                            playerContrib += pay;
                            pot += pay;
                            playerDone = true;
                            ConsoleDisplay.clearConsole();
                            System.out.println("                                                  ╔════════════════════════════╗");
                            System.out.println("                                                  ║         You Called         ║");
                            System.out.println("                                                  ╚════════════════════════════╝");
                            ConsoleDisplay.pause(1500);
                        } else if (choice == 2) {
                            // ma raise sa pot + the already raised money. bale opponent raise + call +
                            // player raise
                            System.out.println("                                                  ╔═══════════════════════════════════════════╗");
                            System.out.print("                                                  Enter raise amount (additional over call): ");
                            double extra = InputValidator.readDouble(1.0, player.getBalance());
                            double pay = Math.min(player.getBalance(), toCall + extra);
                            player.setBalance(player.getBalance() - pay);
                            playerContrib += pay;
                            pot += pay;
                            raises++;
                            botDone = false;
                            playerDone = true;
                            System.out.println("                                                  ╔════════════════════════════╗");
                            System.out.println("                                                  ║         You Raised         ║");
                            System.out.println("                                                  ╚════════════════════════════╝");
                            System.out.println("                                                  ╔════════════════════════════╗");
                            System.out.println("                                                  ║   Opponent is thinking...  ║");
                            System.out.println("                                                  ╚════════════════════════════╝");
                            ConsoleDisplay.pause(1500);

                        } else {
                            // fold
                            return new BettingResult(pot, true, -1);
                        }
                    } else {
                        // if bankrupt opponent — cannot raise, allow call, fold, and sort
                        int choice;
                        while (true) {
                            System.out.println("                                                  ╔═════════════════════════════════════════════════════════════════════╗");
                            System.out.print("                                                  Your action: 1) Call " + Formatter.formatCurrency(toCall) + " 2) Fold 3) Sort Hand: ");
                            choice = InputValidator.readInt(1, 3);
                            if (choice == 3) {
                                AceRoll.showSortedHand(playerHand);
                                continue;
                            }
                            break;
                        }
                        if (choice == 1) {
                            // call raised amount
                            double pay = Math.min(toCall, player.getBalance());
                            player.setBalance(player.getBalance() - pay);
                            playerContrib += pay;
                            pot += pay;
                            playerDone = true;

                            ConsoleDisplay.clearConsole();
                            System.out.println("                                                  ╔════════════════════════════╗");
                            System.out.println("                                                  ║         You Called         ║");
                            System.out.println("                                                  ╚════════════════════════════╝");
                            AceRoll.showHandUI(playerHand);
                        } else {
                            // fold
                            return new BettingResult(pot, true, -1);
                        }
                    }
                }
            } else {
                // bot's turn
                ConsoleDisplay.clearConsole();
                double toCall = playerContrib - botContrib;
                DiceRank botRank = botHand.evaluateHand();
                double botDecision = DiceBotAI.decideBet(toCall, pot, botBalance, botRank, player.getBalance(),
                        maxRaises - raises);
                if (toCall <= 0) {
                    // toCall==0, botDecision>0 => raise, 0 => check
                    if (botDecision > 0 && raises < maxRaises) {
                        double raise = Math.min(botDecision, botBalance); // pili which one and lowest
                        botBalance -= raise;
                        botContrib += raise;
                        pot += raise;
                        raises++;
                        playerDone = false;
                        botDone = true;
                        System.out.println("                                                  ╔════════════════════════════╗");
                        System.out.println("                                                  ║    Opponent Bets " + String.format("%-9s", Formatter.formatCurrency(raise)) + " ║");
                        System.out.println("                                                  ╚════════════════════════════╝");
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                    } else {
                        botDone = true;
                        System.out.println("                                                  ╔════════════════════════════╗");
                        System.out.println("                                                  ║       Opponent Checks      ║");
                        System.out.println("                                                  ╚════════════════════════════╝");
                        ConsoleDisplay.pause(2000);
                        ConsoleDisplay.clearConsole();
                    }
                } else {
                    // toCall > 0
                    if (botDecision < 0) {
                        // fold
                        System.out.println("                                                  ╔════════════════════════════╗");
                        System.out.println("                                                  ║        Opponent Folds      ║");
                        System.out.println("                                                  ╚════════════════════════════╝");
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
                        System.out.println("                                                  ╔════════════════════════════╗");
                        System.out.println("                                                  ║    Opponent Calls " + String.format("%-8s", Formatter.formatCurrency(pay)) + " ║");
                        System.out.println("                                                  ╚════════════════════════════╝");
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
                        System.out.println("                                                  ╔════════════════════════════╗");
                        System.out.println("                                                  ║  Opponent Raises by " + String.format("%-6s", Formatter.formatCurrency(botDecision)) + " ║");
                        System.out.println("                                                  ╚════════════════════════════╝");
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

    public double getPot() {
        return pot;
    }

    public double getBotBalance() {
        return botBalance;
    }
}

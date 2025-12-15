package games.Unlucky9;

import Core.Player;
import Core.PlayerDatabase;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import games.Game;

public class Unlucky9 extends Game {

    private Unlucky9UI ui;
    private Unlucky9Logic logic;

    public Unlucky9() {
        super();
        this.ui = new Unlucky9UI();
        this.logic = new Unlucky9Logic();
    }

    public static void play(Player currentPlayer, PlayerDatabase playerDB) {
        new Unlucky9().startGame(currentPlayer, playerDB);
    }

    // ===== MAIN GAME LOOP =====
    private void playWithPlayer(Player currentPlayer, PlayerDatabase playerDB) {

        this.player = currentPlayer;
        this.balance = currentPlayer.getBalance();
        logic.setBalance(balance);

        while (true) {
            try {
                ConsoleDisplay.clearConsole();

                ui.printTop();
                ui.printLine("UNLUCKY 9");
                ui.printMid();
                ui.printLine("Player: " + currentPlayer.getUsername());
                ui.printLine("Balance: " + Formatter.formatCurrency(balance));
                ui.printBot();

                System.out.println();

                ui.printTop();
                ui.printLine("1. PLAY");
                ui.printLine("2. TUTORIAL");
                ui.printLine("3. EXIT GAME");
                ui.printBot();

                int choice = ui.boxedIntInput("Choose (1-3): ", 1, 3);

                if (choice == 1) {

                    if (balance <= 0) {
                        ui.boxedMessage("NO FUNDS AVAILABLE");
                        ui.waitForInput("Press Enter...");
                        continue;
                    }

                    double bet = ui.boxedDoubleInput(
                            "Enter bet amount:",
                            1,
                            balance
                    );

                    int[] playerCards = logic.drawHand(2);
                    int[] dealerCards = logic.drawHand(2);

                    ConsoleDisplay.clearConsole();

                    ui.printTop();
                    ui.printLine("UNLUCKY 9");
                    ui.printMid();
                    ui.printLine("Bet Placed: " + Formatter.formatCurrency(bet));
                    ui.printBot();

                    ui.loadingAnimation("Dealing", 12, 140);
                    ui.displayPlayerWithOneDealer(playerCards, dealerCards, logic.handValue(playerCards));

                    if (logic.handValue(playerCards) == 9) {
                        logic.applyBet(currentPlayer, bet);
                        balance = logic.getBalance();
                        logic.applyWin(currentPlayer, bet * 3);
                        balance = logic.getBalance();
                        ui.boxedMessage("JACKPOT! WON " + Formatter.formatCurrency(bet * 3));
                        ui.waitForInput("Press Enter...");
                        continue;
                    }

                    boolean drawMore = ui.boxedYesNoInput("Draw 3rd card? (Y/N): ");

                    if (drawMore) {
                        int card = logic.drawSingle();
                        playerCards = logic.appendCard(playerCards, card);
                        ui.boxedMessage("You drew: [" + card + "]");
                        ui.loadingAnimation("Processing", 10, 140);
                    }

                    logic.applyBet(currentPlayer, bet);
                    balance = logic.getBalance();

                    ui.loadingAnimation("Revealing dealer", 10, 140);

                    int playerValue = logic.handValue(playerCards);
                    int dealerValue = logic.handValue(dealerCards);

                    ui.displayHands(playerCards, dealerCards, playerValue, dealerValue);

                    if (dealerValue <= 5) {
                        int card = logic.drawSingle();
                        dealerCards = logic.appendCard(dealerCards, card);
                        dealerValue = logic.handValue(dealerCards);
                        ui.loadingAnimation("Dealer drawing", 10, 140);
                        ui.displayHands(playerCards, dealerCards, playerValue, dealerValue);
                    }

                    resolveRound(currentPlayer, bet, playerValue, dealerValue);

                    ui.waitForInput("Press Enter...");

                } else if (choice == 2) {

                    ConsoleDisplay.clearConsole();
                    ui.printTop();
                    ui.printLine("UNLUCKY 9 - RULES");
                    ui.printMid();
                    ui.printLine("> Cards are digits 1-9");
                    ui.printLine("> Hand value = sum % 10");
                    ui.printLine("> Closest to 9 wins");
                    ui.printLine("> Exact 9 pays 3x");
                    ui.printBot();

                    ui.waitForInput("Press Enter...");

                } else {
                    return;
                }
            } catch (Exception e) {
                // Debug: Print error to help identify issues
                ConsoleDisplay.clearConsole();
                ui.printTop();
                ui.printLine("ERROR: " + e.getMessage());
                ui.printLine("The game encountered an unexpected error.");
                ui.printBot();
                ui.waitForInput("Press Enter to return...");
                return;
            }
        }
    }

    // ===== GAME LOGIC =====
    private void resolveRound(Player p, double bet, int pv, int dv) {
        double payout = logic.resolvePayout(bet, pv, dv);

        ui.loadingAnimation("Calculating result", 14, 160);

        if (payout > 0) {
            logic.applyWin(p, payout);
            balance = logic.getBalance();
            ui.boxedMessage("YOU WON " + Formatter.formatCurrency(payout));
        }
        else if (payout == 0) {
            logic.applyWin(p, bet);
            balance = logic.getBalance();
            ui.boxedMessage("PUSH — BET RETURNED");
        }
        else {
            ui.boxedMessage("YOU LOST " + Formatter.formatCurrency(bet));
        }

        ui.displayNewBalance(balance);
    }

    @Override public void startGame(Player p, PlayerDatabase db) { playWithPlayer(p, db); }
    @Override public void playRound() {}
    @Override public double calculatePayout() { return 0; }
    @Override public void displayRules() {}
    @Override public String getGameName() { return "Unlucky9"; }
    @Override public void updateBalance(double amt) { balance += amt; }
}
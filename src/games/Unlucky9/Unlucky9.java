package games.Unlucky9;

import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import games.Game;

public class Unlucky9 extends Game {

    private Unlucky9UI ui;
    private Unlucky9Logic logic;
    private PlayerDatabase playerDatabase;
    private boolean exitGame;

    // ===== NEW NO-ARG CONSTRUCTOR =====
    public Unlucky9() {
        super(null); // player will be set in startGame()
        this.ui = new Unlucky9UI();
        this.logic = new Unlucky9Logic();
        this.playerDatabase = null; // will be set in startGame()
    }

    public Unlucky9(Player player, PlayerDatabase playerDatabase) {
        super(player);
        this.ui = new Unlucky9UI();
        this.logic = new Unlucky9Logic();
        this.playerDatabase = playerDatabase;
    }

    // ===== MAIN GAME LOOP =====
   @Override
    public void startGame(Player currentPlayer, PlayerDatabase playerDatabase) {
        this.player = currentPlayer;
        this.playerDatabase = playerDatabase;
        this.exitGame = false;

        player.updateGamesPlayed();
        playerDatabase.updatePlayer(player);

        while (!exitGame) {
            playRound();
        }
    }

    // ===== GAME LOGIC =====
    private void resolveRound(Player player, double bet, int playerValue, int dealerValue, PlayerDatabase playerDatabase) {
        double payout = logic.resolvePayout(bet, playerValue, dealerValue);

        ui.loadingAnimation("Calculating result", 14, 160);

        if (payout > 0) {
            player.setBalance(player.getBalance() + payout);
            Transaction.log(player.getUsername(), player.getPlayerId(), "Unlucky9", "WIN", payout, player.getBalance());
            playerDatabase.updatePlayer(player);
            ui.boxedMessage("YOU WON " + Formatter.formatCurrency(payout));
        } else if (payout == 0) {
            player.setBalance(player.getBalance() + bet);
            Transaction.log(player.getUsername(), player.getPlayerId(), "Unlucky9", "WIN", bet, player.getBalance());
            playerDatabase.updatePlayer(player);
            ui.boxedMessage("PUSH — BET RETURNED");
        } else {
            ui.boxedMessage("YOU LOST " + Formatter.formatCurrency(bet));
        }

        ui.displayNewBalance(player.getBalance());
    }

    @Override
    public void playRound() {
        try {
            ConsoleDisplay.clearConsole();

            ui.printTop();
            ui.printLine("UNLUCKY 9");
            ui.printMid();
            ui.printLine("Player: " + player.getUsername());
            ui.printLine("Balance: " + Formatter.formatCurrency(player.getBalance()));
            ui.printBot();

            System.out.println();

            ui.printTop();
            ui.printLine("1. PLAY");
            ui.printLine("2. TUTORIAL");
            ui.printLine("3. EXIT GAME");
            ui.printBot();

            int choice = ui.boxedIntInput("Choose (1-3): ", 1, 3);

            if (choice == 1) {

                if (player.getBalance() <= 0) {
                    ui.boxedMessage("NO FUNDS AVAILABLE");
                    ui.waitForInput("Press Enter...");
                    return;
                }

                double bet = ui.boxedDoubleInput(
                        "Enter bet amount:",
                        1,
                        player.getBalance());

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
                    player.setBalance(player.getBalance() + (bet * 3));
                    Transaction.log(player.getUsername(), player.getPlayerId(), "Unlucky9", "WIN", bet * 3, player.getBalance());
                    playerDatabase.updatePlayer(player);
                    ui.boxedMessage("JACKPOT! WON " + Formatter.formatCurrency(bet * 3));
                    ui.waitForInput("Press Enter...");
                    return;
                }

                boolean drawMore = ui.boxedYesNoInput("Draw 3rd card? (Y/N)");

                if (drawMore) {
                    int card = logic.drawSingle();
                    playerCards = logic.appendCard(playerCards, card);
                    ui.boxedMessage("You drew: [" + card + "]");
                    ui.loadingAnimation("Processing", 10, 140);
                }

                player.setBalance(player.getBalance() - bet);
                Transaction.log(player.getUsername(), player.getPlayerId(), "Unlucky9", "BET_PLACED", -bet, player.getBalance());
                playerDatabase.updatePlayer(player);

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

                resolveRound(player, bet, playerValue, dealerValue, playerDatabase);

                ui.waitForInput("Press Enter...");

            } else if (choice == 2) {
                displayRules();
            } else if (choice == 3) {
                exitGame = true;
            }
        } catch (Exception e) {
            ConsoleDisplay.clearConsole();
            ui.printTop();
            ui.printLine("ERROR: " + e.getMessage());
            ui.printLine("The game encountered an unexpected error.");
            ui.printBot();
            ui.waitForInput("Press Enter to return...");
        }
    }

    @Override
    public double calculatePayout() { return 0; }

    @Override
    public void displayRules() {
        ConsoleDisplay.clearConsole();
        ui.printTop();
        ui.printLine("UNLUKY 9 - RULES");
        ui.printMid();
        ui.printLine("> Cards are digits 1-9");
        ui.printLine("> Hand value = sum % 10");
        ui.printLine("> Closest to 9 wins");
        ui.printLine("> Exact 9 pays 3x");
        ui.printBot();
        ui.waitForInput("Press Enter...");
    }

    @Override
    public String getGameName() { return "Unlucky9"; }

    @Override
    public void updateBalance(double amt) { balance += amt; }
}

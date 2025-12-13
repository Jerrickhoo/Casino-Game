package games.Blackjack;

import games.Game;
import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import ui.AnimationDisplay;

public class BlackJack extends Game {
    private double balance;
    private double lastRoundPayout;
    private Deck deck;
    private static final int RESHUFFLE_THRESHOLD = 75;
    private static final int BOX_WIDTH = 56; // unify box widths

    public BlackJack(double startingBalance) {
        this.balance = startingBalance;
        this.lastRoundPayout = 0;
        this.deck = new Deck();
        this.deck.shuffle();
    }

    public BlackJack() {
        this(100.0);
    }

    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        this.player = player;
        if (player != null)
            this.balance = player.getBalance();

        showWelcomeScreen();
        displayRules();

        printCentered("");
        printMenuBox(new String[] { "[1] Continue to Game", "[2] Return to Casino" });
        printCentered("");
        int menu = readChoice(1, 2);
        if (menu == 2)
            return;

        // Game loop - play while balance >= 1.0
        while (balance >= 1.0) {
            checkDeckPenetration();
            clearAndHeader();

            printCentered("Balance: " + Formatter.formatCurrency(balance));
            double bet = promptBet();
            if (bet <= 0)
                break; // Invalid bet, exit to casino

            playRoundWithBet(bet);

            if (balance < 1.0) {
                printCenteredBox("YOU'RE OUT OF CHIPS - RETURNING TO CASINO");
                waitForEnterAndClear();
                break;
            }

            // After each round, ask play again or return
            printCentered("");
            printMenuBox(new String[] { "[1] Play another hand", "[2] Return to Casino" });
            printCentered("");
            int choice = readChoice(1, 2);
            if (choice == 2)
                break; // User chose to return to casino
        }

        // Update player balance and database before returning
        if (player != null) {
            player.setBalance(balance);
            if (playerDB != null) {
                playerDB.updatePlayer(player);
                Transaction.log(player.getUsername(), player.getPlayerId(), getGameName(), "PLAY_SESSION_END",
                        balance, balance);
            }
        }
    }

    @Override
    public void playRound() {
        double bet = promptBet();
        if (bet > 0)
            playRoundWithBet(bet);
    }

    @Override
    public double calculatePayout() {
        return lastRoundPayout;
    }

    @Override
    public void displayRules() {
        ConsoleDisplay.clearConsole();
        printCentered("");

        String[] lines = new String[] {
                "       BLACKJACK RULES       ",
                "",
                "OBJECTIVE: Beat the dealer without going over 21",
                "",
                "RULES:",
                "  * Face cards = 10; Aces = 1 or 11",
                "  * Blackjack pays 3:2 (1.5x)",
                "  * Dealer hits on soft 17",
                "  * Double allowed on first decision",
                "",
                "HOW TO PLAY:",
                "  1. Place your bet",
                "  2. You and dealer each get 2 cards",
                "  3. Hit, Stand, or Double your bet",
                "  4. Dealer plays automatically",
                "  5. Highest hand wins (no busting)",
                ""
        };
        printBoxLines(lines);

        printCentered("");
        waitForEnterAndClear();
    }

    @Override
    public String getGameName() {
        return "Blackjack";
    }

    @Override
    public void updateBalance(double amount) {
        this.balance += amount;
        if (this.player != null)
            this.player.setBalance(this.balance);
    }

    private void playRoundWithBet(double bet) {
        lastRoundPayout = 0;
        clearAndHeader();
        Formatter.showProgressBar(centerText("Shuffling & Dealing...", getConsoleWidth()), 500);

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.add(deck.draw());
        dealer.add(deck.draw());
        player.add(deck.draw());
        dealer.add(deck.draw());

        player.lastBet = bet;

        displayInitialHands(dealer, player, bet);

        if (handleBlackjacks(player, dealer, bet))
            return;

        boolean playerAlive = playPlayerHand(player, dealer);
        if (!playerAlive) {
            balance -= player.lastBet;
            lastRoundPayout = -player.lastBet;
            printCentered(formatMessage("LOSS: ", "You busted! -" + Formatter.formatCurrency(player.lastBet)));
            waitForEnterAndClear();
            return;
        }

        playDealerHand(dealer, player);

        resolveHand(player, dealer);
        waitForEnterAndClear();
    }

    private boolean handleBlackjacks(Hand player, Hand dealer, double bet) {
        if (dealer.isBlackjack() || player.isBlackjack()) {
            // Show both hands when a blackjack check occurs
            clearAndHeader();
            printCentered("");
            displayHandBox(dealer, player, true);
            printCentered("");
            if (dealer.isBlackjack() && player.isBlackjack()) {
                printCentered(formatMessage("NOTICE: ", "Push (both blackjack). Bet returned."));
                return true;
            } else if (player.isBlackjack()) {
                double win = bet * 1.5;
                balance += win;
                lastRoundPayout = win;
                printCentered(formatMessage("WIN: ", "BLACKJACK! +" + Formatter.formatCurrency(win)));
                return true;
            } else {
                printCentered(formatMessage("LOSS: ", "Dealer has blackjack. You lose."));
                balance -= bet;
                lastRoundPayout = -bet;
                return true;
            }
        }
        return false;
    }

    private boolean playPlayerHand(Hand hand, Hand dealer) {
        boolean firstDecision = true;

        while (true) {
            clearAndHeader();
            printCentered("");
            displayHandBox(dealer, hand, false);
            printCentered("");

            boolean canDouble = firstDecision && hand.getCardCount() == 2 && balance >= hand.lastBet;

            if (firstDecision) {
                String hit = "[1] Hit";
                String stand = "[2] Stand";
                String dbl = canDouble ? "[3] Double" : null;
                if (dbl != null)
                    printMenuBox(new String[] { hit, stand, dbl });
                else
                    printMenuBox(new String[] { hit, stand });

                int max = canDouble ? 3 : 2;
                int choice = readChoice(1, max);

                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    printCentered(formatMessage("-> ", "You draw: " + drawn));
                    ConsoleDisplay.pause(1500);
                    if (hand.getValue() > 21)
                        return false;
                } else if (choice == 2) {
                    return true;
                } else {
                    // Double down
                    balance -= hand.lastBet;
                    hand.lastBet *= 2;
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    printCentered(formatMessage("-> ", "You double and draw: " + drawn));
                    ConsoleDisplay.pause(2000);
                    return hand.getValue() <= 21;
                }
            } else {
                printMenuBox(new String[] { "[1] Hit", "[2] Stand" });
                int choice = readChoice(1, 2);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    printCentered(formatMessage("-> ", "You draw: " + drawn));
                    ConsoleDisplay.pause(1500);
                    if (hand.getValue() > 21)
                        return false;
                } else {
                    return true;
                }
            }
            firstDecision = false;
        }
    }

    private void playDealerHand(Hand dealer, Hand player) {
        AnimationDisplay.showLoadingAnimation(centerText("Dealer is revealing...", getConsoleWidth()), 800);
        printCentered("");
        displayHandBox(dealer, player, true);
        printCentered("");

        while (true) {
            ConsoleDisplay.pause(1300);
            int value = dealer.getValue();
            boolean isSoft = dealer.isSoftHand();
            if (value < 17 || (value == 17 && isSoft)) {
                Card card = deck.draw();
                dealer.add(card);
                clearAndHeader();
                printCentered("");
                displayHandBox(dealer, player, true);
                printCentered("");
                printCentered(formatMessage("-> ", "Dealer draws: " + card));
                ConsoleDisplay.pause(1500);
            } else
                break;
        }
        clearAndHeader();
        printCentered("");
        displayHandBox(dealer, player, true);
        printCentered("");
        printCentered(formatMessage(
                "-> ",
                "Dealer final: " + dealer + " (" + dealer.getValue() + (dealer.isSoftHand() ? " soft" : "") + ")"));
        ConsoleDisplay.pause(1200);
    }

    private void resolveHand(Hand player, Hand dealer) {
        double bet = player.lastBet;
        int playerValue = player.getValue();
        int dealerValue = dealer.getValue();

        printCentered("");

        if (dealer.isBlackjack() && player.isBlackjack()) {
            printCentered(formatMessage("NOTICE: ", "Push (both blackjack). Bet returned."));
            return;
        }
        if (player.isBlackjack()) {
            double win = bet * 1.5;
            balance += win;
            lastRoundPayout = win;
            printCentered(formatMessage("WIN: ", "BLACKJACK! +" + Formatter.formatCurrency(win)));
            return;
        }
        if (playerValue > 21) {
            balance -= bet;
            lastRoundPayout = -bet;
            printCentered(formatMessage("LOSS: ", "You busted -" + Formatter.formatCurrency(bet)));
            return;
        }
        if (dealerValue > 21) {
            balance += bet;
            lastRoundPayout = bet;
            printCentered(formatMessage("WIN: ", "Dealer busted! +" + Formatter.formatCurrency(bet)));
            return;
        }
        if (playerValue > dealerValue) {
            balance += bet;
            lastRoundPayout = bet;
            printCentered(formatMessage("WIN: ", "You win +" + Formatter.formatCurrency(bet)));
        } else if (playerValue == dealerValue) {
            lastRoundPayout = 0;
            printCentered(formatMessage("NOTICE: ", "Push. Bet returned."));
        } else {
            balance -= bet;
            lastRoundPayout = -bet;
            printCentered(formatMessage("LOSS: ", "You lose -" + Formatter.formatCurrency(bet)));
        }
    }

    private void checkDeckPenetration() {
        int remaining = deck.getCardsRemaining();
        int total = deck.getTotalCards();
        if (remaining < total * (100 - RESHUFFLE_THRESHOLD) / 100) {
            deck = new Deck();
            deck.shuffle();
            printCenteredBox("Deck reshuffled");
            ConsoleDisplay.pause(1200);
        }
    }

    private double promptBet() {
        double min = 1.0;
        if (balance < min) {
            printCentered(formatMessage("LOSS: ", "Insufficient funds."));
            return -1;
        }
        printCenteredBox("Place your bet (min " + Formatter.formatCurrency(min) + ")");
        return InputValidator.readDouble(min, balance);
    }

    private void displayInitialHands(Hand dealer, Hand player, double bet) {
        clearAndHeader();
        printCentered("Bet: " + Formatter.formatCurrency(bet));
        printCentered("");
        displayHandBox(dealer, player, false);
        printCentered("");
    }

    private void displayHandBox(Hand dealer, Hand player, boolean revealDealer) {
        int totalWidth = BOX_WIDTH + 2; // top line: corner + BOX_WIDTH '─' + corner
        String padding = getLeftPadForTotalWidth(totalWidth);
        System.out.println(padding + "┌" + "─".repeat(BOX_WIDTH) + "┐");
        String dealerVal = dealer.getValue() + (dealer.isSoftHand() ? " (soft)" : "");
        String dealerDisplayRaw = revealDealer ? dealer.toString() + "  (" + dealerVal + ")" : dealer.showFirstCard();
        String dealerDisplay = String.format("%-" + (BOX_WIDTH - 10) + "s", dealerDisplayRaw);
        System.out.println(padding + "│ Dealer: " + dealerDisplay + "│");
        if (player != null) {
            System.out.println(padding + "├" + "─".repeat(BOX_WIDTH) + "┤");
            String playerStr = player + "  (" + player.getValue() + (player.isSoftHand() ? " soft" : "") + ")";
            System.out.println(padding + "│ Player: " + String.format("%-" + (BOX_WIDTH - 10) + "s", playerStr) + "│");
        }
        System.out.println(padding + "└" + "─".repeat(BOX_WIDTH) + "┘");
    }

    private void clearAndHeader() {
        ConsoleDisplay.clearConsole();
        printHeader();
    }

    private void printHeader() {
        int width = BOX_WIDTH + 2;
        int totalWidth = width + 2; // account for corners
        String padding = getLeftPadForTotalWidth(totalWidth);
        String border = "═".repeat(width);
        System.out.println("");
        System.out.println(padding + "╔" + border + "╗");
        String title = "BLACKJACK";
        int innerWidth = width - 2; // pad inside
        int leftPad = Math.max(0, (innerWidth - title.length()) / 2);
        String titleLine = "║" + " ".repeat(leftPad) + title
                + " ".repeat(Math.max(0, innerWidth - leftPad - title.length())) + "║";
        System.out.println(padding + titleLine);
        System.out.println(padding + "╚" + border + "╝");
        System.out.println("");
    }

    private void showWelcomeScreen() {
        ConsoleDisplay.clearConsole();
        printCentered("");

        String[] lines = new String[] {
                "       WELCOME TO BLACKJACK       ",
                "",
                "Enjoy the classic card game!",
                ""
        };
        printBoxLines(lines);

        printCentered("");
        waitForEnterAndClear();
    }

    private String formatMessage(String prefix, String msg) {
        return prefix + msg;
    }

    private void printMenuBox(String[] options) {
        printBoxLines(options);
    }

    private void printCenteredBox(String text) {
        printBoxLines(new String[] { text });
    }

    private void printBoxLines(String[] lines) {
        int inner = 0;
        for (String line : lines)
            if (line != null)
                inner = Math.max(inner, line.length());
        int contentWidth = Math.min(70, Math.max(BOX_WIDTH, inner));
        String top = "╔" + "═".repeat(contentWidth + 2) + "╗";
        String bottom = "╚" + "═".repeat(contentWidth + 2) + "╝";
        int totalWidth = contentWidth + 4; // corner + inner + corners
        String padding = getLeftPadForTotalWidth(totalWidth);
        System.out.println(padding + top);
        for (String line : lines) {
            if (line == null)
                line = "";
            int pad = contentWidth - line.length();
            String padded = " " + line + " ".repeat(Math.max(0, pad + 1));
            System.out.println(padding + "║" + padded + "║");
        }
        System.out.println(padding + bottom);
    }

    private void waitForEnterAndClear() {
        InputValidator.waitForUserInput(centerText("Press Enter...", getConsoleWidth()));
        clearAndHeader();
    }

    // Suppress question marks and show boxed prompt instead
    private int readChoice(int min, int max) {
        printChoiceBox("Enter choice (" + min + "-" + max + ")");
        return InputValidator.readInt(min, max);
    }

    private void printChoiceBox(String text) {
        printBoxLines(new String[] { text });
    }

    private int getConsoleWidth() {
        String envColumns = System.getenv("COLUMNS");
        try {
            if (envColumns != null)
                return Integer.parseInt(envColumns);
        } catch (Exception ignored) {
        }
        return 80;
    }

    private String centerText(String s, int width) {
        if (s == null)
            return "";
        int pad = Math.max(0, (width - s.length()) / 2);
        return " ".repeat(pad) + s;
    }

    private void printCentered(String s) {
        System.out.println(centerText(s, getConsoleWidth()));
    }

    private String getLeftPadForTotalWidth(int totalWidth) {
        int pad = Math.max(0, (getConsoleWidth() - totalWidth) / 2);
        return " ".repeat(pad);
    }

    // Rely on the project-level Card, Deck, and Hand classes in
    // src/games/Blackjack/
}
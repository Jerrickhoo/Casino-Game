package games.TwentyWon;

import games.Game;
import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import ui.AnimationDisplay;

public class TwentyWon extends Game {
    private double balance;
    private double lastRoundPayout;
    private Deck deck;
    private static final int RESHUFFLE_THRESHOLD = 75;
    private static final int BOX_WIDTH = 70; // wider box widths to match CasinoMain centering

    // Fixed left margin to visually match CasinoMain's spacing
    private static final String LEFT_MARGIN = "                                                ";

    public TwentyWon(double startingBalance) {
        this.balance = startingBalance;
        this.lastRoundPayout = 0;
        this.deck = new Deck();
        this.deck.shuffle();
    }

    public TwentyWon() {
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
        printMenuBox(new String[] { "[1] Continue to Game", "[2] Return to The House" });
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
                printCenteredBox("YOU'RE OUT OF CHIPS - RETURNING TO THE HOUSE");
                waitForEnterAndClear();
                break;
            }

            // After each round, ask play again or return
            printCentered("");
            printMenuBox(new String[] { "[1] Play another hand", "[2] Return to The House" });
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
                "       TWENTY WON! RULES       ",
                "",
                "OBJECTIVE: Beat the dealer without going over 21",
                "",
                "RULES:",
                "  * Face cards = 10; Aces = 1 or 11",
                "  * TWENTY WON! pays 3:2 (1.5x)",
                "  * Dealer hits on soft 17",
                "  * Double allowed on first decision",
                "",
                "PLAY OPTIONS:",
                "  Hit    - Take another card. You may hit repeatedly until you stand or bust.",
                "  Stand  - End your turn; dealer will then play their hand.",
                "  Double - Only allowed on your first decision when you have exactly 2 cards.",
                "           You double your wager, receive exactly one card, and then automatically stand.",
                "",
        };
        printBoxLines(lines);

        printCentered("");
        waitForEnterAndClear();
    }

    @Override
    public String getGameName() {
        return "TwentyWon";
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
                printCentered(formatMessage("NOTICE: ", "Push (both TWENTY WON!). Bet returned."));
                return true;
            } else if (player.isBlackjack()) {
                double win = bet * 1.5;
                balance += win;
                lastRoundPayout = win;
                printCentered(formatMessage("WIN: ", "TWENTY WON! +" + Formatter.formatCurrency(win)));
                return true;
            } else {
                printCentered(formatMessage("LOSS: ", "Dealer has TWENTY WON!. You lose."));
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
                String doubleOption = canDouble ? "[3] Double" : null;
                if (doubleOption != null)
                    printMenuBox(new String[] { hit, stand, doubleOption });
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
            printCentered(formatMessage("NOTICE: ", "Push (both TWENTY WON!). Bet returned."));
            return;
        }
        if (player.isBlackjack()) {
            double win = bet * 1.5;
            balance += win;
            lastRoundPayout = win;
            printCentered(formatMessage("WIN: ", "TWENTY WON! +" + Formatter.formatCurrency(win)));
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
        // Verify player can afford the minimum bet
        if (player != null && !player.canAfford(min)) {
            printCenteredBox("ERROR: You cannot afford the minimum bet! Try again.");
            return -1;
        }
        double bet = boxedReadDouble(min, balance, "Place your bet (min " + Formatter.formatCurrency(min) + ")");
        // Double-check that player can afford the bet they chose
        if (player != null && !player.canAfford(bet)) {
            printCenteredBox("ERROR: You cannot afford that bet! Try again.");
            return -1;
        }
        return bet;
    }

    private void displayInitialHands(Hand dealer, Hand player, double bet) {
        clearAndHeader();
        printCentered("Bet: " + Formatter.formatCurrency(bet));
        printCentered("");
        displayHandBox(dealer, player, false);
        printCentered("");
    }

    private void displayHandBox(Hand dealer, Hand player, boolean revealDealer) {
        String dealerVal = dealer.getValue() + (dealer.isSoftHand() ? " (soft)" : "");
        String dealerDisplayRaw = revealDealer ? dealer.toString() + "  (" + dealerVal + ")" : dealer.showFirstCard();
        String dealerLine = "Dealer: " + dealerDisplayRaw;

        String playerLine = "";
        if (player != null) {
            String playerStr = player + "  (" + player.getValue() + (player.isSoftHand() ? " soft" : "") + ")";
            playerLine = "Player: " + playerStr;
        }

        int maxTextLen = Math.max(dealerLine.length(), playerLine.length());
        int consoleMaxInner = Math.max(1, getConsoleWidth() - 8);
        int inner = Math.max(BOX_WIDTH, maxTextLen);
        inner = Math.min(inner, consoleMaxInner);
        inner = Math.max(inner, 1);

        // Truncate lines that would overflow
        if (dealerLine.length() > inner) {
            dealerLine = dealerLine.substring(0, inner);
        }
        if (playerLine.length() > inner) {
            playerLine = playerLine.substring(0, inner);
        }

        String top = "╔" + "═".repeat(inner + 2) + "╗";
        String separator = "╟" + "─".repeat(inner + 2) + "╢";
        String bottom = "╚" + "═".repeat(inner + 2) + "╝";

        int totalWidth = inner + 4;
        String padding = getLeftPadForTotalWidth(totalWidth);

        System.out.println(padding + top);
        String dealerPadded = " " + dealerLine + " ".repeat(Math.max(0, inner - dealerLine.length() + 1));
        System.out.println(padding + "║" + dealerPadded + "║");

        if (player != null) {
            System.out.println(padding + separator);
            String playerPadded = " " + playerLine + " ".repeat(Math.max(0, inner - playerLine.length() + 1));
            System.out.println(padding + "║" + playerPadded + "║");
        }

        System.out.println(padding + bottom);
    }

    private void clearAndHeader() {
        ConsoleDisplay.clearConsole();
        printHeader();
    }

    private void printHeader() {
        int innerWidth = Math.max(BOX_WIDTH, 10);
        innerWidth = Math.min(innerWidth, getConsoleWidth() - 10);
        int width = innerWidth + 2;
        int totalWidth = width + 2; // account for corners
        String padding = getLeftPadForTotalWidth(totalWidth);
        String border = "═".repeat(Math.max(0, width));
        System.out.println("");
        System.out.println(padding + "╔" + border + "╗");
        String title = "TWENTY WON!";
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
                "       WELCOME TO TWENTY WON!       ",
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
        int contentWidth = Math.max(BOX_WIDTH, inner);
        contentWidth = Math.min(contentWidth, Math.max(1, getConsoleWidth() - 8)); // clamp to console width with min 1
        String top = "╔" + "═".repeat(contentWidth + 2) + "╗";
        String bottom = "╚" + "═".repeat(contentWidth + 2) + "╝";
        int totalWidth = contentWidth + 4; // corner + inner + corners
        String padding = getLeftPadForTotalWidth(totalWidth);
        System.out.println(padding + top);
        for (String line : lines) {
            if (line == null)
                line = "";
            String display = line;
            if (display.length() > contentWidth)
                display = display.substring(0, contentWidth);
            int paddingWidth = contentWidth - display.length();
            String paddedLine = " " + display + " ".repeat(Math.max(0, paddingWidth + 1));
            System.out.println(padding + "║" + paddedLine + "║");
        }
        System.out.println(padding + bottom);
    }

    // Use centered boxed prompts where the user types so the cursor appears inside
    // the box
    private void waitForEnterAndClear() {
        boxedWaitForEnter("Press Enter...");
        clearAndHeader();
    }

    // Suppress question marks and show boxed prompt instead
    private int readChoice(int min, int max) {
        return boxedReadChoice(min, max, "Enter choice (" + min + "-" + max + ")");
    }

    private void printChoiceBox(String text) {
        printInputBoxPrompt(text);
    }

    /**
     * Print a centered single-line boxed prompt for input (used for choices, bet
     * prompt, and Press Enter)
     */
    private void printInputBoxPrompt(String text) {
        int inner = Math.max(BOX_WIDTH, text.length());
        inner = Math.min(inner, Math.max(1, getConsoleWidth() - 8));
        String top = "╔" + "═".repeat(inner + 2) + "╗";
        String bottom = "╚" + "═".repeat(inner + 2) + "╝";
        int totalWidth = inner + 4;
        String padding = getLeftPadForTotalWidth(totalWidth);
        System.out.println(padding + top);
        String display = text.length() > inner ? text.substring(0, inner) : text;
        String padded = " " + display + " ".repeat(Math.max(0, inner - display.length() + 1));
        System.out.println(padding + "║" + padded + "║");
        System.out.println(padding + bottom);
    }

    /**
     * Read an int while keeping the box visible and showing validation errors
     * centered.
     * Uses InputValidator.readString() and parses to control error messages and
     * centering.
     */
    private int boxedReadChoice(int min, int max, String boxText) {
        while (true) {
            int inner = Math.max(BOX_WIDTH, boxText.length());
            inner = Math.min(inner, Math.max(1, getConsoleWidth() - 8));
            String top = "╔" + "═".repeat(inner + 2) + "╗";
            String bottom = "╚" + "═".repeat(inner + 2) + "╝";
            int totalWidth = inner + 4;
            String padding = getLeftPadForTotalWidth(totalWidth);

            System.out.println(padding + top);
            String display = boxText.length() > inner ? boxText.substring(0, inner) : boxText;
            String padded = " " + display + " ".repeat(Math.max(0, inner - display.length() + 1));
            System.out.println(padding + "║" + padded + "║");

            // print prompt area and read input (cursor will be inside the box)
            int promptPos = Math.max(0, (inner - 4) / 2);
            System.out.print(padding + "║" + " ".repeat(promptPos) + "> ");
            System.out.flush();

            String input = InputValidator.readString();

            // Draw a properly aligned interior row (both side borders) then the bottom
            System.out.println(padding + "║" + " ".repeat(inner + 2) + "║");
            System.out.println(padding + bottom);

            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                } else {
                    printCenteredBox("ERROR: Enter a number between " + min + " and " + max + ". Try again.");
                    ConsoleDisplay.pause(900);
                }
            } catch (NumberFormatException e) {
                printCenteredBox("ERROR: Invalid number. Try again.");
                ConsoleDisplay.pause(900);
            }
        }
    }

    /**
     * Read a double (for bets) while keeping the box visible and showing validation
     * errors centered.
     */
    private double boxedReadDouble(double min, double max, String boxText) {
        while (true) {
            int inner = Math.max(BOX_WIDTH, boxText.length());
            inner = Math.min(inner, Math.max(1, getConsoleWidth() - 8));
            String top = "╔" + "═".repeat(inner + 2) + "╗";
            String bottom = "╚" + "═".repeat(inner + 2) + "╝";
            int totalWidth = inner + 4;
            String padding = getLeftPadForTotalWidth(totalWidth);

            System.out.println(padding + top);
            String display = boxText.length() > inner ? boxText.substring(0, inner) : boxText;
            String padded = " " + display + " ".repeat(Math.max(0, inner - display.length() + 1));
            System.out.println(padding + "║" + padded + "║");

            // print prompt area and read input (cursor will be inside the box)
            int promptPos = Math.max(0, (inner - 12) / 2);
            System.out.print(padding + "║" + " ".repeat(promptPos));
            System.out.flush();

            String input = InputValidator.readString();

            // Draw a properly aligned interior row (both side borders) then the bottom
            System.out.println(padding + "║" + " ".repeat(inner + 2) + "║");
            System.out.println(padding + bottom);

            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                } else {
                    printCenteredBox("ERROR: Enter a number between " + Formatter.formatCurrency(min) + " and "
                            + Formatter.formatCurrency(max) + ". Try again.");
                    ConsoleDisplay.pause(900);
                }
            } catch (NumberFormatException e) {
                printCenteredBox("ERROR: Invalid number. Try again.");
                ConsoleDisplay.pause(900);
            }
        }
    }

    /**
     * Wait for Enter while keeping the box visible and the cursor positioned inside
     * it
     */
    private void boxedWaitForEnter(String boxText) {
        int inner = Math.max(BOX_WIDTH, boxText.length());
        inner = Math.min(inner, Math.max(1, getConsoleWidth() - 8));
        String top = "╔" + "═".repeat(inner + 2) + "╗";
        String bottom = "╚" + "═".repeat(inner + 2) + "╝";
        int totalWidth = inner + 4;
        String padding = getLeftPadForTotalWidth(totalWidth);

        System.out.println(padding + top);
        String display = boxText.length() > inner ? boxText.substring(0, inner) : boxText;
        String padded = " " + display + " ".repeat(Math.max(0, inner - display.length() + 1));
        System.out.println(padding + "║" + padded + "║");

        int promptPos = Math.max(0, (inner - 2) / 2);
        System.out.print(padding + "║" + " ".repeat(promptPos));
        System.out.flush();

        InputValidator.waitForUserInput("");
        // Print a full interior row so the side borders are aligned and box is closed
        System.out.println(padding + "║" + " ".repeat(inner + 2) + "║");
        System.out.println(padding + bottom);
    }

    private int getConsoleWidth() {
        String environmentColumns = System.getenv("COLUMNS");
        try {
            if (environmentColumns != null)
                return Integer.parseInt(environmentColumns);
        } catch (Exception ignored) {
        }
        return 80;
    }

    private String centerText(String s, int width) {
        if (s == null)
            return "";
        // Simpler: always prefix the fixed left margin so output visually matches
        // CasinoMain
        return LEFT_MARGIN + s;
    }

    private void printCentered(String s) {
        System.out.println(centerText(s, getConsoleWidth()));
    }

    private String getLeftPadForTotalWidth(int totalWidth) {
        int basePad = Math.max(0, (getConsoleWidth() - totalWidth) / 2);
        int pad = Math.min(basePad, Math.max(0, getConsoleWidth() - totalWidth));
        return LEFT_MARGIN + " ".repeat(pad);
    }

    // Rely on the project-level Card, Deck, and Hand classes in
    // src/games/Blackjack/
}
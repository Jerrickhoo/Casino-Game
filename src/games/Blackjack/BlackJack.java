package games.Blackjack;

import games.Game;
import Core.Player;
import Core.PlayerDatabase;
import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import ui.AnimationDisplay;

public class BlackJack extends Game {
    private double balance;
    private double lastRoundPayout;
    private Deck deck;
    private static final int RESHUFFLE_THRESHOLD = 75; // Reshuffle when 75% through deck

    public BlackJack(double startingBalance) {
        this.balance = startingBalance;
        this.lastRoundPayout = 0;
        this.deck = new Deck();
        this.deck.shuffle();
    }

    public BlackJack() {
        super();
        this.lastRoundPayout = 0;
        this.deck = new Deck();
        this.deck.shuffle();
    }

    // ===== ABSTRACT GAME IMPLEMENTATION =====

    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        this.player = player;
        if (player != null) {
            this.balance = player.getBalance();
        }

        showWelcomeScreen();

        System.out.println();
        System.out.println("                    ╔════════════════════════════════════════════════════════╗");
        System.out.println("                    ║              [1] Continue to Game                      ║");
        System.out.println("                    ║              [2] Return to Casino Menu                 ║");
        System.out.println("                    ╚════════════════════════════════════════════════════════╝");
        System.out.print("                    Choose: ");
        int menuChoice = InputValidator.readInt(1, 2);

        if (menuChoice == 2) {
            return;
        }

        ConsoleDisplay.pause(700);

        // Main game loop
        while (balance > 0) {
            ConsoleDisplay.clearConsole();
            showGameHeader();

            System.out.println("                    Current Balance: " + Formatter.formatCurrency(balance));
            double bet = getBetFromPlayer();
            if (bet <= 0)
                break;

            playRound(bet);

            if (balance <= 0) {
                ConsoleDisplay.clearConsole();
                System.out.println("\n                    ╔════════════════════════════════════════╗");
                System.out.println("                    ║     YOU'RE OUT OF CHIPS! GAME OVER      ║");
                System.out.println("                    ╚════════════════════════════════════════╝");
                InputValidator.waitForUserInput("                    \nPress Enter to return to casino...");
                break;
            }

            if (!askPlayAgain()) {
                break;
            }
        }

        // Update player and database
        if (player != null) {
            player.setBalance(balance);
            player.updateGamesPlayed();
            if (playerDB != null) {
                playerDB.updatePlayer(player);
                playerDB.logTransaction(player.getUsername(), getGameName(), "PLAY_SESSION_END", balance, balance);
            }
        }
    }

    @Override
    public void playRound() {
        // Single round triggered from outside (legacy support)
        if (balance > 0) {
            double bet = getBetFromPlayer();
            if (bet > 0) {
                playRound(bet);
            }
        }
    }

    @Override
    public double calculatePayout() {
        return lastRoundPayout;
    }

    @Override
    public void displayRules() {
        showWelcomeScreen();
    }

    @Override
    public String getGameName() {
        return "Blackjack";
    }

    @Override
    public void updateBalance(double amount) {
        this.balance += amount;
        if (this.player != null) {
            this.player.setBalance(this.balance);
        }
    }

    // ===== PRIVATE HELPER METHODS =====

    /**
     * Get valid bet from player with proper validation
     */
    private double getBetFromPlayer() {
        double minBet = 1.0;
        double maxBet = balance;

        if (balance < minBet) {
            System.out.println("                    Insufficient balance to play. Minimum bet: $" + minBet);
            return -1;
        }

        System.out.print("                    Place your bet ($" + minBet + " - $" + Formatter.formatCurrency(maxBet)
                + "): ");
        return InputValidator.readDouble(minBet, maxBet);
    }

    /**
     * Ask if player wants to play again
     */
    private boolean askPlayAgain() {
        System.out.println();
        System.out.println("                    ╔════════════════════════════════════════╗");
        System.out.println("                    ║  [1] Play another hand                 ║");
        System.out.println("                    ║  [2] Return to Casino                  ║");
        System.out.println("                    ╚════════════════════════════════════════╝");
        System.out.print("                    Choose: ");
        int choice = InputValidator.readInt(1, 2);
        return choice == 1;
    }

    /**
     * Check and reshuffle deck if needed (Shuffle Penetration)
     */
    private void checkDeckPenetration() {
        int cardsRemaining = deck.getCardsRemaining();
        int totalCards = deck.getTotalCards();
        if (cardsRemaining < (totalCards * RESHUFFLE_THRESHOLD / 100)) {
            ConsoleDisplay.clearConsole();
            System.out.println("                    Reshuffling deck...");
            deck = new Deck();
            deck.shuffle();
            ConsoleDisplay.pause(800);
        }
    }

    /**
     * Main round logic - handles single hand and split logic
     */
    private void playRound(double bet) {
        lastRoundPayout = 0; // Reset payout
        checkDeckPenetration();

        ConsoleDisplay.clearConsole();
        Formatter.showProgressBar("                    Dealing cards", 500);

        Hand playerHand = new Hand();
        Hand dealerHand = new Hand();

        // Initial deal
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());

        // Display initial hands
        displayInitialHands(dealerHand, playerHand, bet);

        // Check for blackjacks
        if (handleBlackjacks(playerHand, dealerHand, bet)) {
            return;
        }

        // Check for split opportunity
        if (playerHand.canSplit() && balance >= bet * 2) {
            if (offerSplit()) {
                handleSplitRound(dealerHand, playerHand, bet);
                return;
            }
        }

        // Normal single hand play
        double finalBet = bet;
        playerHand.setActive(true);

        if (!playPlayerHand(playerHand, dealerHand, finalBet)) {
            // Player busted
            balance -= finalBet;
            lastRoundPayout = -finalBet;
            System.out.println("                      ✗ YOU BUSTED");
            InputValidator.waitForUserInput("                    Press Enter to continue...");
            return;
        }

        // Dealer plays
        playDealerHand(dealerHand);

        // Resolve
        resolveSingleHand(playerHand, dealerHand, finalBet);
        InputValidator.waitForUserInput("                    Press Enter to continue...");
    }

    /**
     * Display initial hands
     */
    private void displayInitialHands(Hand dealer, Hand player, double bet) {
        ConsoleDisplay.clearConsole();
        showGameHeader();
        System.out.println("                    Bet: " + Formatter.formatCurrency(bet));
        System.out.println("                    ═══════════════════════════════════════════");
        System.out.println("                    ┌────────────────────────────────────────┐");
        System.out.println("                    │          DEALER'S HAND                 │");
        System.out.println("                    └────────────────────────────────────────┘");
        System.out.println("                      " + dealer.showFirstCard());
        System.out.println();
        System.out.println("                    ┌────────────────────────────────────────┐");
        System.out.println("                    │          YOUR HAND                     │");
        System.out.println("                    └────────────────────────────────────────┘");
        System.out.println("                      " + player + "  Value: " + player.getValue() + " "
                + (player.isSoftHand() ? "(Soft)" : "(Hard)"));
    }

    /**
     * Handle blackjack scenarios
     */
    private boolean handleBlackjacks(Hand player, Hand dealer, double bet) {
        boolean playerBJ = player.isBlackjack();
        boolean dealerBJ = dealer.isBlackjack();

        if (!playerBJ && !dealerBJ) {
            return false;
        }

        ConsoleDisplay.pause(600);
        revealHands(dealer, player);

        if (playerBJ && dealerBJ) {
            System.out.println("                    \n  = Both have Blackjack - PUSH");
            System.out.println("                      Bet returned: " + Formatter.formatCurrency(bet));
            lastRoundPayout = 0;
        } else if (playerBJ) {
            double payout = bet * 1.5;
            balance += payout;
            System.out.println("                    \n  ✓ BLACKJACK! YOU WIN!");
            System.out.println("                      Winnings: " + Formatter.formatCurrency(payout));
            lastRoundPayout = payout;
            showWinAnimation();
        } else {
            balance -= bet;
            System.out.println("                    \n  ✗ Dealer has Blackjack - YOU LOSE");
            System.out.println("                      Amount lost: " + Formatter.formatCurrency(bet));
            lastRoundPayout = -bet;
        }

        InputValidator.waitForUserInput("                    Press Enter to continue...");
        return true;
    }

    /**
     * Offer split to player
     */
    private boolean offerSplit() {
        System.out.println("                    ");
        System.out.println("                    ┌────────────────────────────────────────┐");
        System.out.println("                    │  [1] SPLIT         [2] NO SPLIT       │");
        System.out.println("                    └────────────────────────────────────────┘");
        System.out.print("                    Choose: ");
        return InputValidator.readInt(1, 2) == 1;
    }

    /**
     * Handle split round - special logic for split aces
     */
    private void handleSplitRound(Hand dealer, Hand originalPlayer, double bet) {
        // Deduct the split bet upfront
        balance -= bet;

        boolean splitAces = originalPlayer.getCard(0).getRank().equals("A");

        Card card0 = originalPlayer.getCard(0);
        Card card1 = originalPlayer.getCard(1);

        Hand hand1 = new Hand();
        Hand hand2 = new Hand();

        hand1.add(card0);
        hand2.add(card1);

        // Draw one card to each split hand
        hand1.add(deck.draw());
        hand2.add(deck.draw());

        System.out.println("                    Split created. Playing Hand 1 then Hand 2.");
        System.out.println("                    Split bet deducted: " + Formatter.formatCurrency(bet));
        ConsoleDisplay.pause(800);

        // Play each split hand (special handling for aces)
        if (splitAces) {
            // Split aces: only 1 card each, no further hits
            playSplitAceHand(hand1, dealer, bet, "Hand 1");
            playSplitAceHand(hand2, dealer, bet, "Hand 2");
        } else {
            // Normal split: can hit/double/stand
            playSplitHand(hand1, dealer, bet, "Hand 1");
            playSplitHand(hand2, dealer, bet, "Hand 2");
        }

        // Dealer plays once
        AnimationDisplay.showLoadingAnimation("                    Dealer is thinking", 1000);
        playDealerHand(dealer);

        // Resolve both hands
        System.out.println("\n                    ═══════════════════════════════════════════");
        System.out.println("                    Hand 1: " + hand1 + " (" + hand1.getValue() + ")");
        double hand1Result = resolveSplitHand(hand1, dealer, bet);

        System.out.println("                    Hand 2: " + hand2 + " (" + hand2.getValue() + ")");
        double hand2Result = resolveSplitHand(hand2, dealer, bet);

        lastRoundPayout = hand1Result + hand2Result;
        InputValidator.waitForUserInput("                    Press Enter to continue...");
    }

    /**
     * Play split ace hand (1 card each, no double/hit)
     */
    private void playSplitAceHand(Hand hand, Hand dealer, double bet, String label) {
        ConsoleDisplay.clearConsole();
        showGameHeader();
        System.out.println("                    " + label + " (Split Ace) | Bet: " + Formatter.formatCurrency(bet));
        System.out.println("                    ═══════════════════════════════════════════");
        System.out.println("                    Dealer: " + dealer.showFirstCard());
        System.out.println("                    " + label + ": " + hand + "  (" + hand.getValue() + ")");
        System.out.println("                    (Ace splits - only 1 card, no further action)");
        ConsoleDisplay.pause(1200);
    }

    /**
     * Play normal split hand (can hit/double/stand)
     */
    private void playSplitHand(Hand hand, Hand dealer, double bet, String label) {
        double handBet = bet;
        boolean firstDecision = true;

        while (true) {
            ConsoleDisplay.clearConsole();
            showGameHeader();
            System.out.println("                    " + label + " | Bet: " + Formatter.formatCurrency(handBet));
            System.out.println("                    ═══════════════════════════════════════════");
            System.out.println("                    Dealer: " + dealer.showFirstCard());
            System.out.println("                    " + label + ": " + hand + "  (" + hand.getValue() + " "
                    + (hand.isSoftHand() ? "Soft" : "Hard") + ")");
            System.out.println();

            if (firstDecision) {
                // Can double on any first decision with sufficient balance
                boolean canDouble = balance >= bet;

                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │  [1] HIT  [2] STAND"
                        + (canDouble ? "  [3] DOUBLE      │" : "              │"));
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = InputValidator.readInt(1, canDouble ? 3 : 2);

                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST! " + hand.getValue());
                        return;
                    }
                } else if (choice == 2) {
                    break;
                } else if (choice == 3) { // Double
                    if (balance >= bet) {
                        balance -= bet; // Deduct double bet immediately
                        handBet *= 2;
                        Card drawn = deck.draw();
                        hand.add(drawn);
                        System.out.println("                      You doubled and drew: " + drawn);
                        ConsoleDisplay.pause(700);
                        if (hand.getValue() > 21) {
                            System.out.println("                      BUST after double! " + hand.getValue());
                        }
                        break; // Double ends turn
                    } else {
                        System.out.println("                      Insufficient funds to double!");
                        ConsoleDisplay.pause(700);
                        continue;
                    }
                }
            } else {
                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │      [1] HIT           [2] STAND      │");
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = InputValidator.readInt(1, 2);

                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST! " + hand.getValue());
                        return;
                    }
                } else {
                    break;
                }
            }
            firstDecision = false;
        }
    }

    /**
     * Play player hand (single hand - NOT split)
     * Returns true if player didn't bust, false if busted
     */
    private boolean playPlayerHand(Hand hand, Hand dealer, double bet) {
        boolean firstDecision = true;

        while (true) {
            ConsoleDisplay.clearConsole();
            showGameHeader();
            System.out.println("                    Bet: " + Formatter.formatCurrency(bet));
            System.out.println("                    ═══════════════════════════════════════════");
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │          DEALER'S HAND                 │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.println("                      " + dealer.showFirstCard());
            System.out.println();
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │          YOUR HAND                     │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.println("                      " + hand + "  Value: " + hand.getValue() + " "
                    + (hand.isSoftHand() ? "(Soft)" : "(Hard)"));
            System.out.println();

            if (firstDecision) {
                // Check if double-down is allowed (any 2-card hand with sufficient balance)
                boolean canDouble = hand.getCardCount() == 2 && balance >= bet;

                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │  [1] HIT  [2] STAND"
                        + (canDouble ? "  [3] DOUBLE      │" : "              │"));
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");

                int choice = InputValidator.readInt(1, canDouble ? 3 : 2);

                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST! " + hand.getValue());
                        return false; // Player busted
                    }
                } else if (choice == 2) {
                    break;
                } else if (choice == 3) { // Double
                    balance -= bet; // Deduct double bet immediately
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You doubled and drew: " + drawn);
                    ConsoleDisplay.pause(700);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST after double! " + hand.getValue());
                        return false;
                    }
                    break; // Double ends turn
                }
            } else {
                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │      [1] HIT           [2] STAND      │");
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = InputValidator.readInt(1, 2);

                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST! " + hand.getValue());
                        return false;
                    }
                } else {
                    break;
                }
            }
            firstDecision = false;
        }

        return true; // Player didn't bust
    }

    /**
     * Play dealer hand (Soft 17 rule: Dealer hits on soft 17, stands on hard 17+)
     */
    private void playDealerHand(Hand dealer) {
        AnimationDisplay.showLoadingAnimation("                    Dealer is revealing", 1000);
        revealHands(dealer, null);

        while (true) {
            ConsoleDisplay.pause(600);
            int value = dealer.getValue();
            boolean isSoft = dealer.isSoftHand();

            // Dealer must hit on 16 or less, or on soft 17 (Soft 17 rule)
            if (value < 17 || (value == 17 && isSoft)) {
                Card c = deck.draw();
                System.out.println("                      Dealer draws: " + c);
                dealer.add(c);
            } else {
                break; // Dealer stands
            }
        }

        System.out.println("                      Dealer final: " + dealer + "  (" + dealer.getValue() + " "
                + (dealer.isSoftHand() ? "Soft" : "Hard") + ")");
    }

    /**
     * Resolve a single hand outcome
     */
    private void resolveSingleHand(Hand playerHand, Hand dealer, double bet) {
        int pVal = playerHand.getValue();
        int dVal = dealer.getValue();

        if (pVal > 21) {
            balance -= bet;
            lastRoundPayout -= bet;
            System.out.println("                      ✗ Hand busted. Lost " + Formatter.formatCurrency(bet));
        } else if (dVal > 21) {
            balance += bet;
            lastRoundPayout += bet;
            System.out.println("                      ✓ Dealer busted. Win " + Formatter.formatCurrency(bet));
            showWinAnimation();
        } else if (pVal > dVal) {
            balance += bet;
            lastRoundPayout += bet;
            System.out.println("                      ✓ Hand wins! " + Formatter.formatCurrency(bet));
            showWinAnimation();
        } else if (pVal == dVal) {
            System.out.println("                      = Push. Bet returned: " + Formatter.formatCurrency(bet));
        } else {
            balance -= bet;
            lastRoundPayout -= bet;
            System.out.println("                      ✗ Dealer wins. Lost " + Formatter.formatCurrency(bet));
        }

        System.out.println("                      Balance: " + Formatter.formatCurrency(balance));
    }

    /**
     * Resolve a split hand outcome and return the payout
     */
    private double resolveSplitHand(Hand playerHand, Hand dealer, double bet) {
        int pVal = playerHand.getValue();
        int dVal = dealer.getValue();
        double payout = 0;

        if (pVal > 21) {
            payout = -bet;
            System.out.println("                      ✗ Hand busted. Lost " + Formatter.formatCurrency(bet));
        } else if (dVal > 21) {
            payout = bet;
            balance += bet;
            System.out.println("                      ✓ Dealer busted. Win " + Formatter.formatCurrency(bet));
            showWinAnimation();
        } else if (pVal > dVal) {
            payout = bet;
            balance += bet;
            System.out.println("                      ✓ Hand wins! " + Formatter.formatCurrency(bet));
            showWinAnimation();
        } else if (pVal == dVal) {
            payout = 0;
            System.out.println("                      = Push. Bet returned: " + Formatter.formatCurrency(bet));
        } else {
            payout = -bet;
            System.out.println("                      ✗ Dealer wins. Lost " + Formatter.formatCurrency(bet));
        }

        System.out.println("                      Balance: " + Formatter.formatCurrency(balance));
        return payout;
    }

    private void revealHands(Hand dealer, Hand player) {
        System.out.println("                    ┌────────────────────────────────────────┐");
        System.out.println("                    │          DEALER'S HAND                 │");
        System.out.println("                    └────────────────────────────────────────┘");
        System.out.println("                      " + dealer + "  (" + dealer.getValue() + " "
                + (dealer.isSoftHand() ? "Soft" : "Hard") + ")");
        if (player != null) {
            System.out.println();
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │          YOUR HAND                     │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.println("                      " + player + "  (" + player.getValue() + " "
                    + (player.isSoftHand() ? "Soft" : "Hard") + ")");
        }
    }

    private void showGameHeader() {
        System.out.println();
        System.out.println("                    ╔════════════════════════════════════════════════════════╗");
        System.out.println("                    ║                 ♠ BLACKJACK GAME ♠                    ║");
        System.out.println("                    ║              ♥ ♦ ♣ ♠ ♥ ♦ ♣ ♠ ♥ ♦ ♣                ║");
        System.out.println("                    ╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void showWelcomeScreen() {
        System.out.println("\n\n");
        System.out.println("                    ╔════════════════════════════════════════════════════════╗");
        System.out.println("                    ║            WELCOME TO BLACKJACK!                      ║");
        System.out.println("                    ╚════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("                    ┌────────────────────────────────────────────────────────┐");
        System.out.println("                    │                    OBJECTIVE:                          │");
        System.out.println("                    │  Beat the dealer without going over 21 points         │");
        System.out.println("                    └────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("                    ┌────────────────────────────────────────────────────────┐");
        System.out.println("                    │                     GAME RULES:                        │");
        System.out.println("                    │                                                        │");
        System.out.println("                    │  • Card Values: 2-10 = face value, J/Q/K = 10         │");
        System.out.println("                    │  • Ace = 1 or 11 (automatic best value)               │");
        System.out.println("                    │  • Blackjack (21 on 2 cards) = 1.5x payout           │");
        System.out.println("                    │  • Bust (over 21) = Automatic loss                    │");
        System.out.println("                    │  • Dealer hits on 16 or less                          │");
        System.out.println("                    │  • Dealer hits on SOFT 17 (A+6)                       │");
        System.out.println("                    │  • Double-down allowed on ANY hand value              │");
        System.out.println("                    │  • Split aces = 1 card each (no further action)       │");
        System.out.println("                    └────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("                    ┌────────────────────────────────────────────────────────┐");
        System.out.println("                    │                    YOUR OPTIONS:                       │");
        System.out.println("                    │                                                        │");
        System.out.println("                    │  [1] HIT - Draw another card                          │");
        System.out.println("                    │  [2] STAND - Keep your hand and end your turn         │");
        System.out.println("                    │  [3] DOUBLE - Double bet on any hand value            │");
        System.out.println("                    │  [4] SPLIT - Split equal cards (costs 2x bet)         │");
        System.out.println("                    └────────────────────────────────────────────────────────┘");
        System.out.println();
        InputValidator.waitForUserInput();
    }

    private void showWinAnimation() {
        System.out.println("                      ♠ ♥ ♦ ♣ ♠ ♥ ♦ ♣ ♠ ♥ ♦ ♣");
    }
}
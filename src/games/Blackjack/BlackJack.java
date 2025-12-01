package games.Blackjack;

import games.Game;
import Core.Player;
import Core.PlayerDatabase;
import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import ui.AnimationDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlackJack extends Game {
    private double balance;

    public BlackJack(double startingBalance) {
        this.balance = startingBalance;
    }

    public BlackJack() {
        super();
    }

    public double start() {
        ConsoleDisplay.clearConsole();
        showWelcomeScreen();

        System.out.println();
        System.out.println("                    ╔════════════════════════════════════════════════════════╗");
        System.out.println("                    ║              [1] Continue to Game                      ║");
        System.out.println("                    ║              [2] Return to Casino Menu                 ║");
        System.out.println("                    ╚════════════════════════════════════════════════════════╝");
        System.out.print("                    Choose: ");
        int menuChoice = InputValidator.readInt(1, 2);

        if (menuChoice == 2) {
            return balance;
        }

        ConsoleDisplay.pause(700);

        while (balance > 0) {
            ConsoleDisplay.clearConsole();
            showGameHeader();

            System.out.println("                    Current Balance: " + Formatter.formatCurrency(balance));
            System.out.print(
                    "                    Place your bet (min $1, max " + Formatter.formatCurrency(balance) + "): ");
            double bet = InputValidator.readDouble(1, balance);

            playRound(bet);

            if (balance <= 0) {
                ConsoleDisplay.clearConsole();
                System.out.println("\n                    ╔════════════════════════════════════════╗");
                System.out.println("                    ║     YOU'RE OUT OF CHIPS! GAME OVER      ║");
                System.out.println("                    ╚════════════════════════════════════════╝");
                InputValidator.waitForUserInput("                    \nPress Enter to return to casino...");
                break;
            }

            System.out.println();
            System.out.println("                    ╔════════════════════════════════════════╗");
            System.out.println("                    ║  [1] Play another hand                 ║");
            System.out.println("                    ║  [2] Return to Casino                  ║");
            System.out.println("                    ╚════════════════════════════════════════╝");
            System.out.print("                    Choose: ");
            int choice = InputValidator.readInt(1, 2);
            if (choice == 2)
                break;
        }

        return balance;
    }

    // -- Implement abstract Game methods --

    @Override
    public void startGame(Player player, PlayerDatabase playerDB) {
        this.player = player;
        if (player != null) {
            this.balance = player.getBalance();
        }

        double finalBalance = start();

        if (player != null) {
            player.setBalance(finalBalance);
            player.updateGamesPlayed();
            if (playerDB != null) {
                playerDB.updatePlayer(player);
                playerDB.logTransaction(player.getUsername(), getGameName(), "PLAY_SESSION_END", finalBalance,
                        finalBalance);
            }
        }
    }

    @Override
    public void playRound() {
        // Ask for a bet and play a round using existing method
        double bet = InputValidator.readDouble(1, this.balance);
        playRound(bet);
    }

    @Override
    public double calculatePayout() {
        // Not tracked per-round here; return current balance
        return this.balance;
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

    private void playRound(double bet) {
        ConsoleDisplay.clearConsole();
        Formatter.showProgressBar("                    Shuffling deck", 500);

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        // initial deal
        player.add(deck.draw());
        dealer.add(deck.draw());
        player.add(deck.draw());
        dealer.add(deck.draw());

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
        System.out.println("                      " + player + "  Value: " + player.getValue());

        // Check for naturals
        boolean playerBJ = player.isBlackjack();
        boolean dealerBJ = dealer.isBlackjack();

        if (playerBJ || dealerBJ) {
            revealHands(dealer, player);
            if (playerBJ && dealerBJ) {
                System.out.println("                    \n  ✓ Both have Blackjack - PUSH");
                System.out.println("                      Bet returned: " + Formatter.formatCurrency(bet));
            } else if (playerBJ) {
                double payout = bet * 1.5;
                balance += payout;
                System.out.println("                    \n  ✓ BLACKJACK! YOU WIN!");
                System.out.println("                      Winnings: " + Formatter.formatCurrency(payout));
                showWinAnimation();
            } else {
                balance -= bet;
                System.out.println("                    \n  ✗ Dealer has Blackjack - YOU LOSE");
                System.out.println("                      Amount lost: " + Formatter.formatCurrency(bet));
            }
            InputValidator.waitForUserInput("                    Press Enter to continue...");
            return;
        }

        // Check for split option
        boolean performedSplit = false;
        Hand handA = null, handB = null;
        if (player.canSplit() && balance >= bet * 2) {
            System.out.println("                    ");
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │  [1] SPLIT         [2] NO SPLIT       │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.print("                    Choose: ");
            int splitChoice = InputValidator.readInt(1, 2);
            if (splitChoice == 1) {
                performedSplit = true;
                Card c0 = player.cards.get(0);
                Card c1 = player.cards.get(1);
                handA = new Hand();
                handB = new Hand();
                handA.add(c0);
                handB.add(c1);
                handA.add(deck.draw());
                handB.add(deck.draw());
                System.out.println("                    Split created. Playing Hand A then Hand B.");
                ConsoleDisplay.pause(800);
            }
        }

        if (performedSplit) {
            // Play Hand A
            playPlayerHand(deck, dealer, handA, bet, "Hand A");
            // Play Hand B
            playPlayerHand(deck, dealer, handB, bet, "Hand B");

            // Dealer plays once
            AnimationDisplay.showLoadingAnimation("                    Dealer is thinking", 1000);
            revealHands(dealer, null);
            while (dealer.getValue() < 17) {
                ConsoleDisplay.pause(600);
                Card c = deck.draw();
                System.out.println("                      Dealer draws: " + c);
                dealer.add(c);
            }
            System.out.println("                      Dealer final: " + dealer + "  (" + dealer.getValue() + ")");

            // Resolve both hands
            resolveHandOutcome(handA, dealer, bet);
            resolveHandOutcome(handB, dealer, bet);
            InputValidator.waitForUserInput("                    Press Enter to continue...");
            return;
        }

        // Player turn (single hand with double option)
        boolean playerBusted = false;
        double currentBet = bet;
        boolean firstDecision = true;

        while (true) {
            ConsoleDisplay.clearConsole();
            showGameHeader();
            System.out.println("                    Bet: " + Formatter.formatCurrency(currentBet));
            System.out.println("                    ═══════════════════════════════════════════");
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │          DEALER'S HAND                 │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.println("                      " + dealer.showFirstCard());
            System.out.println();
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │          YOUR HAND                     │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.println("                      " + player + "  Value: " + player.getValue());
            System.out.println();

            if (firstDecision) {
                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │  [1] HIT  [2] STAND  [3] DOUBLE      │");
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = InputValidator.readInt(1, 3);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    player.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (player.getValue() > 21) {
                        System.out.println("                      You: " + player + "  (" + player.getValue() + ")");
                        System.out.println("                      You busted!");
                        playerBusted = true;
                        break;
                    }
                } else if (choice == 2) {
                    break;
                } else { // Double
                    if (balance < currentBet * 2) {
                        System.out.println("                      Insufficient funds to double!");
                        ConsoleDisplay.pause(700);
                        continue;
                    }
                    currentBet *= 2;
                    Card drawn = deck.draw();
                    player.add(drawn);
                    System.out.println("                      You doubled and drew: " + drawn);
                    ConsoleDisplay.pause(700);
                    if (player.getValue() > 21) {
                        System.out.println("                      You: " + player + "  (" + player.getValue() + ")");
                        System.out.println("                      You busted!");
                        playerBusted = true;
                    }
                    break;
                }
            } else {
                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │      [1] HIT           [2] STAND      │");
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = InputValidator.readInt(1, 2);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    player.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (player.getValue() > 21) {
                        System.out.println("                      You: " + player + "  (" + player.getValue() + ")");
                        System.out.println("                      You busted!");
                        playerBusted = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            firstDecision = false;
        }

        // Dealer turn
        if (!playerBusted) {
            AnimationDisplay.showLoadingAnimation("                    Dealer is thinking", 1000);
            revealHands(dealer, player);
            while (dealer.getValue() < 17) {
                ConsoleDisplay.pause(600);
                Card c = deck.draw();
                System.out.println("                      Dealer draws: " + c);
                dealer.add(c);
            }
            System.out.println("                      Dealer final: " + dealer + "  (" + dealer.getValue() + ")");
        }

        // Resolve
        int playerVal = player.getValue();
        int dealerVal = dealer.getValue();
        System.out.println("\n                    ═══════════════════════════════════════════");
        if (playerBusted) {
            balance -= currentBet;
            System.out.println("                      ✗ YOU BUSTED - YOU LOSE");
            System.out.println("                        Amount lost: " + Formatter.formatCurrency(currentBet));
        } else if (dealerVal > 21) {
            balance += currentBet;
            System.out.println("                      ✓ DEALER BUSTED - YOU WIN!");
            System.out.println("                        Winnings: " + Formatter.formatCurrency(currentBet));
            showWinAnimation();
        } else if (playerVal > dealerVal) {
            balance += currentBet;
            System.out.println("                      ✓ YOU WIN!");
            System.out.println("                        Winnings: " + Formatter.formatCurrency(currentBet));
            showWinAnimation();
        } else if (playerVal == dealerVal) {
            System.out.println("                      = PUSH - Bet returned");
            System.out.println("                        Amount returned: " + Formatter.formatCurrency(currentBet));
        } else {
            balance -= currentBet;
            System.out.println("                      ✗ DEALER WINS - YOU LOSE");
            System.out.println("                        Amount lost: " + Formatter.formatCurrency(currentBet));
        }
        System.out.println("                    ═══════════════════════════════════════════");
        System.out.println("                      New Balance: " + Formatter.formatCurrency(balance));

        InputValidator.waitForUserInput("                    Press Enter to continue...");
    }

    // Play a single hand with Hit/Stand/Double (for split hands)
    private void playPlayerHand(Deck deck, Hand dealer, Hand hand, double bet, String label) {
        boolean busted = false;
        double handBet = bet;
        boolean firstDecision = true;

        while (true) {
            ConsoleDisplay.clearConsole();
            showGameHeader();
            System.out.println("                    " + label + " | Bet: " + Formatter.formatCurrency(handBet));
            System.out.println("                    ═══════════════════════════════════════════");
            System.out.println("                    Dealer: " + dealer.showFirstCard());
            System.out.println("                    " + label + ": " + hand + "  (" + hand.getValue() + ")");
            System.out.println();

            if (firstDecision) {
                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │  [1] HIT  [2] STAND  [3] DOUBLE      │");
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = InputValidator.readInt(1, 3);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    ConsoleDisplay.pause(600);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST! " + hand.getValue());
                        busted = true;
                        break;
                    }
                } else if (choice == 2) {
                    break;
                } else { // Double
                    if (balance < handBet * 2) {
                        System.out.println("                      Insufficient funds to double!");
                        ConsoleDisplay.pause(700);
                        continue;
                    }
                    handBet *= 2;
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You doubled and drew: " + drawn);
                    ConsoleDisplay.pause(700);
                    if (hand.getValue() > 21) {
                        System.out.println("                      BUST after double! " + hand.getValue());
                        busted = true;
                    }
                    break;
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
                        busted = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            firstDecision = false;
        }

        hand.lastBet = handBet;
        if (busted) {
            /* read variable to avoid unused-value warning */ }
    }

    // Resolve outcome for a single hand
    private void resolveHandOutcome(Hand playerHand, Hand dealer, double defaultBet) {
        double useBet = playerHand.lastBet > 0 ? playerHand.lastBet : defaultBet;
        int pVal = playerHand.getValue();
        int dVal = dealer.getValue();

        if (pVal > 21) {
            balance -= useBet;
            System.out.println("                      ✗ Hand busted. Lost " + Formatter.formatCurrency(useBet));
        } else if (dVal > 21) {
            balance += useBet;
            System.out.println("                      ✓ Dealer busted. Win " + Formatter.formatCurrency(useBet));
            showWinAnimation();
        } else if (pVal > dVal) {
            balance += useBet;
            System.out.println("                      ✓ Hand beats dealer! Win " + Formatter.formatCurrency(useBet));
            showWinAnimation();
        } else if (pVal == dVal) {
            System.out.println("                      = Push. Bet returned: " + Formatter.formatCurrency(useBet));
        } else {
            balance -= useBet;
            System.out.println("                      ✗ Dealer wins. Lost " + Formatter.formatCurrency(useBet));
        }
    }

    private void revealHands(Hand dealer, Hand player) {
        System.out.println("                    ┌────────────────────────────────────────┐");
        System.out.println("                    │          DEALER'S HAND                 │");
        System.out.println("                    └────────────────────────────────────────┘");
        System.out.println("                      " + dealer + "  (" + dealer.getValue() + ")");
        if (player != null) {
            System.out.println();
            System.out.println("                    ┌────────────────────────────────────────┐");
            System.out.println("                    │          YOUR HAND                     │");
            System.out.println("                    └────────────────────────────────────────┘");
            System.out.println("                      " + player + "  (" + player.getValue() + ")");
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
        System.out.println("                    │  • Each card is worth its face value                   │");
        System.out.println("                    │  • Face cards (J, Q, K) = 10 points                   │");
        System.out.println("                    │  • Ace = 1 or 11 points (automatic)                   │");
        System.out.println("                    │  • Blackjack (21 on 2 cards) = 1.5x payout           │");
        System.out.println("                    │  • Bust (over 21) = Automatic loss                    │");
        System.out.println("                    │  • Dealer must HIT on 16 or less                      │");
        System.out.println("                    │  • Dealer must STAND on 17 or more                    │");
        System.out.println("                    └────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("                    ┌────────────────────────────────────────────────────────┐");
        System.out.println("                    │                    YOUR OPTIONS:                       │");
        System.out.println("                    │                                                        │");
        System.out.println("                    │  [1] HIT - Draw another card                          │");
        System.out.println("                    │  [2] STAND - Keep your hand and end your turn         │");
        System.out.println("                    │  [3] DOUBLE - Double your bet and get 1 card          │");
        System.out.println("                    │  [1] SPLIT - Split equal cards (costs 2x bet)         │");
        System.out.println("                    └────────────────────────────────────────────────────────┘");
        System.out.println();
        InputValidator.waitForUserInput();
    }

    private void showWinAnimation() {
        System.out.println("                      ♠ ♥ ♦ ♣ ♠ ♥ ♦ ♣ ♠ ♥ ♦ ♣");
    }

    // Inner classes
    private static class Card {
        final String rank;
        final String suit;

        Card(String rank, String suit) {
            this.rank = rank;
            this.suit = suit;
        }

        int value() {
            if ("J".equals(rank) || "Q".equals(rank) || "K".equals(rank))
                return 10;
            if ("A".equals(rank))
                return 11;
            return Integer.parseInt(rank);
        }

        @Override
        public String toString() {
            return rank + suit;
        }
    }

    private static class Deck {
        private final List<Card> cards = new ArrayList<>();
        private final Random rng = new Random();

        Deck() {
            String[] suits = { "♠", "♥", "♦", "♣" };
            String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
            for (String s : suits) {
                for (String r : ranks) {
                    cards.add(new Card(r, s));
                }
            }
        }

        void shuffle() {
            Collections.shuffle(cards, rng);
        }

        Card draw() {
            if (cards.isEmpty())
                throw new IllegalStateException("Deck empty");
            return cards.remove(cards.size() - 1);
        }
    }

    private static class Hand {
        private final List<Card> cards = new ArrayList<>();
        double lastBet = 0;

        void add(Card c) {
            cards.add(c);
        }

        int getValue() {
            int total = 0;
            int aces = 0;
            for (Card c : cards) {
                total += c.value();
                if ("A".equals(c.rank))
                    aces++;
            }
            while (total > 21 && aces > 0) {
                total -= 10;
                aces--;
            }
            return total;
        }

        boolean isBlackjack() {
            return cards.size() == 2 && getValue() == 21;
        }

        boolean canSplit() {
            if (cards.size() != 2)
                return false;
            return cards.get(0).rank.equals(cards.get(1).rank);
        }

        String showFirstCard() {
            if (cards.isEmpty())
                return "";
            return cards.get(0).toString() + " [?]";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cards.size(); i++) {
                if (i > 0)
                    sb.append(" ");
                sb.append(cards.get(i));
            }
            return sb.toString();
        }
    }
}
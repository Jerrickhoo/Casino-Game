package games.Blackjack;

import utilities.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlackJack {
    private double balance;

    public BlackJack(double startingBalance) {
        this.balance = startingBalance;
    }

    public double start() {
        utilities.clearConsole();
        showWelcomeScreen();
        utilities.pause(1500);

        while (balance > 0) {
            utilities.clearConsole();
            showGameHeader();

            System.out.println("                    Current Balance: " + utilities.formatCurrency(balance));
            System.out.print("                    Place your bet (min $1, max " + utilities.formatCurrency(balance) + "): ");
            double bet = utilities.readDouble(1, balance);

            playRound(bet);

            if (balance <= 0) {
                utilities.clearConsole();
                System.out.println("\n                    ╔════════════════════════════════════════╗");
                System.out.println("                    ║     YOU'RE OUT OF CHIPS! GAME OVER      ║");
                System.out.println("                    ╚════════════════════════════════════════╝");
                utilities.waitForUserInput("                    \nPress Enter to return to casino...");
                break;
            }

            System.out.println();
            System.out.println("                    ╔════════════════════════════════════════╗");
            System.out.println("                    ║  [1] Play another hand                 ║");
            System.out.println("                    ║  [2] Return to Casino                  ║");
            System.out.println("                    ╚════════════════════════════════════════╝");
            System.out.print("                    Choose: ");
            int choice = utilities.readInt(1, 2);
            if (choice == 2) break;
        }

        return balance;
    }

    private void playRound(double bet) {
        utilities.clearConsole();
        utilities.showProgressBar("                    Shuffling deck", 500);

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        // initial deal
        player.add(deck.draw());
        dealer.add(deck.draw());
        player.add(deck.draw());
        dealer.add(deck.draw());

        utilities.clearConsole();
        showGameHeader();
        System.out.println("                    Bet: " + utilities.formatCurrency(bet));
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
                System.out.println("                      Bet returned: " + utilities.formatCurrency(bet));
            } else if (playerBJ) {
                double payout = bet * 1.5;
                balance += payout;
                System.out.println("                    \n  ✓ BLACKJACK! YOU WIN!");
                System.out.println("                      Winnings: " + utilities.formatCurrency(payout));
                showWinAnimation();
            } else {
                balance -= bet;
                System.out.println("                    \n  ✗ Dealer has Blackjack - YOU LOSE");
                System.out.println("                      Amount lost: " + utilities.formatCurrency(bet));
            }
            utilities.waitForUserInput("                    Press Enter to continue...");
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
            int splitChoice = utilities.readInt(1, 2);
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
                utilities.pause(800);
            }
        }

        if (performedSplit) {
            // Play Hand A
            playPlayerHand(deck, dealer, handA, bet, "Hand A");
            // Play Hand B
            playPlayerHand(deck, dealer, handB, bet, "Hand B");
            
            // Dealer plays once
            utilities.showLoadingAnimation("                    Dealer is thinking", 1000);
            revealHands(dealer, null);
            while (dealer.getValue() < 17) {
                utilities.pause(600);
                Card c = deck.draw();
                System.out.println("                      Dealer draws: " + c);
                dealer.add(c);
            }
            System.out.println("                      Dealer final: " + dealer + "  (" + dealer.getValue() + ")");
            
            // Resolve both hands
            resolveHandOutcome(handA, dealer, bet);
            resolveHandOutcome(handB, dealer, bet);
            utilities.waitForUserInput("                    Press Enter to continue...");
            return;
        }

        // Player turn (single hand with double option)
        boolean playerBusted = false;
        double currentBet = bet;
        boolean firstDecision = true;

        while (true) {
            utilities.clearConsole();
            showGameHeader();
            System.out.println("                    Bet: " + utilities.formatCurrency(currentBet));
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
                int choice = utilities.readInt(1, 3);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    player.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    utilities.pause(600);
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
                        utilities.pause(700);
                        continue;
                    }
                    currentBet *= 2;
                    Card drawn = deck.draw();
                    player.add(drawn);
                    System.out.println("                      You doubled and drew: " + drawn);
                    utilities.pause(700);
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
                int choice = utilities.readInt(1, 2);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    player.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    utilities.pause(600);
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
            utilities.showLoadingAnimation("                    Dealer is thinking", 1000);
            revealHands(dealer, player);
            while (dealer.getValue() < 17) {
                utilities.pause(600);
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
            System.out.println("                        Amount lost: " + utilities.formatCurrency(currentBet));
        } else if (dealerVal > 21) {
            balance += currentBet;
            System.out.println("                      ✓ DEALER BUSTED - YOU WIN!");
            System.out.println("                        Winnings: " + utilities.formatCurrency(currentBet));
            showWinAnimation();
        } else if (playerVal > dealerVal) {
            balance += currentBet;
            System.out.println("                      ✓ YOU WIN!");
            System.out.println("                        Winnings: " + utilities.formatCurrency(currentBet));
            showWinAnimation();
        } else if (playerVal == dealerVal) {
            System.out.println("                      = PUSH - Bet returned");
            System.out.println("                        Amount returned: " + utilities.formatCurrency(currentBet));
        } else {
            balance -= currentBet;
            System.out.println("                      ✗ DEALER WINS - YOU LOSE");
            System.out.println("                        Amount lost: " + utilities.formatCurrency(currentBet));
        }
        System.out.println("                    ═══════════════════════════════════════════");
        System.out.println("                      New Balance: " + utilities.formatCurrency(balance));

        utilities.waitForUserInput("                    Press Enter to continue...");
    }

    // Play a single hand with Hit/Stand/Double (for split hands)
    private void playPlayerHand(Deck deck, Hand dealer, Hand hand, double bet, String label) {
        boolean busted = false;
        double handBet = bet;
        boolean firstDecision = true;

        while (true) {
            utilities.clearConsole();
            showGameHeader();
            System.out.println("                    " + label + " | Bet: " + utilities.formatCurrency(handBet));
            System.out.println("                    ═══════════════════════════════════════════");
            System.out.println("                    Dealer: " + dealer.showFirstCard());
            System.out.println("                    " + label + ": " + hand + "  (" + hand.getValue() + ")");
            System.out.println();

            if (firstDecision) {
                System.out.println("                    ┌────────────────────────────────────────┐");
                System.out.println("                    │  [1] HIT  [2] STAND  [3] DOUBLE      │");
                System.out.println("                    └────────────────────────────────────────┘");
                System.out.print("                    Choose: ");
                int choice = utilities.readInt(1, 3);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    utilities.pause(600);
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
                        utilities.pause(700);
                        continue;
                    }
                    handBet *= 2;
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You doubled and drew: " + drawn);
                    utilities.pause(700);
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
                int choice = utilities.readInt(1, 2);
                if (choice == 1) {
                    Card drawn = deck.draw();
                    hand.add(drawn);
                    System.out.println("                      You draw: " + drawn);
                    utilities.pause(600);
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
    }

    // Resolve outcome for a single hand
    private void resolveHandOutcome(Hand playerHand, Hand dealer, double defaultBet) {
        double useBet = playerHand.lastBet > 0 ? playerHand.lastBet : defaultBet;
        int pVal = playerHand.getValue();
        int dVal = dealer.getValue();

        if (pVal > 21) {
            balance -= useBet;
            System.out.println("                      ✗ Hand busted. Lost " + utilities.formatCurrency(useBet));
        } else if (dVal > 21) {
            balance += useBet;
            System.out.println("                      ✓ Dealer busted. Win " + utilities.formatCurrency(useBet));
            showWinAnimation();
        } else if (pVal > dVal) {
            balance += useBet;
            System.out.println("                      ✓ Hand beats dealer! Win " + utilities.formatCurrency(useBet));
            showWinAnimation();
        } else if (pVal == dVal) {
            System.out.println("                      = Push. Bet returned: " + utilities.formatCurrency(useBet));
        } else {
            balance -= useBet;
            System.out.println("                      ✗ Dealer wins. Lost " + utilities.formatCurrency(useBet));
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
        System.out.println("                    ║                                                        ║");
        System.out.println("                    ║            OBJECTIVE:                                  ║");
        System.out.println("                    ║            Beat the dealer without going over 21      ║");
        System.out.println("                    ║                                                        ║");
        System.out.println("                    ║            FEATURES:                                   ║");
        System.out.println("                    ║            • HIT - Draw another card                   ║");
        System.out.println("                    ║            • STAND - End your turn                     ║");
        System.out.println("                    ║            • DOUBLE - Double bet, get 1 card          ║");
        System.out.println("                    ║            • SPLIT - Split equal cards (costs 2x bet) ║");
        System.out.println("                    ║                                                        ║");
        System.out.println("                    ║            • Face cards = 10 points                    ║");
        System.out.println("                    ║            • Ace = 1 or 11 points                      ║");
        System.out.println("                    ║            • Blackjack (21 on 2 cards) = 1.5x payout  ║");
        System.out.println("                    ╚════════════════════════════════════════════════════════╝");
        System.out.println("\n");
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
            if ("J".equals(rank) || "Q".equals(rank) || "K".equals(rank)) return 10;
            if ("A".equals(rank)) return 11;
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
            String[] suits = {"♠", "♥", "♦", "♣"};
            String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
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
            if (cards.isEmpty()) throw new IllegalStateException("Deck empty");
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
                if ("A".equals(c.rank)) aces++;
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
            if (cards.size() != 2) return false;
            return cards.get(0).rank.equals(cards.get(1).rank);
        }

        String showFirstCard() {
            if (cards.isEmpty()) return "";
            return cards.get(0).toString() + " [?]";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cards.size(); i++) {
                if (i > 0) sb.append(" ");
                sb.append(cards.get(i));
            }
            return sb.toString();
        }
    }
}
package games.Unlucky9;

import java.util.Random;
import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import utilities.InputValidator;
import games.Game;

public class Unlucky9 extends Game {

	private final Random random = new Random();

	// ===== UI CONSTANTS =====
	private static final int BOX_WIDTH = 56;
	private static final String LEFT_MARGIN = "                                                ";
	private static final String H_LINE =
			"══════════════════════════════════════════════════════════";

	public Unlucky9() {
		super();
	}

	public static void play(Player currentPlayer, PlayerDatabase playerDB) {
		new Unlucky9().startGame(currentPlayer, playerDB);
	}

	// ===== MAIN GAME LOOP =====
	private void playWithPlayer(Player currentPlayer, PlayerDatabase playerDB) {

		this.player = currentPlayer;
		this.balance = currentPlayer.getBalance();

		while (true) {
			try {
				ConsoleDisplay.clearConsole();

				printTop();
				printLine("UNLUCKY 9");
				printMid();
				printLine("Player: " + currentPlayer.getUsername());
				printLine("Balance: " + Formatter.formatCurrency(balance));
				printBot();

				System.out.println();

				printTop();
				printLine("1. PLAY");
				printLine("2. TUTORIAL");
				printLine("3. EXIT GAME");
				printBot();

				int choice = boxedIntInput("Choose (1-3): ", 1, 3);

				if (choice == 1) {

					if (balance <= 0) {
						boxedMessage("NO FUNDS AVAILABLE");
						waitForInput("Press Enter...");
						continue;
					}

					double bet = boxedDoubleInput(
							"Enter bet amount:",
							1,
							balance
					);

					int[] playerCards = drawHand(2);
					int[] dealerCards = drawHand(2);

					ConsoleDisplay.clearConsole();

					printTop();
					printLine("UNLUCKY 9");
					printMid();
					printLine("Bet Placed: " + Formatter.formatCurrency(bet));
					printBot();

					loadingAnimation("Dealing", 12, 140);
					displayPlayerWithOneDealer(playerCards, dealerCards);

					if (handValue(playerCards) == 9) {
						applyBet(currentPlayer, bet);
						applyWin(currentPlayer, bet * 3, "JACKPOT!");
						waitForInput("Press Enter...");
						continue;
					}

					boolean drawMore = boxedYesNoInput("Draw 3rd card? (Y/N): ");

					if (drawMore) {
						int card = drawSingle();
						playerCards = appendCard(playerCards, card);
						boxedMessage("You drew: [" + card + "]");
						loadingAnimation("Processing", 10, 140);
					}

					applyBet(currentPlayer, bet);

					loadingAnimation("Revealing dealer", 10, 140);

					int playerValue = handValue(playerCards);
					int dealerValue = handValue(dealerCards);

					displayHands(playerCards, dealerCards, playerValue, dealerValue);

					if (dealerValue <= 5) {
						int card = drawSingle();
						dealerCards = appendCard(dealerCards, card);
						dealerValue = handValue(dealerCards);
						loadingAnimation("Dealer drawing", 10, 140);
						displayHands(playerCards, dealerCards, playerValue, dealerValue);
					}

					resolveRound(currentPlayer, bet, playerValue, dealerValue);

					waitForInput("Press Enter...");

				} else if (choice == 2) {

					ConsoleDisplay.clearConsole();
					printTop();
					printLine("UNLUCKY 9 - RULES");
					printMid();
					printLine("> Cards are digits 1-9");
					printLine("> Hand value = sum % 10");
					printLine("> Closest to 9 wins");
					printLine("> Exact 9 pays 3x");
					printBot();

					waitForInput("Press Enter...");

				} else {
					return;
				}
			} catch (Exception e) {
				// Debug: Print error to help identify issues
				ConsoleDisplay.clearConsole();
				printTop();
				printLine("ERROR: " + e.getMessage());
				printLine("The game encountered an unexpected error.");
				printBot();
				waitForInput("Press Enter to return...");
				return;
			}
		}
	}

	// ===== BOXED INPUT METHODS =====
	private int boxedIntInput(String label, int min, int max) {
		printTop();
		printLine(label);
		System.out.print(LEFT_MARGIN + "║ > ");
		int value = InputValidator.readInt(min, max);
		printBot();
		return value;
	}

	private double boxedDoubleInput(String label, double min, double max) {
		printTop();
		printLine(label);
		System.out.print(LEFT_MARGIN + "║ > ");
		double value = InputValidator.readDouble(min, max);
		printBot();
		return value;
	}

	private boolean boxedYesNoInput(String label) {
		printTop();
		printLine(label);
		System.out.print(LEFT_MARGIN + "║ > ");
		boolean value = InputValidator.readYesNo();
		printBot();
		return value;
	}

	private void boxedMessage(String msg) {
		printTop();
		printLine(msg);
		printBot();
	}

	private void waitForInput(String message) {
		InputValidator.waitForUserInput(LEFT_MARGIN + message);
	}

	// ===== GAME LOGIC =====
	private void applyBet(Player p, double bet) {
		balance -= bet;
		p.setBalance(balance);
		Transaction.log(p.getUsername(), p.getPlayerId(),
				"Unlucky9", "BET", bet, balance);
	}

	private void applyWin(Player p, double payout, String msg) {
		balance += payout;
		p.setBalance(balance);
		Transaction.log(p.getUsername(), p.getPlayerId(),
				"Unlucky9", "WIN", payout, balance);
		boxedMessage(msg + " WON " + Formatter.formatCurrency(payout));
	}

	private void resolveRound(Player p, double bet, int pv, int dv) {
		double payout = resolvePayout(bet, pv, dv);

		loadingAnimation("Calculating result", 14, 160);

		if (payout > 0) applyWin(p, payout, "YOU");
		else if (payout == 0) applyWin(p, bet, "PUSH — BET RETURNED");
		else boxedMessage("YOU LOST " + Formatter.formatCurrency(bet));

		System.out.println(LEFT_MARGIN + "New Balance: " + Formatter.formatCurrency(balance));
	}

	private int[] drawHand(int n) {
		int[] cards = new int[n];
		for (int i = 0; i < n; i++)
			cards[i] = drawSingle();
		return cards;
	}

	private int drawSingle() { return random.nextInt(9) + 1; }

	private int handValue(int[] cards) {
		int sum = 0;
		for (int c : cards)
			sum += c;
		return sum % 10;
	}

	private double resolvePayout(double bet, int playerValue, int dealerValue) {
		if (playerValue == 9)
			return bet * 3;
		if (playerValue > dealerValue)
			return bet * 2;
		if (playerValue == dealerValue)
			return 0;
		return -1;
	}

	// ===== DISPLAY =====
	private void displayHands(int[] p, int[] d, int pv, int dv) {
		printTop();
		printLine("PLAYER HAND");
		printLine(formatHand(p) + " => " + pv);
		printMid();
		printLine("DEALER HAND");
		printLine(formatHand(d) + " => " + dv);
		printBot();
	}

	private void displayPlayerWithOneDealer(int[] p, int[] d) {
		printTop();
		printLine("PLAYER HAND");
		printLine(formatHand(p) + " => " + handValue(p));
		printMid();
		printLine("DEALER HAND");
		printLine("[" + d[0] + "] [?]");
		printBot();
	}

	private String formatHand(int[] hand) {
		StringBuilder sb = new StringBuilder();
		for (int v : hand)
			sb.append("[").append(v).append("] ");
		return sb.toString().trim();
	}

	// ===== UI CORE =====
	private void printTop() { System.out.println(LEFT_MARGIN + "╔" + H_LINE + "╗"); }
	private void printMid() { System.out.println(LEFT_MARGIN + "╠" + H_LINE + "╣"); }
	private void printBot() { System.out.println(LEFT_MARGIN + "╚" + H_LINE + "╝"); }

	private void printLine(String text) {
		if (text.length() > BOX_WIDTH) text = text.substring(0, BOX_WIDTH);
		System.out.printf(LEFT_MARGIN + "║ %-"+BOX_WIDTH+"s ║%n", text);
	}

	private void loadingAnimation(String msg, int cycles, int delay) {
		String[] frames = { ".", "..", "...", " ..", "  ." };
		for (int i = 0; i < cycles; i++) {
			System.out.print("\r" + LEFT_MARGIN + msg + frames[i % frames.length]);
			try { Thread.sleep(delay); }
			catch (InterruptedException e) { Thread.currentThread().interrupt(); }
		}
		System.out.println();
	}

	private int[] appendCard(int[] arr, int card) {
		int[] out = new int[arr.length + 1];
		System.arraycopy(arr, 0, out, 0, arr.length);
		out[arr.length] = card;
		return out;
	}

	@Override
	public void startGame(Player p, PlayerDatabase db) {
		playWithPlayer(p, db);
	}

	@Override
	public void playRound() {
	}

	@Override
	public double calculatePayout() {
		return 0;
	}

	@Override
	public void displayRules() {
	}

	@Override
	public String getGameName() {
		return "Unlucky9";
	}

	@Override
	public void updateBalance(double amount) {
		balance += amount;
	}
}

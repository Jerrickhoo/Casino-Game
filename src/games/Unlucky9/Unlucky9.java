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
	private static final int BOX_WIDTH = 58;
	private static final String H_LINE =
			"════════════════════════════════════════════════════════════";

	public Unlucky9() {
		super();
	}

	public Unlucky9(double playerBalance) {
		super();
		this.balance = playerBalance;
	}

	public static void play(Player currentPlayer, PlayerDatabase playerDB) {
		Unlucky9 game = new Unlucky9();
		game.startGame(currentPlayer, playerDB);
	}

	// ===== MAIN GAME LOOP =====
	private void playWithPlayer(Player currentPlayer, PlayerDatabase playerDB) {

		if (currentPlayer != null) {
			this.player = currentPlayer;
			this.balance = currentPlayer.getBalance();
		}

		while (true) {
			ConsoleDisplay.clearConsole();

			printTop();
			printLine("UNLUCKY 9");
			printMid();
			printLine("Player: " + currentPlayer.getUsername());
			printLine("Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
			printBot();

			System.out.println();
			printTop();
			printLine("1. PLAY");
			printLine("2. TUTORIAL");
			printLine("3. EXIT GAME");
			printBot();

			int choice = InputValidator.readInt(1, 3);

			if (choice == 1) {

				if (currentPlayer.getBalance() <= 0) {
					System.out.println("\n❌ No funds available.");
					InputValidator.waitForUserInput("Press Enter...");
					continue;
				}

				System.out.print("\nEnter bet amount: ");
				double bet = InputValidator.readDouble(1, currentPlayer.getBalance());

				if (!currentPlayer.canAfford(bet)) {
					System.out.println("\n❌ You cannot afford that bet.");
					InputValidator.waitForUserInput("Press Enter...");
					continue;
				}

				int[] playerCards = drawHand(2);
				int[] dealerCards = drawHand(2);

				ConsoleDisplay.clearConsole();

				printTop();
				printLine("UNLUCKY 9");
				printMid();
				printLine("Bet Placed: " + Formatter.formatCurrency(bet));
				printBot();

				loadingAnimation("Dealing", 8, 120);
				displayPlayerWithOneDealer(playerCards, dealerCards);

				int shownValue = handValue(playerCards);
				if (shownValue == 9) {
					this.balance -= bet;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(),
							"Unlucky9", "BET", bet, this.balance);

					this.balance += bet * 3;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(),
							"Unlucky9", "WIN", bet * 3, this.balance);

					printTop();
					printLine("JACKPOT! INSTANT 9!");
					printBot();

					InputValidator.waitForUserInput("Press Enter...");
					continue;
				}

				System.out.print("\nDraw 3rd card? (Y/N): ");
				if (InputValidator.readYesNo()) {
					int card = drawSingle();
					playerCards = appendCard(playerCards, card);
					System.out.println("You drew: [" + card + "]");
					loadingAnimation("Processing", 6, 120);
				}

				this.balance -= bet;
				currentPlayer.setBalance(this.balance);
				Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(),
						"Unlucky9", "BET", bet, this.balance);

				loadingAnimation("Revealing dealer", 6, 120);

				int playerValue = handValue(playerCards);
				int dealerValue = handValue(dealerCards);

				displayHands(playerCards, dealerCards, playerValue, dealerValue);

				if (dealerValue <= 5) {
					int dealerCard = drawSingle();
					dealerCards = appendCard(dealerCards, dealerCard);
					dealerValue = handValue(dealerCards);
					loadingAnimation("Dealer drawing", 6, 120);
					displayHands(playerCards, dealerCards, playerValue, dealerValue);
				}

				double payout = resolvePayout(bet, playerValue, dealerValue);

				loadingAnimation("Calculating result", 8, 120);

				if (payout > 0) {
					this.balance += payout;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(),
							"Unlucky9", "WIN", payout, this.balance);

					printTop();
					printLine("YOU WON " + Formatter.formatCurrency(payout));
					printBot();

				} else if (payout == 0) {
					this.balance += bet;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(),
							"Unlucky9", "PUSH", bet, this.balance);

					printTop();
					printLine("PUSH — BET RETURNED");
					printBot();

				} else {
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(),
							"Unlucky9", "LOSS", bet, this.balance);

					printTop();
					printLine("YOU LOST " + Formatter.formatCurrency(bet));
					printBot();
				}

				System.out.println("\nNew Balance: " +
						Formatter.formatCurrency(this.balance));

				currentPlayer.updateGamesPlayed();
				playerDB.updatePlayer(currentPlayer);

				InputValidator.waitForUserInput("Press Enter...");

			} else if (choice == 2) {

				ConsoleDisplay.clearConsole();
				printTop();
				printLine("UNLUCKY 9 - TUTORIAL");
				printMid();
				printLine("• Cards are digits 1–9");
				printLine("• Hand value = sum % 10");
				printLine("• Closest to 9 wins");
				printLine("• Exact 9 pays 3x");
				printBot();

				InputValidator.waitForUserInput("Press Enter...");
			} else {
				return;
			}
		}
	}

	// ===== GAME LOGIC =====
	private int[] drawHand(int n) {
		int[] cards = new int[n];
		for (int i = 0; i < n; i++) cards[i] = drawSingle();
		return cards;
	}

	private int drawSingle() {
		return random.nextInt(9) + 1;
	}

	private int handValue(int[] cards) {
		int sum = 0;
		for (int c : cards) sum += c;
		return sum % 10;
	}

	private double resolvePayout(double bet, int playerValue, int dealerValue) {
		if (playerValue == 9) return bet * 3;
		if (playerValue > dealerValue) return bet * 2;
		if (playerValue == dealerValue) return 0;
		return -1;
	}

	// ===== DISPLAY HELPERS =====
	private void displayHands(int[] player, int[] dealer, int playerValue, int dealerValue) {
		printTop();
		printLine("PLAYER HAND");
		printLine(formatHand(player) + " => " + playerValue);
		printMid();
		printLine("DEALER HAND");
		printLine(formatHand(dealer) + " => " + dealerValue);
		printBot();
	}

	private void displayPlayerWithOneDealer(int[] player, int[] dealer) {
		printTop();
		printLine("PLAYER HAND");
		printLine(formatHand(player) + " => " + handValue(player));
		printMid();
		printLine("DEALER HAND");
		printLine("[" + dealer[0] + "] [?]");
		printBot();
	}

	private String formatHand(int[] hand) {
		StringBuilder sb = new StringBuilder();
		for (int v : hand) sb.append("[").append(v).append("] ");
		return sb.toString().trim();
	}

	// ===== UI CORE =====
	private void printTop() {
		System.out.println("            ╔" + H_LINE + "╗");
	}

	private void printMid() {
		System.out.println("            ╠" + H_LINE + "╣");
	}

	private void printBot() {
		System.out.println("            ╚" + H_LINE + "╝");
	}

	private void printLine(String text) {
		if (text.length() > BOX_WIDTH)
			text = text.substring(0, BOX_WIDTH);
		System.out.printf("            ║ %-"+BOX_WIDTH+"s ║%n", text);
	}

	private void loadingAnimation(String message, int cycles, int delayMs) {
		String[] frames = { ".", "..", "...", " ..", "  .", "   " };
		for (int i = 0; i < cycles; i++) {
			System.out.print("\r" + message + frames[i % frames.length]);
			try { Thread.sleep(delayMs); }
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

	@Override public void startGame(Player p, PlayerDatabase db) {
		playWithPlayer(p, db);
	}

	@Override public void playRound() {}
	@Override public double calculatePayout() { return 0; }
	@Override public void displayRules() {}
	@Override public String getGameName() { return "Unlucky9"; }
	@Override public void updateBalance(double amount) { balance += amount; }
}

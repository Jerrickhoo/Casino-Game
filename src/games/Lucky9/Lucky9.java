package games.Lucky9;

import java.util.Random;
import Core.Player;
import Core.PlayerDatabase;
import utilities.InputValidator;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import games.Game;

/**
 * Lucky9 game
 * - Player places a bet
 * - Player and Dealer each draw 3 digits (1-9)
 * - Hand value = sum of digits % 10 (closest to 9 wins)
 * - Exact 9 on player's hand -> special jackpot multiplier
 *
 * This class provides a static helper `play` that CasinoMain can call,
 * and an instance-style constructor + start() for other uses.
 */
public class Lucky9 extends Game {
	private final Random rng = new Random();

	// Allow instance usage similar to SlotMachine.start()
	private double balance;

	public Lucky9() {
		super();
	}

	public Lucky9(double playerBalance) {
		super();
		this.balance = playerBalance;
	}

	/**
	 * Start loop for instance usage. Returns updated balance when player exits.
	 */
	public double start() {
		while (true) {
			ConsoleDisplay.clearConsole();
			System.out.println("\n\n");
			System.out.println("            ╔══════════════════════════════════════════════════════════╗");
			System.out.println("            ║                        LUCKY 9                          ║");
			System.out.println("            ╠══════════════════════════════════════════════════════════╣");
			System.out.println("            ║                Get as close to 9 as possible!            ║");
			System.out.println("            ╚══════════════════════════════════════════════════════════╝");
			System.out.println("");
			System.out.println("                 Current Balance: " + Formatter.formatCurrency(balance));
			System.out.println("");
			System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
			System.out.println("            ║      1. PLAY         ║ ║    2. EXIT GAME      ║");
			System.out.println("            ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║ ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄   ║");
			System.out.println("            ║   █                 █   ║   █                 █   ║");
			System.out.println("            ║   █    █▀▀▀▀▀▀█    █   ║   █    █▀▀▀▀▀▀█    █   ║");
			System.out.println("            ║   █    █ DEAL █    █   ║   █    █ BACK  █    █   ║");
			System.out.println("            ║   █    █▄▄▄▄▄▄█    █   ║   █    █▄▄▄▄▄▄█    █   ║");
			System.out.println("            ║   █       🎴        █   ║   █      🚪         █   ║");
			System.out.println("            ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█   ║");
			System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
			System.out.print("\n                 Choose (1-2): ");

			int choice = InputValidator.readInt(1, 2);
			if (choice == 1) {
				if (balance <= 0) {
					System.out.println("\n                 ❌ You have no funds to bet. Deposit more to play.");
					InputValidator.waitForUserInput("\n                 Press Enter to continue...");
					continue;
				}

				System.out.print("\n                 Enter bet amount: ");
				double bet = InputValidator.readDouble(1, balance);
				balance -= bet;

				// Clear screen after bet is placed
				ConsoleDisplay.clearConsole();
				System.out.println("\n");
				System.out.println("            ╔══════════════════════════════════════════════════════════╗");
				System.out.println("            ║                        LUCKY 9                          ║");
				System.out.println("            ╠══════════════════════════════════════════════════════════╣");
				System.out.println("            ║               Bet Placed: "
						+ String.format("%-20s", Formatter.formatCurrency(bet)) + "           ║");
				System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				System.out.println("");

				// Enhanced dealing animation
				dealingAnimation();

				int[] playerCards = drawHand();
				int[] dealerCards = drawHand();

				int playerValue = handValue(playerCards);
				int dealerValue = handValue(dealerCards);

				displayHands(playerCards, dealerCards, playerValue, dealerValue);

				double payout = resolvePayout(bet, playerValue, dealerValue, playerCards);

				System.out.println("");
				if (payout > 0) {
					balance += payout;
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              YOU WON "
							+ String.format("%-33s", Formatter.formatCurrency(payout)) + "   ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				} else if (payout == 0 && playerValue == dealerValue) {
					balance += bet;
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              ➖ PUSH — BET RETURNED                      ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				} else {
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║             ❌ YOU LOST "
							+ String.format("%-33s", Formatter.formatCurrency(bet)) + "║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				}

				System.out.println("");
				System.out.println("                 New Balance: " + Formatter.formatCurrency(balance));
				System.out.println("");

				InputValidator.waitForUserInput("                 Press Enter to continue...");
			} else {
				ConsoleDisplay.pause(1000, "Returning to Casino menu...");
				return balance;
			}
		}
	}

	// Static entry point for CasinoMain style usage
	public static void play(Player currentPlayer, PlayerDatabase playerDB) {
		Lucky9 game = new Lucky9();
		game.playWithPlayer(currentPlayer, playerDB);
	}

	// Play loop when provided player + db
	private void playWithPlayer(Player currentPlayer, PlayerDatabase playerDB) {
		while (true) {
			ConsoleDisplay.clearConsole();
			System.out.println("\n\n");
			System.out.println("            ╔══════════════════════════════════════════════════════════╗");
			System.out.println("            ║                        LUCKY 9                           ║");
			System.out.println("            ╠══════════════════════════════════════════════════════════╣");
			System.out.println("            ║               Try to reach 9 (mod 10).                   ║");
			System.out.println("            ╚══════════════════════════════════════════════════════════╝");
			System.out.println("");
			System.out.println("                 Player: " + String.format("%-30s", currentPlayer.getUsername()));
			System.out.println("                 Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
			System.out.println("");
			System.out.println("            ╔══════════════════════╗ ╔══════════════════════╗");
			System.out.println("            ║      1. PLAY         ║ ║    2. EXIT GAME      ║");
			System.out.println("            ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄  ║ ║   ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄  ║");
			System.out.println("            ║   █               █  ║ ║   █               █  ║");
			System.out.println("            ║   █    █▀▀▀▀▀▀█   █  ║ ║   █    █▀▀▀▀▀▀█   █  ║");
			System.out.println("            ║   █    █ DEAL █   █  ║ ║   █    █ BACK █   █  ║");
			System.out.println("            ║   █    █▄▄▄▄▄▄█   █  ║ ║   █    █▄▄▄▄▄▄█   █  ║");
			System.out.println("            ║   █               █  ║ ║   █               █  ║");
			System.out.println("            ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█  ║ ║   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█  ║");
			System.out.println("            ╚══════════════════════╝ ╚══════════════════════╝");
			System.out.print("\n                 Choose (1-2): ");

			int choice = InputValidator.readInt(1, 2);
			if (choice == 1) {
				if (currentPlayer.getBalance() <= 0) {
					System.out.println(
							"\n                 ❌ You have no funds to bet. Win some at other games or register more funds.");
					InputValidator.waitForUserInput("\n                 Press Enter to continue...");
					continue;
				}

				System.out.print("\n                 Enter bet amount: ");
				double bet = InputValidator.readDouble(1, currentPlayer.getBalance());

				// Clear screen after bet is placed
				ConsoleDisplay.clearConsole();
				System.out.println("\n");
				System.out.println("            ╔══════════════════════════════════════════════════════════╗");
				System.out.println("            ║                        LUCKY 9                           ║");
				System.out.println("            ╠══════════════════════════════════════════════════════════╣");
				System.out.println("            ║               Bet Placed: "
						+ String.format("%-20s", Formatter.formatCurrency(bet)) + "          ║");
				System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				System.out.println("");

				// Enhanced dealing animation
				dealingAnimation();

				// Deduct immediately (will be returned on push or add payout on win)
				currentPlayer.setBalance(currentPlayer.getBalance() - bet);
				playerDB.logTransaction(currentPlayer.getUsername(), "Lucky9", "BET", bet, currentPlayer.getBalance());

				int[] playerCards = drawHand();
				int[] dealerCards = drawHand();

				int playerValue = handValue(playerCards);
				int dealerValue = handValue(dealerCards);

				displayHands(playerCards, dealerCards, playerValue, dealerValue);

				double payout = resolvePayout(bet, playerValue, dealerValue, playerCards);

				System.out.println("");
				if (payout > 0) {
					currentPlayer.setBalance(currentPlayer.getBalance() + payout);
					playerDB.logTransaction(currentPlayer.getUsername(), "Lucky9", "WIN", payout,
							currentPlayer.getBalance());
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              YOU WON "
							+ String.format("%-33s", Formatter.formatCurrency(payout)) + "║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				} else if (payout == 0 && playerValue == dealerValue) {
					// push
					currentPlayer.setBalance(currentPlayer.getBalance() + bet);
					playerDB.logTransaction(currentPlayer.getUsername(), "Lucky9", "PUSH", bet,
							currentPlayer.getBalance());
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║                 PUSH — BET RETURNED                      ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				} else {
					playerDB.logTransaction(currentPlayer.getUsername(), "Lucky9", "LOSS", bet,
							currentPlayer.getBalance());
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              YOU LOST "
							+ String.format("%-33s", Formatter.formatCurrency(bet)) + "  ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				}

				System.out.println("");
				System.out.println(
						"                 New Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
				System.out.println("");

				currentPlayer.updateGamesPlayed();
				playerDB.updatePlayer(currentPlayer);

				InputValidator.waitForUserInput("                 Press Enter to continue...");

			} else {
				// Exit to games menu
				return;
			}
		}
	}

	// Draw 3 cards valued 1..9
	private int[] drawHand() {
		int[] cards = new int[3];
		for (int i = 0; i < 3; i++) {
			cards[i] = rng.nextInt(9) + 1; // 1..9
		}
		return cards;
	}

	// Hand value: (sum of cards) % 10
	private int handValue(int[] cards) {
		int sum = 0;
		for (int c : cards)
			sum += c;
		return sum % 10;
	}

	private void dealingAnimation() {
		String[] frames = { "🂠 🂠 🂠", "🃏 🃏 🃏", "🂡 🂡 🂡", "🃏 🃏 🃏", "🂢 🂢 🂢", "🃏 🃏 🃏" };
		System.out.println("");
		System.out.println("                 Dealing your cards...");
		System.out.println("");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.print("\r                            " + frames[j % frames.length] + "        ");
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		System.out.println("\r                                                  ");
		System.out.println("");
	}

	private void displayHands(int[] player, int[] dealer, int pValue, int dValue) {
		System.out.println();
		System.out.println("            ╔════════════════════════════════════════════════════════════╗");
		System.out.print("            ║  Player: ");
		for (int v : player)
			System.out.print("[" + v + "] ");
		System.out.println("  => " + pValue + "                                ║");

		System.out.print("            ║  Dealer : ");
		for (int v : dealer)
			System.out.print("[" + v + "] ");
		System.out.println("  => " + dValue + "                               ║");
		System.out.println("            ╚════════════════════════════════════════════════════════════╝");
	}

	/**
	 * Resolve payout rules:
	 * - If playerValue > dealerValue => win pays 2x (i.e., get bet*2 back: original
	 * bet was already deducted so payout = bet*2)
	 * - If equal => push (return bet)
	 * - If playerValue < dealerValue => lose (no payout)
	 * - Special: if player's handValue == 9 => jackpot pays 3x (payout = bet * 3)
	 */
	private double resolvePayout(double bet, int playerValue, int dealerValue, int[] playerCards) {
		// Special exact 9 (jackpot) — only if player's value is 9 (regardless of
		// dealer)
		if (playerValue == 9) {
			// Big payout
			return bet * 3.0;
		}

		if (playerValue > dealerValue) {
			// Win: return bet + winnings (1x) -> payout = bet * 2
			return bet * 2.0;
		}

		if (playerValue == dealerValue) {
			// push
			return 0.0;
		}

		// loss
		return -1.0; // indicate loss
	}

	// --- Implement abstract Game methods (lightweight wrappers) ---
	@Override
	public void startGame(Player player, PlayerDatabase playerDB) {
		// Use the existing playWithPlayer flow when a player + DB are provided
		playWithPlayer(player, playerDB);
	}

	@Override
	public void playRound() {
		// Not used directly by CasinoMain; rounds are played inside
		// start()/playWithPlayer()
	}

	@Override
	public double calculatePayout() {
		// Not applicable as a single-call; returns 0 as placeholder
		return 0;
	}

	@Override
	public void displayRules() {
		ConsoleDisplay.clearConsole();
		System.out.println("Lucky9 Rules: Draw 3 digits (1-9). Closest to 9 wins. Exact 9 => jackpot x3.");
	}

	@Override
	public String getGameName() {
		return "Lucky9";
	}

	@Override
	public void updateBalance(double amount) {
		this.balance += amount;
		if (this.player != null) {
			this.player.setBalance(this.balance);
		}
	}

}

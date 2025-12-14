package games.Unlucky9;

import java.util.Random;
import Core.Player;
import Core.PlayerDatabase;
import Core.Transaction;
import utilities.ConsoleDisplay;
import utilities.Formatter;
import utilities.InputValidator;
import games.Game;

/**
 * Unlucky9 game
 * - Player places a bet
 * - Player and Dealer each draw 3 digits (1-9)
 * - Hand value = sum of digits % 10 (closest to 9 wins)
 * - Exact 9 on player's hand -> special jackpot multiplier
 *
 * This class provides a static helper `play` that CasinoMain can call,
 * and an instance-style constructor + start() for other uses.
 */
public class Unlucky9 extends Game {
	private final Random random = new Random();

	// Use inherited `balance` from Game; do not shadow it here.

	public Unlucky9() {
		super();
	}

	public Unlucky9(double playerBalance) {
		super();
		this.balance = playerBalance;
	}

	// Static entry point for CasinoMain style usage
	public static void play(Player currentPlayer, PlayerDatabase playerDB) {
		Unlucky9 game = new Unlucky9();
		game.startGame(currentPlayer, playerDB);
	}

	// Play loop when provided player + db (main game flow)

	// Play loop when provided player + db
	private void playWithPlayer(Player currentPlayer, PlayerDatabase playerDB) {
		// ensure game state mirrors the logged-in player so balance updates apply
		if (currentPlayer != null) {
			this.player = currentPlayer;
			this.balance = currentPlayer.getBalance();
		}

		while (true) {
			ConsoleDisplay.clearConsole();
			System.out.println("\n\n");
			System.out.println("            ╔══════════════════════════════════════════════════════════╗");
			System.out.println("            ║                        Unlucky 9                        ║");
			System.out.println("            ╠══════════════════════════════════════════════════════════╣");
			System.out.println("            ║               Try to reach 9 (mod 10).                   ║");
			System.out.println("            ╚══════════════════════════════════════════════════════════╝");
			System.out.println("");
			System.out.println("                 Player: " + String.format("%-30s", currentPlayer.getUsername()));
			System.out.println("                 Balance: " + Formatter.formatCurrency(currentPlayer.getBalance()));
			System.out.println("");
			System.out.print("\n                 Choose (1-3): ");
			System.out.println();
			System.out.println("            ╔══════════════════════════════════════════════════════════╗");
			System.out.println("            ║   1. PLAY    2. TUTORIAL    3. EXIT GAME                 ║");
			System.out.println("            ╚══════════════════════════════════════════════════════════╝");

			int choice = InputValidator.readInt(1, 3);
			if (choice == 1) {
				if (currentPlayer.getBalance() <= 0) {
					System.out.println(
							"\n                 ❌ You have no funds to bet. Win some at other games or register more funds.");
					InputValidator.waitForUserInput("\n                 Press Enter to continue...");
					continue;
				}

				System.out.print("\n                 Enter bet amount: ");
				double bet = InputValidator.readDouble(1, currentPlayer.getBalance());
				// Verify player can afford the bet they chose
				if (!currentPlayer.canAfford(bet)) {
					System.out.println("\n                 ❌ You cannot afford that bet!");
					InputValidator.waitForUserInput("\n                 Press Enter to continue...");
					continue;
				}
				// Start round flow: deal 2 cards to player and dealer
				int[] playerCards = drawHand(2);
				int[] dealerCards = drawHand(2);

				// Show player's cards, hide dealer
				ConsoleDisplay.clearConsole();
				System.out.println("\n");
				System.out.println("            ╔══════════════════════════════════════════════════════════╗");
				System.out.println("            ║                        Unlucky 9                        ║");
				System.out.println("            ╠══════════════════════════════════════════════════════════╣");
				System.out.println("            ║               Bet Placed: "
						+ String.format("%-20s", Formatter.formatCurrency(bet)) + "           ║");
				System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				System.out.println("");
				// display player's two cards only
				// show player's two cards and reveal one of dealer's cards for thrill
				// small dealing animation
				loadingAnimation("Dealing", 8, 120);
				displayPlayerWithOneDealer(playerCards, dealerCards);

				// If player's shown hand is already 9, instant win (jackpot)
				int shownPlayerValue = handValue(playerCards);
				if (shownPlayerValue == 9) {
					// Deduct bet then award jackpot
					this.balance -= bet;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(), "Unlucky9", "BET", bet,
							this.balance);
					this.balance += bet * 3.0;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(), "Unlucky9", "WIN",
							bet * 3.0, this.balance);
					System.out.println();
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              JACKPOT! You hit 9 — Instant Win!         ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
					InputValidator.waitForUserInput("                 Press Enter to continue...");
					currentPlayer.updateGamesPlayed();
					playerDB.updatePlayer(currentPlayer);
					continue;
				}

				// Ask player whether to draw exactly one 3rd card
				System.out.print("                 Draw 3rd card? (Y/N): ");
				boolean draw = InputValidator.readYesNo();
				if (draw) {
					int card = drawSingle();
					playerCards = appendCard(playerCards, card);
					System.out.println("                 You drew: [" + card + "]");
					loadingAnimation("Processing draw", 6, 120);
				}

				// Deduct bet and log before dealer actions
				this.balance -= bet;
				currentPlayer.setBalance(this.balance);
				Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(), "Unlucky9", "BET", bet,
						this.balance);

				// Reveal dealer cards and compute values
				System.out.println();
				loadingAnimation("Revealing dealer", 6, 120);
				int playerValue = handValue(playerCards);
				int dealerValue = handValue(dealerCards);
				displayHands(playerCards, dealerCards, playerValue, dealerValue);

				// Dealer draws one card if dealer's value <= 5
				if (dealerValue <= 5) {
					int dealerCard = drawSingle();
					dealerCards = appendCard(dealerCards, dealerCard);
					System.out.println("\n                 Dealer draws: [" + dealerCard + "]");
					// recompute dealer value and show updated hands
					dealerValue = handValue(dealerCards);
					loadingAnimation("Dealer drawing", 6, 120);
					displayHands(playerCards, dealerCards, playerValue, dealerValue);
				}

				double payout = resolvePayout(bet, playerValue, dealerValue, playerCards);
				// suspense before result
				loadingAnimation("Calculating result", 8, 120);

				System.out.println("");
				if (payout > 0) {
					this.balance += payout;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(), "Unlucky9", "WIN", payout,
							this.balance);
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              YOU WON "
							+ String.format("%-33s", Formatter.formatCurrency(payout)) + "   ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				} else if (payout == 0 && playerValue == dealerValue) {
					this.balance += bet;
					currentPlayer.setBalance(this.balance);
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(), "Unlucky9", "PUSH", bet,
							this.balance);
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║                 PUSH — BET RETURNED                      ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				} else {
					Transaction.log(currentPlayer.getUsername(), currentPlayer.getPlayerId(), "Unlucky9", "LOSS", bet,
							this.balance);
					currentPlayer.setBalance(this.balance);
					System.out.println("            ╔══════════════════════════════════════════════════════════╗");
					System.out.println("            ║              YOU LOST "
							+ String.format("%-33s", Formatter.formatCurrency(bet)) + "  ║");
					System.out.println("            ╚══════════════════════════════════════════════════════════╝");
				}

				System.out.println("");
				System.out.println("                 New Balance: " + Formatter.formatCurrency(this.balance));
				System.out.println("");

				currentPlayer.updateGamesPlayed();
				playerDB.updatePlayer(currentPlayer);

				InputValidator.waitForUserInput("                 Press Enter to continue...");

			} else if (choice == 2) {
				// Show tutorial/rules screen then return to menu
				ConsoleDisplay.clearConsole();
				System.out.println("\n");
				System.out.println("            ╔════════════════════════════════════════════════════════════╗");
				System.out.println("            ║                        LUCKY 9 - TUTORIAL                  ║");
				System.out.println("            ╠════════════════════════════════════════════════════════════╣");
				System.out.println("            ║  - Each card is a digit 1..9. Hand value = sum % 10.       ║");
				System.out.println("            ║  - Closest to 9 wins. Exact 9 pays 3x, regular win pays 2x.║");
				System.out.println("            ║  - You'll be prompted for each card (Y/N) up to 3 draws.   ║");
				System.out.println("            ║    If you draw none, you'll receive 1 card automatically.  ║");
				System.out.println("            ╚════════════════════════════════════════════════════════════╝");
				System.out.println("");
				InputValidator.waitForUserInput("                 Press Enter to return to Unlucky9 menu...");
				continue;
			} else {
				// Exit to games menu
				return;
			}
		}
	}

	// Draw N cards valued 1..9
	private int[] drawHand(int n) {
		int[] cards = new int[n];
		for (int i = 0; i < n; i++) {
			cards[i] = drawSingle();
		}
		return cards;
	}

	// Draw a single card (1..9)
	private int drawSingle() {
		return random.nextInt(9) + 1;
	}

	// Hand value: (sum of cards) % 10
	private int handValue(int[] cards) {
		int sum = 0;
		for (int c : cards)
			sum += c;
		return sum % 10;
	}

	private void displayHands(int[] player, int[] dealer, int playerValue, int dealerValue) {
		System.out.println();
		System.out.println("            ╔════════════════════════════════════════════════════════════╗");
		System.out.print("            ║  Player: ");
		for (int v : player)
			System.out.print("[" + v + "] ");
		System.out.println("  => " + playerValue + "                                ║");

		System.out.print("            ║  Dealer : ");
		for (int v : dealer)
			System.out.print("[" + v + "] ");
		System.out.println("  => " + dealerValue + "                               ║");
		System.out.println("            ╚════════════════════════════════════════════════════════════╝");
	}

	// (Removed unused displayPlayerOnly)

	// Display player's cards and reveal only the dealer's first card
	private void displayPlayerWithOneDealer(int[] player, int[] dealer) {
		System.out.println();
		System.out.println("            ╔════════════════════════════════════════════════════════════╗");
		System.out.print("            ║  Player: ");
		for (int v : player)
			System.out.print("[" + v + "] ");
		System.out.println("  => " + handValue(player) + "                                    ║");
		System.out.print("            ║  Dealer : ");
		// reveal dealer first card, hide the rest
		if (dealer.length > 0)
			System.out.print("[" + dealer[0] + "] ");
		for (int i = 1; i < dealer.length; i++) {
			System.out.print("[?] ");
		}
		System.out.println("  => ?                                   ║");
		System.out.println("            ╚════════════════════════════════════════════════════════════╝");
	}

	// Simple loading animation used between actions to increase suspense
	private void loadingAnimation(String message, int cycles, int delayMs) {
		String[] frames = { ".", "..", "...", " ..", "  .", "   " };
		for (int i = 0; i < cycles; i++) {
			System.out.print("\r                 " + message + frames[i % frames.length] + "   ");
			try {
				Thread.sleep(delayMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		System.out.println();
	}

	// Append a single card to an array, returning a new array
	private int[] appendCard(int[] arr, int card) {
		int[] out = new int[arr.length + 1];
		System.arraycopy(arr, 0, out, 0, arr.length);
		out[arr.length] = card;
		return out;
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
		System.out.println("Unlucky9 Rules: Draw 3 digits (1-9). Closest to 9 wins. Exact 9 => jackpot x3.");
	}

	@Override
	public String getGameName() {
		return "Unlucky9";
	}

	@Override
	public void updateBalance(double amount) {
		this.balance += amount;
		if (this.player != null) {
			this.player.setBalance(this.balance);
		}
	}

}

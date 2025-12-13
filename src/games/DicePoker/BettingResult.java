package games.DicePoker;

/**
 * Holds the result of a betting round.
 */
public class BettingResult {
    public final double pot;
    public final boolean folded;
    public final int foldWinner; // 1=player, -1=bot, 0=no fold

    public BettingResult(double pot, boolean folded, int foldWinner) {
        this.pot = pot;
        this.folded = folded;
        this.foldWinner = foldWinner;
    }
}
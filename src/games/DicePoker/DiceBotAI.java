package games.DicePoker;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple bot AI for deciding which dice to reroll.
 * Strategy: keep any groups (pairs, trips, quads). If a straight is present,
 * keep all.
 * Otherwise keep the highest single and reroll the rest.
 */
public class DiceBotAI {
    public static List<Integer> selectReroll(DiceSet hand) {
        List<Integer> toReroll = new ArrayList<>();
        int[] dice = hand.getRawDice();
        int[] counts = new int[7];
        for (int d : dice)
            if (d >= 1 && d <= 6)
                counts[d]++;

        // if straight -> keep all
        boolean straight = true;
        boolean oneToFive = true;
        for (int v = 1; v <= 5; v++)
            if (counts[v] != 1)
                oneToFive = false;
        boolean twoToSix = true;
        for (int v = 2; v <= 6; v++)
            if (counts[v] != 1)
                twoToSix = false;
        straight = oneToFive || twoToSix;
        if (straight)
            return toReroll; // empty -> keep all

        // find values to keep (counts >=2)
        boolean[] keepValue = new boolean[7];
        for (int v = 1; v <= 6; v++)
            if (counts[v] >= 2)
                keepValue[v] = true;

        // if nothing to keep, keep highest single
        if (java.util.stream.IntStream.rangeClosed(1, 6).noneMatch(v -> counts[v] >= 2)) {
            int maxVal = -1;
            int maxIdx = -1;
            for (int i = 0; i < dice.length; i++) {
                if (dice[i] > maxVal) {
                    maxVal = dice[i];
                    maxIdx = i;
                }
            }
            // keep the highest die, reroll others
            for (int i = 0; i < dice.length; i++)
                if (i != maxIdx)
                    toReroll.add(i);
            return toReroll;
        }

        // otherwise reroll dice that are not part of the groups
        for (int i = 0; i < dice.length; i++) {
            if (!keepValue[dice[i]])
                toReroll.add(i);
        }
        return toReroll;
    }

    /**
     * Decide betting action for the bot.
     * Returns: -1 = fold, 0 = call/check, >0 = raise amount (additional over the
     * call)
     */
    public static double decideBet(double toCall, double pot, double botBalance, DiceRank hand, double oppBalance,
            int raisesRemaining) {
        // Basic heuristics
        if (toCall <= 0) {
            // can check or bet
            if (hand.compareTo(DiceRank.THREE_OF_A_KIND) >= 0) {
                // strong hand, raise moderately
                double raise = Math.min(botBalance, Math.max(1.0, pot * 0.2));
                return raise; // positive => raise
            }
            // otherwise check
            return 0;
        } else {
            // need to call to stay in
            if (hand.compareTo(DiceRank.PAIR) >= 0) {
                // keep with pair or better
                return 0; // call
            }
            // weak hand: fold if toCall large compared to pot/balance
            double affordRatio = toCall / Math.max(1.0, botBalance);
            if (affordRatio > 0.5)
                return -1; // fold
            // else maybe call small invites
            if (toCall <= pot * 0.25)
                return 0; // call
            return -1; // otherwise fold
        }
    }
}

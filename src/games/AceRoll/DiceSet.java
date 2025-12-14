package games.AceRoll;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import utilities.Formatter;

/**
 * Represents a set of five dice and evaluation logic.
 * This class does NOT read console input. It only provides methods
 * to roll dice, reroll given indices, display them and evaluate the hand.
 */
public class DiceSet {
    private final int[] dice = new int[5];
    private final Random random = new Random();

    public DiceSet() {
        // empty
    }

    public void rollAll() {
        for (int i = 0; i < dice.length; i++)
            dice[i] = roll();
    }

    public int roll() {
        return random.nextInt(6) + 1;
    }

    /**
     * Reroll dice at the given zero-based indices. Duplicates in the list are
     * ignored.
     */
    public void reroll(List<Integer> zeroBasedIndices) {
        if (zeroBasedIndices == null || zeroBasedIndices.isEmpty())
            return;
        // ensure unique and within range
        List<Integer> uniqueIndices = zeroBasedIndices.stream()
                .filter(i -> i >= 0 && i < dice.length)
                .distinct()
                .collect(Collectors.toList());
        for (int i : uniqueIndices) {
            dice[i] = roll();
        }
    }

    public void showHand() {
        System.out.println("Here are the values of your hand:");
        for (int i = 0; i < dice.length; i++) {
            System.out.println(Formatter.numToDice(dice[i]) + " (" + dice[i] + ")");
        }
    }

    /**
     * Evaluate and return only the rank. For tie-breaking use
     * {@link #getSortedDescending()} or
     * extend this class to return a score object.
     */
    public DiceRank evaluateHand() {
        int[] counts = new int[7]; // 0 unused, 1..6
        for (int d : dice) {
            if (d >= 1 && d <= 6)
                counts[d]++;
        }

        boolean isFive = false, isFour = false, isThree = false;
        int pairs = 0;
        for (int value = 1; value <= 6; value++) {
            int count = counts[value];
            if (count == 5)
                isFive = true;
            if (count == 4)
                isFour = true;
            if (count == 3)
                isThree = true;
            if (count == 2)
                pairs++;
        }

        if (isFive)
            return DiceRank.FIVE_OF_A_KIND;
        if (isFour)
            return DiceRank.FOUR_OF_A_KIND;
        if (isThree && pairs == 1)
            return DiceRank.FULL_HOUSE;
        if (checkStraight(counts))
            return DiceRank.STRAIGHT;
        if (isThree)
            return DiceRank.THREE_OF_A_KIND;
        if (pairs == 2)
            return DiceRank.TWO_PAIR;
        if (pairs == 1)
            return DiceRank.PAIR;
        return DiceRank.NO_COMBINATION;
    }

    private boolean checkStraight(int[] counts) {
        // small straight 1-5 or big straight 2-6
        boolean oneToFive = true;
        for (int v = 1; v <= 5; v++)
            if (counts[v] != 1)
                oneToFive = false;
        boolean twoToSix = true;
        for (int v = 2; v <= 6; v++)
            if (counts[v] != 1)
                twoToSix = false;
        return oneToFive || twoToSix;
    }

    /**
     * Return a copy of dice sorted descending for tie-breaking.
     */
    public int[] getSortedDescending() {
        int[] copy = Arrays.copyOf(dice, dice.length);
        Arrays.sort(copy);
        // reverse
        for (int i = 0; i < copy.length / 2; i++) {
            int tempValue = copy[i];
            copy[i] = copy[copy.length - 1 - i];
            copy[copy.length - 1 - i] = tempValue;
        }
        return copy;
    }

    public int[] getRawDice() {
        return Arrays.copyOf(dice, dice.length);
    }
}

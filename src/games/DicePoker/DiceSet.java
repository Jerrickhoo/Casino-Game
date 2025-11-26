package games.DicePoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// import utilities.utilities;

public class DiceSet {
    private Die[] dice = new Die[5];

    public DiceSet() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = new Die();
        }
    }

    public Die[] getDice() {
        return dice;
    }

    public void rollAll(){
        for(Die d: dice){
            d.roll();
        }
    }

    public void showHand(){
       
        int i=1;
        System.out.println("Here are the values of your hand: ");
        for(Die d: dice){
            System.out.println(i + ". " + d.numToRoman());
            i++;
        }
    }

    public void reroll() {
        
        Scanner scan = new Scanner(System.in);
        
        List<Integer> rerollIndices = new ArrayList<>();
        while (true) {
            System.out.print("Enter 1-5 to reroll a die, 0 to stop: ");
            int input = scan.nextInt();
            if (input == 0) break;
            if (input < 1 || input > 5) {
                System.out.println("Invalid input. Try again.");
                continue;
            }
            rerollIndices.add(input - 1); // store 0-based index
        }

        // reroll the selected dice
        for (int idx : rerollIndices) {
            dice[idx].roll();
        }
        // utilities.clearConsole();
        System.out.println("After reroll:");
        showHand();
    }

    public String evaluateHand() {
        int[] counts = new int[7]; // index 1-6

        // Count occurrences of each die value
        for (Die d : dice) {
            counts[d.getValue()]++;
        }

        boolean three = false;
        int pairs = 0;

        for (int c : counts) {
            if (c == 5) return "Five of a Kind";
            if (c == 4) return "Four of a Kind";
            if (c == 3) three = true;
            if (c == 2) pairs++;
        }

        if (three && pairs == 1) return "Full House";
        if (three) return "Three of a Kind";
        if (pairs == 2) return "Two Pair";
        if (pairs == 1) return "Pair";

        return "No Combination";
    }

    public int getHandRank() {
        String hand = evaluateHand();
        switch (hand) {
            case "Five of a Kind": return 7;
            case "Four of a Kind": return 6;
            case "Full House": return 5;
            case "Three of a Kind": return 4;
            case "Two Pair": return 3;
            case "Pair": return 2;
            default: return 1; // No Combination
        }
    }


}

package games.DicePoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DiceSet {
    private static Die[] dice = new Die[5];

    public DiceSet() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = new Die();
        }
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

    System.out.println("After reroll:");
    showHand();
}

}

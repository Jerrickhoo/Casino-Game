package games.DicePoker;

public class DicePoker {
    public static void main(String[] args) {
        DiceSet dSet = new DiceSet();

        dSet.rollAll();
        dSet.showHand();
        //dSet.reroll();

        HandEvaluator eval = new HandEvaluator();

        eval.evaluateHand();
        
    }
}

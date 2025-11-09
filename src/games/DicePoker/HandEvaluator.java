package games.DicePoker;

public class HandEvaluator {
    private static DiceSet diceSet = new DiceSet();

    public HandEvaluator(){
        diceSet = new DiceSet();
    }

    public DiceSet getDiceSet() {
        return diceSet;
    }
    
    public void setDiceSet(DiceSet diceSet) {
        HandEvaluator.diceSet = diceSet;
    }

    public void evaluateHand(){
        diceSet.showHand();
    }

}

package games.DicePoker;

import java.util.Random;

public class Die {
    private int value;
    private Random random = new Random();

    public void roll() {
        value = random.nextInt(6) + 1;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String numToRoman(){
        if(this.value == 1){
            return "I";
        }else if(this.value == 2){
            return "II";
        }else if(this.value == 3){
            return "III";
        }else if(this.value == 4){
            return "IV";
        }else if(this.value == 5){
            return "V";
        }else if(this.value == 6){
            return "VI";
        }else{
            return "";
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

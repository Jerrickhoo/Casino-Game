package games.DicePoker;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Core.Player;
import Core.PlayerDatabase;
import games.Game;

public class DicePoker extends Game {
    private double balance;
    private int[] diceSet = new int[5];
    private Random random = new Random();


    public DicePoker(double playerBalance){
        this.balance = playerBalance;
    }

    public DicePoker(){
        super();
    }

    public void startGame(Player player, PlayerDatabase playerDB) {

        int i = 1;
        
        while(i == 1){
        
            DiceSet playerSet = new DiceSet();
            DiceSet botSet = new DiceSet();
            //int pot = 0;
            
            //betting(pot, playerAcc);
            playerSet.rollAll();
            playerSet.showHand();
            playerSet.reroll();

            System.out.println("\nBot Hand:");
            botSet.rollAll();
            botSet.showHand();

            

            System.out.println(playerSet.evaluateHand());
            System.out.println(botSet.evaluateHand());

            int playerRank = playerSet.getHandRank();
            int botRank = botSet.getHandRank();
            if(playerRank > botRank){
                System.out.println("Player Wins!");
            }else if(botRank > playerRank){
                System.out.println("Opponent Wins!");
            }else{
                System.out.println("Tie");
            }
            
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter 1");
            i = scan.nextInt();
            
        } 
    }

    public void rollAll() {
        for (int i = 0; i < diceSet.length; i++) {
            diceSet[i] = roll();
        }
    }

    public int roll() {
        return random.nextInt(6) + 1;
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
            rerollIndices.add(input - 1); 
        }

        // reroll the selected dice
        for (int i : rerollIndices) {
            diceSet[i] = roll();
        }
        System.out.println("After reroll:");
        showHand();
    }

    public void showHand() {
        System.out.println("Here are the values of your hand: ");
        for (int i = 0; i < diceSet.length; i++) {
            System.out.println((i + 1) + ". " + numToRoman(diceSet[i]));
        }
    }

    public String numToRoman(int num){
        if(num == 1){
            return "I";
        }else if(num == 2){
            return "II";
        }else if(num == 3){
            return "III";
        }else if(num == 4){
            return "IV";
        }else if(num == 5){
            return "V";
        }else if(num == 6){
            return "VI";
        }else{
            return "";
        }
    }

    public String evaluateHand() {
        int[] counts = new int[7]; // index 1-6

        // Count occurrences of each die value
        for (int d : diceSet) {
            counts[d]++;
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


    public void playRound(){

    }

    @Override
    public double calculatePayout() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculatePayout'");
    }

    @Override
    public void displayRules() {
        System.out.println("Welcome to Dice Poker!");

    }

    @Override
    public String getGameName() {
        return "Dice Poker";
    }

    @Override
    public void updateBalance(double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBalance'");
    }


    public static void betting(int pot, Player playerAcc){
        Random random = new Random();
        Scanner scan = new Scanner(System.in);
        int botBet;
        String call;
    
        System.out.println("Enter Bet Amount: ");
        int bet = scan.nextInt();

        int raise = random.nextInt(2);

        if(raise == 1){
          System.out.println("Opponent raised 3");
          botBet = bet + 3;

          System.out.println("Call? (y/n): ");
          call = scan.nextLine();
          if(call == "y"){
            bet = bet + 3;
            playerAcc.setBalance(playerAcc.getBalance() - bet);
          }
          
        } else{
            playerAcc.setBalance(playerAcc.getBalance() - bet);
            botBet = bet;
        }

        pot = botBet + bet;
        

    }
}

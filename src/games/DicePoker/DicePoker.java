package games.DicePoker;

import java.util.Scanner;
import java.util.Random;

import Core.Player;

public class DicePoker {
    private Player playerAcc;
    public static void main(String[] args) {
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

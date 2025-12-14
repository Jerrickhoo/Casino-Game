package tools;

import java.lang.reflect.Method;

import games.Blackjack.BlackJack;
import games.Blackjack.Card;
import games.Blackjack.Hand;

public class TestBlackjackDisplay {
    public static void main(String[] args) throws Exception {
        BlackJack bj = new BlackJack();

        Hand player = new Hand();
        Hand dealer = new Hand();

        // Player: A K (Blackjack)
        player.add(new Card("A", "S"));
        player.add(new Card("K", "H"));

        // Dealer: 7 9 (16)
        dealer.add(new Card("7", "D"));
        dealer.add(new Card("9", "C"));

        Method printHeader = BlackJack.class.getDeclaredMethod("printHeader");
        Method displayHandBox = BlackJack.class.getDeclaredMethod("displayHandBox", Hand.class, Hand.class,
                boolean.class);
        printHeader.setAccessible(true);
        displayHandBox.setAccessible(true);

        // Show initial hands (dealer hidden) — avoid clearing console to preserve
        // captured output
        displayHandBox.invoke(bj, dealer, player, false);

        // Simulate dealer drawing cards while keeping player visible
        Thread.sleep(900);
        dealer.add(new Card("5", "H"));
        printHeader.invoke(bj);
        displayHandBox.invoke(bj, dealer, player, true);

        Thread.sleep(900);
        dealer.add(new Card("2", "S"));
        printHeader.invoke(bj);
        displayHandBox.invoke(bj, dealer, player, true);

        System.out.println();
        System.out.println("[Test complete] Check that player cards remained visible during dealer draws.");
    }
}

package blitz_gui;

import java.util.ArrayList;
import javax.swing.*;

/**
 * The Good Wheel - it contains mostly positive
 * effects for you...and your opponents if
 * luck isn't on your side.
 * 
 * @author Taylor Fanel-Luevano
 * @version 1.2
 */
public class GoodWheel extends Wheel {

    /**
     * Build the good wheel with a weight of 5 and a cost of -2 coins
     * @param players - the Player list pointer
     * @see Wheel#Wheel(int, int, ArrayList)
     */
    public GoodWheel(ArrayList<Player> players) {
        super(5, -2, players);
    }

    /**
     * Spins the good wheel, with an effect based on the number
     * spun by spinWheel().
     * @param player - The player that spun the wheel
     * @param menu - main menu; used to display messages
     * @see Wheel#chargePlayer(Player, Blitz)
     * @see Wheel#spinWheel()
     * @see Player#modCoinCount(double, Blitz)
     * @see Player#getName()
     * @see Player#getCoins()
     * @see Player#setCoinCoint(int)
     * @see Player#curse()
     */
    public void spinGoodWheel(Player player, Blitz menu) {
        chargePlayer(player, menu); // Charge the player

        int wheelSpin = spinWheel(); // Spin the wheel to get a number
        ArrayList<Player> dynamic; // Create a new ArrayList for specific ops

        switch (wheelSpin) {

            case 1: // Outcome 1

                // Give the player a coin (essentially gives them the cost + 1)
                JOptionPane.showMessageDialog(menu, player.getName()+" has gained 1 coin. Not bad!");
                player.modCoinCount(-WHEEL_COST + 1, menu);

                break;
            case 2: // Outcome 2

                // Give the player 2 coins (essentially gives them the cost + 2)
                JOptionPane.showMessageDialog(menu, player.getName()+" has gained 2 coins. Now we're in business!");
                player.modCoinCount(-WHEEL_COST + 2, menu);

                break;
            case 3: // Outcome 3

                // Refund the player and give every other player 1 coin
                JOptionPane.showMessageDialog(menu, player.getName()+" had their coins refunded and got everyone else +1 coin.");
                player.modCoinCount(-WHEEL_COST, menu);
                for (int i = 0; i < playerList.size(); i++) {
                    if (!playerList.get(i).getName().equals(player.getName())) 
                        playerList.get(i).modCoinCount(1, menu);;
                }
                break;
            case 4: // Outcome 4

                // Refund the player and give the cost of the wheel spin over to the poorest player
                JOptionPane.showMessageDialog(menu, player.getName()+" had their coins refunded and gave the poorest player\n"
                                                                    +"the cost of their wheel spin.");
                // Refund the player
                player.modCoinCount(-WHEEL_COST, menu);

                dynamic = new ArrayList<Player>();

                // Add every other player to the dynamic list and determine the poorest player among them
                for (Player iterate : playerList) {
                    if (!iterate.getName().equals(player.getName())) dynamic.add(iterate);
                }
                
                Player poorest = dynamic.get(0);

                for (Player iterate : dynamic) {
                    if (iterate.getCoins() <= poorest.getCoins()) 
                        poorest = iterate;
                }

                // Once determined, give the coins over to the poorest player
                poorest.modCoinCount(-WHEEL_COST, menu);

                break;
            case 5: // Outcome 5
                
                // Curse the richest player and cut their coins in half
                JOptionPane.showMessageDialog(menu, player.getName()+" cursed the richest player and cut their coins in half!");
                dynamic = new ArrayList<Player>();

                // Add every other player to the dynamic list and determine the richest player among them
                for (Player iterate : playerList) {
                    if (!iterate.getName().equals(player.getName())) dynamic.add(iterate);
                }

                Player richest = dynamic.get(0);

                for (Player iterate : dynamic) {
                    if (iterate.getCoins() >= richest.getCoins()) 
                        richest = iterate;
                }

                // Once determined, halve the coins and curse the richest player
                richest.setCoinCoint((int)(richest.getCoins() * 0.5));
                richest.curse();

                break;
        }
    }
}

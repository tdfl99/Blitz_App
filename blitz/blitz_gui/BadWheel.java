package blitz_gui;

import java.util.ArrayList;
import javax.swing.*;

/**
 * The Bad Wheel - it gives you coins right off the
 * bat, but it may come at a stronger price - for you,
 * or for your opponents.
 * 
 * @author Taylor Fanel-Luevano
 * @version 1.2
 */
public class BadWheel extends Wheel {
    
    /**
     * Build the bad wheel with a weight of 5 and a 'cost' of +2 coins
     * @param players - the Player list pointer
     * @see Wheel#Wheel(int, int, ArrayList)
     */
    public BadWheel(ArrayList<Player> players) {
        super(6, 2, players);
    }

    /**
     * Spins the bad wheel, with an effect based on the number
     * spun by spinWheel().
     * @param player - The player that spun the wheel
     * @param menu - main menu; used to display messages
     * @see Wheel#chargePlayer(Player, Blitz)
     * @see Wheel#spinWheel()
     * @see Player#modCoinCount(double, Blitz)
     * @see Player#getName()
     * @see Player#getCoins()
     * @see Player#curse()
     */
    public void spinBadWheel(Player player, Blitz menu) {
        int prevCoinCount = player.getCoins(); /* Since the curse effect applies to the coins gained by
                                                spinning the bad wheel, it's important to keep track of
                                                what the previous coin count of the player was */ 

        chargePlayer(player, menu); // Charge the player

        int wheelSpin = spinWheel(); // Spin the wheel to get a number
        ArrayList<Player> dynamic; // Create a new ArrayList for specific ops

        switch (wheelSpin) {

            case 1: // Otucome 1

                // Subtract a coin (net gain overall)
                JOptionPane.showMessageDialog(menu, player.getName()+" has lost a coin. Still a net gain, not bad!");
                player.modCoinCount(-1, menu);

                break;

            case 2: // Outcome 2

                // Subtract the earnings + 1
                JOptionPane.showMessageDialog(menu, player.getName()+" has lost their earnings + 1 coin. Better luck next time.");
                player.modCoinCount(-(player.getCoins() - prevCoinCount) - 1, menu);

                break;

            case 3: // Outcome 3

                // Subtract the earnings and curse the player
                JOptionPane.showMessageDialog(menu, player.getName()+" lost their earnings and have been cursed!");
                player.modCoinCount(-(player.getCoins() - prevCoinCount), menu);
                player.curse();

                break;

            case 4: // Outcome 4 & 5; grouped together since they both deal with the richest player
            case 5:

                // First, determine the richest player
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
                // Once determined, see which number was pulled

                // If the spin is a 4...
                if (wheelSpin == 4) {
                    // Steal 2 coins from the richest player
                    JOptionPane.showMessageDialog(menu, player.getName()+" stole 2 coins from the richest player!");
                    player.modCoinCount(2, menu);
                    richest.modCoinCount(-2, menu);
                // Otherwise if the spin was a 5...
                } else if (wheelSpin == 5) {
                    // Curse the richest player and cut their coins in half
                    JOptionPane.showMessageDialog(menu, player.getName()+" cursed the richest player and cut their coins in half!");
                    richest.setCoinCoint((int)(richest.getCoins() * 0.5));
                    richest.curse();
                }
                break;

            case 6: // Outcome 6

                // Take the earnings and give it over to the poorest player
                JOptionPane.showMessageDialog(menu, player.getName()+"'s earnings have been stolen by the poorest player!");
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

                // Once determined, remove the coins and give them to the poorest player
                poorest.modCoinCount(player.getCoins() - prevCoinCount, menu);
                player.modCoinCount(-(player.getCoins() - prevCoinCount), menu);

                break;
                

        }
    }
}
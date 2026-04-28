package blitz_gui;

import java.util.ArrayList;
import javax.swing.*;

/**
 * The Chaos Wheel - If it awakens during a turn, there
 * is always the risk of a player being forced to spin
 * it. This chaos is a little limited, but no less nerve
 * wracking.
 * 
 * @author Taylor Fanel-Luevano
 * @version 1.1
 */
public class ChaosWheel extends Wheel {
    
    private boolean exhausted = true; // Checks if the Chaos Wheel is ready to spin
    private int fatigue = 3; // Wheel fatigue; if at 0, then the Chaos Wheel is ready to spin

    /**
     * Build the chaos wheel with a weight of 6 and a cost 
     * of 0 coins, since spinning it is involuntary.
     * @param players - the Player list pointer
     * @see Wheel#Wheel(int, int, ArrayList)
     */
    public ChaosWheel(ArrayList<Player> players) {
        super(6, 0, players);
    }

    /**
     * Spins the chaos wheel, with an effect based on the number
     * spun by spinWheel().
     * @param player - The player that spun the wheel
     * @param menu - main menu; used to display messages
     * @param goodWheel - stores the good wheel for one effect
     * @param badWheel - ditto
     * @see Wheel#spinWheel()
     * @see Player#modCoinCount(double, Blitz)
     * @see Player#getName()
     * @see Player#getCoins()
     * @see Player#setCoinCoint(int)
     * @see Player#curse()
     * @see Player#unCurse()
     */
    public void spinChaosWheel(Player player, Blitz menu, GoodWheel goodWheel, BadWheel badWheel) {

        // Anounce the incoming spin
        JOptionPane.showMessageDialog(menu, player.getName()+" has been forced to spin the Chaos Wheel!");

        int wheelSpin = spinWheel(); /* Spin the wheel to get a number 
                                    (since the cost is 0, chargePlayer() would do nothing) */ 

        switch (wheelSpin) {

            case 1: // Outcome 1

                // Do nothing
                JOptionPane.showMessageDialog(menu, player.getName()+" has had...nothing happen to them."+
                                                                "\nChaos includes nothing happening, you know.");

                break;

            case 2: // Outcome 2

                // Force another Chaos Wheelspin
                JOptionPane.showMessageDialog(menu, player.getName()+" has...to spin the wheel again."+
                                                                "\nWho knows, you might get stuck in a loop!");
                spinChaosWheel(player, menu, goodWheel, badWheel);

                break;

            case 3: // Outcome 3

                // Swap the curse values of each player
                JOptionPane.showMessageDialog(menu, player.getName()+" un-cursed all cursed and cursed all not cursed."+
                                                                "\nIt's cursed-a-geddon! Or something, idk.");
                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(i).isCursed()) playerList.get(i).unCurse();
                    else playerList.get(i).curse();
                }

                break;

            case 4: // Outcome 4

                // Reduce the player's coin count to 1
                JOptionPane.showMessageDialog(menu, player.getName()+"'s finger slipped and lost all of their coins but 1."+
                                                                "\n...sorry?");
                player.setCoinCoint(1);

                break;

            case 5: // Outcome 5

                // Give the player 3 coins
                JOptionPane.showMessageDialog(menu, player.getName()+" had defied all odds and gained +3 coins! Lucky you.");
                player.modCoinCount(3, menu);
                break;

            case 6: // Outcome 6

                // Force the player to spin both Good and Bad Wheels
                JOptionPane.showMessageDialog(menu, player.getName()+" is forced to spin both the good and bad wheels!"+
                                                                    "\nWelp, hope you get lucky then");
                goodWheel.spinGoodWheel(player, menu);
                badWheel.spinBadWheel(player, menu);
                break;
        }

        fatigue++; // When spun, increase the wheel's fatigue

        // Once fatigue is 3, set the Wheel to not be spun
        if (fatigue == 3) {
            JOptionPane.showMessageDialog(menu, "The Chaos wheel grows weary and wains...");
            exhausted = true;
        }
    }

    /**
     * Advances the Chaos Wheel's rest stage by removing fatigue.
     * @param menu - main menu, for displaying messages
     */
    public void rest(Blitz menu) {
        fatigue--; // Assumes the Chaos Wheel is advancing its rest stage, so decrement fatigue

        // Display messages based on the state of fatigue
        if (fatigue == 1) JOptionPane.showMessageDialog(menu, "The Chaos wheel stirs...");
        else if (fatigue == 0) {
            JOptionPane.showMessageDialog(menu, "The Chaos wheel rattles to life...");
            exhausted = false; // Once fatigue is 0, set the Wheel as ready to spin
        }
    }

    /**
     * Checks if the Chaos Wheel is currently 
     * unable to be spun.
     * @return if the wheel can be spun
     */
    public boolean isExhausted() {
        return exhausted;
    }
}
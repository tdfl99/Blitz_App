package blitz_gui;

import javax.swing.JOptionPane;

/**
 * This is the Player class that stores the info of any
 * player that participates in the game.
 * 
 * @author Taylor Fanel-Luevano
 * @version 1.1
 */
public class Player {
    private String name; // Stores their name
    private int coins = 5; // Stores their coin count; initialized to 5
    private boolean isCursed = false; // Checks if they're cursed; initially false

    /**
     * Creates a player object and gives it a name.
     * @param playerName - the name to be given to the new player object
     */
    public Player(String playerName) {
        name = playerName;
    }

    /**
     * Fetches the player name.
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Fetches the player coint count.
     * @return coin count of the player
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Checks if the player is cursed or not.
     * @return a boolean to check if player is cursed
     */
    public boolean isCursed() {
        return isCursed;
    }

    /**
     * Curses the player.
     */
    public void curse() {
        isCursed = true;
    }

    /**
     * Un-curses the player.
     */
    public void unCurse() {
        isCursed = false;
    }

    /**
     * Modifies the player's coint count directly, without involving
     * the curse status effect.
     * @param modifier - the % modifier to apply to the player's coins
     */
    public void setCoinCoint(int modifier) {
        coins = modifier;
    }

    /**
     * Increases or decreases the coin count based on what a spin
     * has done to the player.
     * @param coinsToMod - the number of coins to add or subtract
     * @param main - the main menu; used to display the message
     */
    public void modCoinCount(double coinsToMod, Blitz main) {

        // First check if the player is cursed and if their coin gains are at least 1
        if (isCursed && coinsToMod > 0) {

            // Apply the curse effect, cutting their coin gain in half
            coins += (coinsToMod * 0.5);
            isCursed = false; // After doing this, the player is cleared of the effect
            JOptionPane.showMessageDialog(main, name+" has been cleansed at a loss of "+(int)(coinsToMod * 0.5)+" coins.");
        
        // If none of the above is true, simply adjust the coin count.
        } else coins += coinsToMod;

        // Prevent the coin count from going below 0
        if (coins < 0) coins = 0;
    }
}

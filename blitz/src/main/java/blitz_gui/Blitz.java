package blitz_gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

/**
 * BLITZ : The Ultimate Game of Chance!
 * 
 * This is a simple multiplayer game for up to 8 people that
 * intended to play much like a party game, with mostly random
 * elements that add to both the chaotic nature of such games
 * while encouraging replayability.
 * 
 * @author Taylor Fanel-Luevano
 * @version 1.1
 */
public class Blitz extends JFrame implements ActionListener {
    private final int MAX_PLAYERS = 8, // player/round count related limits
                     MIN_PLAYERS = 4, 
                     MAX_ROUNDS = 9,
                     MIN_ROUNDS = 3,
                     ROUND_INCREMENT = 3,
                     ROUND_FORFEIT = -1; // determines the cost for forfeiting a turn
    
    private JPanel gamePanel = new JPanel(new GridBagLayout()), // JPanels for all GUI elements
                   newGame = new JPanel(new GridBagLayout()),   // each one uses a GridBag layout
                   mainGame = new JPanel(new GridBagLayout()),
                   l1,      
                   l2,  // generic layer Panels to separate groups of UI elements
                   l3;
    
    private JButton addPlayer = new JButton("<html><div style='text-align: center;'>Add<br>Player</div></html>"), // Buttons for all player functions
                    remPlayer = new JButton("<html><div style='text-align: center;'>Remove<br>Player</div></html>"), 
                    begin = new JButton("Begin Game"),
                    spinGoodWheel = new JButton(), // No labels as they are established later
                    spinBadWheel = new JButton(),
                    forfeitTurn = new JButton();
    
    private JLabel title = new JLabel("BLITZ"), // Labels for all related elements
                   roundTitle = new JLabel("Number of Rounds ("+MIN_ROUNDS+" -> "+MAX_ROUNDS+"):"),
                   flavorText = new JLabel("Spin a wheel if you dare, %s..."), // Placeholder Labels; prevents a critical
                   currentRound = new JLabel("Current Round: %d");             // bug where the table isn't sized properly

    private JTable playerTable, // Tables to keep track of player activity
                   playerTableCoins;

    private JSpinner roundSelection; // Spinner to set the number of rounds

    private ArrayList<Player> playerList = new ArrayList<Player>(); // Array that stores all player objects

    private int numPlayers = 0, // Game variables that are dynamic each game
                roundNum = 1,
                turnNum = 0,
                maxRounds;

    private Player currentPlayer; // Stores the player that currently has a turn

    private final GoodWheel goodWheel = new GoodWheel(playerList); // Wheel objects for the game
    private final BadWheel badWheel = new BadWheel(playerList);
    private final ChaosWheel chaosWheel = new ChaosWheel(playerList);

    private Random chaosWheelRest; // Unique random generator for the ChaosWheel

    /**
     * Constructor for the main game window
     * @see Blitz#buildStartMenu()
     * @see Blitz#buildMainGame()
     */
    public Blitz() {
        setTitle("BLITZ");
        setResizable(false);
        add(gamePanel);

        // builds the primary game menus
        buildStartMenu();
        buildMainGame();
        mainGame.setVisible(false); // So the main game menu doesn't overlap the start menu
        
        GridBagConstraints l = new GridBagConstraints();
        l.insets = new Insets(10, 10, 10, 10);
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 0;
        gamePanel.add(newGame, l);
        l.insets = new Insets(10, 10, 10, 10);
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 0;
        gamePanel.add(mainGame, l);
    }

    /**
     * Contructs the start menu, allowing the addition and
     * removal of players and setting the number of rounds
     */
    
    private void buildStartMenu() {
        String[] nameListHeading = {"Players"}; // String arrays to store into the table
        String[][] playerNameList = new String[MAX_PLAYERS][1];
        GridBagConstraints l;

        addPlayer.setPreferredSize(new Dimension(80, 40));
        addPlayer.addActionListener(this);
        remPlayer.setPreferredSize(new Dimension(80, 40));
        remPlayer.addActionListener(this);
        begin.setPreferredSize(new Dimension(120, 25));
        begin.addActionListener(this);

        SpinnerNumberModel m = new SpinnerNumberModel(MIN_ROUNDS, MIN_ROUNDS, MAX_ROUNDS, ROUND_INCREMENT);
        roundSelection = new JSpinner(m);

        // playerTable stores just the names of the players
        playerTable = new JTable(playerNameList, nameListHeading);
        playerTable.setEnabled(false); // To prevent editing of the table
        TableColumnModel t = playerTable.getColumnModel();
        t.getColumn(0).setPreferredWidth(150);

        title.setFont(new Font("Times New Roman", Font.BOLD, 18));
        roundTitle.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        // Set layers
        l1 = new JPanel(new GridBagLayout());   // title
        l2 = new JPanel(new GridBagLayout());   // settings
        l3 = new JPanel(new GridBagLayout());   // player list
        
        // Layers
        l = new GridBagConstraints();
        l.insets = new Insets(0, 0, 20, 0);
        l.fill = GridBagConstraints.NORTH; 
        l.gridwidth = 2;
        l.gridx = 0;
        l.gridy = 0;
        newGame.add(l1, l);

        l = new GridBagConstraints();
        l.insets = new Insets(0, 0, 0, 20);
        l.fill = GridBagConstraints.WEST;
        l.gridx = 0;
        l.gridy = 1;
        newGame.add(l2, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.EAST;
        l.gridx = 1;
        l.gridy = 1;
        newGame.add(l3, l);

        // Title
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 0;
        l1.add(title, l);

        // Round Selection
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridwidth = 2;
        l.gridx = 0;
        l.gridy = 0;
        l2.add(roundTitle, l);

        l = new GridBagConstraints();
        l.insets = new Insets(0, 0, 15, 0);
        l.fill = GridBagConstraints.CENTER;
        l.gridwidth = 2;
        l.gridx = 0;
        l.gridy = 1;
        l2.add(roundSelection, l);

        // Buttons
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 2;
        l2.add(addPlayer, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 1;
        l.gridy = 2;
        l2.add(remPlayer, l);

        l = new GridBagConstraints();
        l.insets = new Insets(15, 0, 0, 0);
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridwidth = 2;
        l.gridx = 0;
        l.gridy = 3;
        l2.add(begin, l);
        
        // Player table
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 2;
        l3.add(playerTable.getTableHeader(), l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 3;
        l3.add(playerTable, l);
    }

    /**
     * Builds the main game window when the start menu confirms
     * everything can begin
     * @see Wheel#getCost()
     */
    private void buildMainGame() {
        String[] nameListHeading = {"Players", "Coins"}; // String arrays to store into the table
        String[][] playerStatList = new String[MAX_PLAYERS][2];
        GridBagConstraints l;

        // Uses the .getCost() function of Wheel to properly display the button text
        spinGoodWheel.setText("Good Wheel ("+goodWheel.getCost()+")");
        spinGoodWheel.setPreferredSize(new Dimension(125, 25));
        spinGoodWheel.addActionListener(this);
        spinBadWheel.setText("Bad Wheel ("+badWheel.getCost()+")");
        spinBadWheel.setPreferredSize(new Dimension(125, 25));
        spinBadWheel.addActionListener(this);
        forfeitTurn.setText("Forfeit Turn ("+ROUND_FORFEIT+")");
        forfeitTurn.setPreferredSize(new Dimension(125, 25));
        forfeitTurn.addActionListener(this);

        // playerTableCoins stores the name + coin counts of each player
        playerTableCoins = new JTable(playerStatList, nameListHeading);
        playerTableCoins.setEnabled(false);
        TableColumnModel t = playerTableCoins.getColumnModel();
        t.getColumn(0).setPreferredWidth(150);
        t.getColumn(1).setPreferredWidth(50);

        flavorText.setFont(new Font("Times New Roman", Font.BOLD, 18));
        currentRound.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        // Set layers
        l1 = new JPanel(new GridBagLayout());   // flavor text
        l2 = new JPanel(new GridBagLayout());   // wheels
        l3 = new JPanel(new GridBagLayout());   // scores

        // Layers
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.NORTH;
        l.insets = new Insets(0, 0, 20, 0);
        l.gridwidth = 2;
        l.gridx = 0;
        l.gridy = 0;
        mainGame.add(l1, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.WEST;
        l.insets = new Insets(0, 0, 0, 20);
        l.gridx = 0;
        l.gridy = 1;
        mainGame.add(l2, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.EAST;
        l.gridx = 1;
        l.gridy = 1;
        mainGame.add(l3, l);

        // Flavor Text
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 0;
        l1.add(flavorText, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.CENTER;
        l.gridx = 0;
        l.gridy = 1;
        l1.add(currentRound, l);

        // Actions
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 0;
        l2.add(spinGoodWheel, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 1;
        l2.add(spinBadWheel, l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 2;
        l2.add(forfeitTurn, l);

        // Scores
        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 0;
        l3.add(playerTableCoins.getTableHeader(), l);

        l = new GridBagConstraints();
        l.fill = GridBagConstraints.HORIZONTAL;
        l.gridx = 0;
        l.gridy = 1;
        l3.add(playerTableCoins, l);
    }

    /**
     * Modifies the Player array to add or remove players from it.
     * @param name - player name
     * @param opCode - the operation code - is the player being added (1) or removed (2)?
     * @return a boolean to confirm if the player table was updated or not
     * @see Blitz#parsePlayerTables(String, int, int)
     */
    private boolean modPlayerList(String name, int opCode) {
        boolean hasListBeenModded = false; // boolean to return
        boolean canPlayerBeAdded = true; // boolean to check if the player can be added to the list
        
        // if a player is trying to be added...
        if (opCode == 1) {
            /* Check if the player name is empty
            if it isn't, then check if the player list already contains the player.
            if either check fails, then set canPlayerBeAdded to false */
            if (name.trim().isEmpty()) canPlayerBeAdded = false;
            else if (numPlayers != 0) {
                for (int i = 0; i < numPlayers; i++) {
                    if (name.equals(playerList.get(i).getName())) {
                        canPlayerBeAdded = false;
                        break;
                    }
                }
            }
            // If the player can be added, then attempt to add them; otherwise, deny the operation
            if (canPlayerBeAdded) {
                Player newPlayer = new Player(name);
                playerList.add(newPlayer);
                numPlayers++;
                parsePlayerTables(name, opCode, numPlayers);
                hasListBeenModded = true; // Operation was successful
            }
        // If a player is trying to be removed...
        } else if (opCode == 2) {
            // Check if the player exists in the list; if they do, then attempt to remove them
            for (int i = 0; i < numPlayers; i++) {
                if (name.equals(playerList.get(i).getName())) {
                    playerList.remove(i);
                    numPlayers--;
                    parsePlayerTables(name, opCode, i+1);
                    hasListBeenModded = true;
                    break;
                }
            }
        }

        // If it returns true, then the Player list was successfully adjusted
        return hasListBeenModded;
    }

    /**
     * Updates the player JTables to add or remove the names of players being
     * added/removed
     * @param name - name of the player
     * @param opCode - operation code; 1 for add, 2 for remove
     * @param playerNum - stores the index the player is to be added to/removed from
     */
    private void parsePlayerTables(String name, int opCode, int playerNum) {
        
        // If a player is being added...
        if (opCode == 1) {
            // set the values at index playerNum of the tables to the player name.
            playerTable.setValueAt(name, playerNum - 1, 0);
            playerTableCoins.setValueAt(name, playerNum - 1, 0);
        // If a player is being removed...
        } else if (opCode == 2) {
            // set the values at index playerNum of the tables to null.
            playerTable.setValueAt(null, playerNum - 1, 0);
            playerTableCoins.setValueAt(null, playerNum - 1, 0);

            // Once removed, check if the number of players is greater than 0. If not...
            if (numPlayers != 0) {
                // Iterate each filled index slot in the tables and update the names to remove any gaps in the list
                for (int i = playerNum - 1; i < numPlayers; i++) {
                    playerTable.setValueAt(playerTable.getValueAt(i+1, 0), i, 0);
                    playerTableCoins.setValueAt(playerTable.getValueAt(i+1, 0), i, 0);
                }
            }
            // Finally, if the number of players is not at maximum, set the last index value to null to remove dupes
            if (numPlayers != MAX_PLAYERS) {
                playerTable.setValueAt(null, numPlayers, 0);
                playerTableCoins.setValueAt(null, numPlayers, 0);
            }
        }
    }

    /**
     * Adjust the coins displayed for each player in playerTableCoins.
     * @see Player#getCoins()
     * @see Player#isCursed()
     * @see Player#getName()
     */
    private void adjustCoinCountTable() {
        String coins; // stores the coins of each player

        // iterate through the Player list and update each coin value in the table
        for (int i = 0; i < numPlayers; i++) {
            coins = Integer.toString(playerList.get(i).getCoins());
            if (!playerTableCoins.getValueAt(i, 1).equals(coins))
                playerTableCoins.setValueAt(coins, i, 1);

            // If a player is cursed, then additionally add (CURSED!) to their name in the table
            if (playerList.get(i).isCursed())
                playerTableCoins.setValueAt(playerList.get(i).getName()+" (CURSED!)", i, 0);

            // Failsafe as the cursed effect text is not updated anywhere else
            else playerTableCoins.setValueAt(playerList.get(i).getName(), i, 0);
        }
    }

    /**
     * Advances to the next player that has a turn, and advances
     * the round if all players had a turn.
     * Additionally has functionality for the activation of the
     * Chaos Wheel.
     * @see Blitz#winCondition()
     * @see Blitz#adjustCoinCountTable()
     * @see Blitz#advanceGameState()
     * @see Player#getName()
     * @see ChaosWheel#isExhausted()
     * @see ChaosWheel#spinChaosWheel(Player, Blitz, GoodWheel, BadWheel)
     */
    private void advanceGameState() {
        int chaosAdvance; // Determines if the Chaos Wheel must be spun
        
        // This function assumes a player has completed their turn, so the turn num always increments first
        turnNum++;

        // If the turn num is equal to the number of players, then the round is complete and advances to the next round
        if (turnNum == numPlayers) {
            roundNum++;
            if (roundNum == maxRounds + 1) winCondition(); // If the rounds exceed the max, the game is complete
            turnNum = 0;
            currentRound.setText("Current Round: "+roundNum+"/"+maxRounds);
        }
        // Once the turn logic is complete, use turnNum to parse the index of the next player to get a turn
        currentPlayer = playerList.get(turnNum);
        flavorText.setText("Spin a wheel if you dare, "+currentPlayer.getName()+"...");

        // Since the Chaos wheel requires no input, it is handled separately to the other 2 wheels
        chaosWheelRest = new Random(); // A number between 1 and the number of players/2 is chosen
        chaosAdvance = chaosWheelRest.nextInt(numPlayers/2)+1;

        /* If the number rolled is the maximum number that can be rolled 
        (i.e. 2 for a 4 player game, 4 for an 8 player) and the Chaos Wheel
        is not in its resting state, then the Chaos wheel must be spun for the next player */
        if (chaosAdvance == numPlayers/2 && !chaosWheel.isExhausted()) {
            chaosWheel.spinChaosWheel(currentPlayer, this, goodWheel, badWheel);
            adjustCoinCountTable(); // Performs all the logic for spinning a wheel, including recursively calling the function
            advanceGameState();
        }
    }

    /**
     * When all rounds are done, determine the winner of the game.
     * @see Player#getCoins()
     * @see Player#getName()
     * @see GoodWheel#spinGoodWheel(Player, Blitz)
     * @see Blitz#adjustCoinCountTable()
     */
    private void winCondition() {
        Player winner = playerList.get(0); // The first player in the Player list is assumed the winner initially

        // Iterates through the player list to determine who has higher coin counts
        for (Player win : playerList) {
            if (win.getCoins() > winner.getCoins()) winner = win;
        }

        // Iterate a second time to confirm any ties
        for (Player win : playerList) {

            // Tiebreak by forcing the competing winner to spin the good wheel
            if (win.getCoins() == winner.getCoins() && !win.getName().equals(winner.getName())) {
                JOptionPane.showMessageDialog(this, "Tied score by "+winner.getName()+" and "+win.getName()+"!"+
                                                   "\nTiebreak the winner by spinning the good wheel!");
                flavorText.setText("SUDDEN DEATH, SPIN THE WHEEL "+win.getName()+"!");
                currentRound.setText("Current Round: !!!");
                goodWheel.spinGoodWheel(win, this);
                
                // Display if the tiebreak succeeded or not
                if (win.getCoins() > winner.getCoins())
                    JOptionPane.showMessageDialog(this, "Tiebreak! "+win.getName()+" now has a higher coin count!");
                else JOptionPane.showMessageDialog(this, "Tiebreak failed "+win.getName()+"...sorry.");

                /* To take into account any unexpected outcomes (such as a losing player 
                suddenly gaining enought coins to win outright or compete for the win),
                the function is recursively called */ 
                winCondition();
            }
        }

        adjustCoinCountTable(); // So the winner's coins are properly displayed
        JOptionPane.showMessageDialog(this, winner.getName()+" has won the game of BLITZ! Congratulations!");
        System.exit(0);
    }
    
    @Override
    /**
     * The action event to handle any button usage.
     * @see Blitz#modPlayerList(String, int)
     * @see Blitz#advanceGameState()
     * @see Blitz#adjustCoinCountTable()
     * @see Player#getName()
     * @see Player#getCoins()
     * @see Player#modCoinCount(double, Blitz)
     * @see GoodWheel#spinGoodWheel(Player, Blitz)
     * @see BadWheel#spinBadWheel(Player, Blitz)
     * @see ChaosWheel#spinChaosWheel(Player, Blitz, GoodWheel, BadWheel)
     * @see ChaosWheel#rest()
     */
    public void actionPerformed(ActionEvent op) {
        JButton src = (JButton)op.getSource(); // Gets the operation button
        String name; // For when a player is being added/removed
        int chaosAdvance; // For when a player completes their turn

        // Operation 1: Adding a player
        if (src == addPlayer) {

            // First check if the player list is full
            if (numPlayers == MAX_PLAYERS) {
                JOptionPane.showMessageDialog(this, "No more players can be added.");
            } else {

                // Enter a name to be added; the null check is if the op is canceled
                name = JOptionPane.showInputDialog(this, "Enter player name");
                if (name != null) {

                    // Try to add the player, and notify if they were successfully added
                    if (modPlayerList(name, 1))
                        JOptionPane.showMessageDialog(this, "Player added!");
                    else JOptionPane.showMessageDialog(this, "Player can't be added.");
                }
            } 
        // Operation 2: Removing a player
        } else if (src == remPlayer) {

            // First check if there are even players to remove
            if (numPlayers == 0) {
                JOptionPane.showMessageDialog(this, "But...there aren't any players.");
            } else {

                // Enter a name to be removed; the null check is if the op is canceled
                name = JOptionPane.showInputDialog(this, "Enter player name");
                if (name != null) {

                    // Try to remove the player, and notify if they were successfully removed
                    if (modPlayerList(name, 2))
                        JOptionPane.showMessageDialog(this, "Player removed...");
                    else JOptionPane.showMessageDialog(this, "That player doesn't exist.");
                }
            }
        // Operation 3: Starting the game
        } else if (src == begin) { 
            maxRounds = (Integer)roundSelection.getValue(); // Get the desired round count from the JSpinner

            // First check if the game has the minimum amount of required players
            if (numPlayers < MIN_PLAYERS) {
                JOptionPane.showMessageDialog(this, "This game needs "+MIN_PLAYERS+" or more players to begin.");
            } else {

                /* Notify the number of rounds to be played and run logic to 
                switch from the start menu to the main game menu*/ 
                JOptionPane.showMessageDialog(this, "The game begins! (Play for "+maxRounds+" rounds)");
                newGame.setVisible(false);
                mainGame.setVisible(true);
                pack();
                setLocationRelativeTo(null);

                /* Initialize the coin counts on playerTableCoins (the names were 
                already handled by the parsePlayerTables function) */ 
                for (int i = 0; i < numPlayers; i++) {
                    playerTableCoins.setValueAt(Integer.toString(playerList.get(i).getCoins()), i, 1);
                }

                currentPlayer = playerList.get(turnNum); // As turnNum is initialized to 0, there is no need to run advanceGameState() yet
                flavorText.setText("Spin a wheel if you dare, "+currentPlayer.getName()+"...");
                currentRound.setText("Current Round: 1/"+maxRounds);
            }
        } else {
            // If the op is not any of the above, it's assumed to come from the main game menu

            // Operation 5: Spin the Good Wheel
            if (src == spinGoodWheel) {

                // If the player can't afford the cost, then notify; otherwise, let them spin it
                if (currentPlayer.getCoins() < Math.abs(goodWheel.getCost()))
                    JOptionPane.showMessageDialog(this, "You cannot afford to spin the good wheel "+currentPlayer.getName());
                else {
                    goodWheel.spinGoodWheel(currentPlayer, this);
                    adjustCoinCountTable();
                    advanceGameState();
                }
            // Operation 6 and 7: Spin the Bad Wheel or Forfeit the turn
            } else {

                // Let the player spin the Bad Wheel
                if (src == spinBadWheel) badWheel.spinBadWheel(currentPlayer, this);

                // If it wasn't the Bad Wheel, then the player is assumed to have forfeited their turn
                else {
                    JOptionPane.showMessageDialog(this, currentPlayer.getName()+" has forfeited their turn.");
                    currentPlayer.modCoinCount(ROUND_FORFEIT, this);
                }
                adjustCoinCountTable();
                advanceGameState();
            }
            
            // Logic that runs after any of the main game buttons were pressed

            chaosWheelRest = new Random(); // A number between 1 and 4 is chosen
            chaosAdvance = chaosWheelRest.nextInt(4)+1;

            /* If the number rolled is 4 and the Chaos Wheel is in its resting state, then the 
            Chaos wheel advances its resting state */
            if (chaosWheel.isExhausted() && chaosAdvance == 4) chaosWheel.rest(this);
        }
    }
}

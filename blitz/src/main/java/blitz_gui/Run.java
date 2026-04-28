package blitz_gui;

import javax.swing.*;

public class Run {

    private static Blitz gameWindow;

    /**
     * Runs the executable
     * @param args - command-line arguments, though it is unused
     * @see Blitz#buildBaseGUI()
     */
    public static void main(String[] args) {
        gameWindow = new Blitz();
        gameWindow.pack();
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setVisible(true);
        gameWindow.setLocationRelativeTo(null); // So it always centers on the screen
    }
}

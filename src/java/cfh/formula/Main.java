/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.formula;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import cfh.formula.gui.FormulaPanel;

/**
 * @author Carlos F. Heuberger, 2022-09-02
 *
 */
public class Main {
    
    public static void main(String... args) {
        SwingUtilities.invokeLater(Main::init);
    }
    
    private static void init(String... args) {
        var frame = new JFrame("Formula - " + FormulaPanel.VERSION);
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.add(new FormulaPanel(args));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

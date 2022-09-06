/*
 * Copyright: Carlos F. Heuberger. All rights reserved.
 *
 */
package cfh.formula.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import cfh.formula.expr.Interpreter;

/**
 * @author Carlos F. Heuberger, 2022-09-02
 *
 */
public class FormulaPanel extends JPanel {

    public static final String VERSION = "v 0.1 by Carlos F. Heuberger";
    
    private static final Font FONT = new Font("monospaced", Font.PLAIN, 14);
    
    private static final String PREF_FORMULA = "formula";
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private final JTextArea source;
    private final List<JButton> buttons;
    private final JTextArea output;
    
    private JDialog helpDialog = null;
    
    public FormulaPanel(String[] args) {
        var execute = newButton("Execute", this::doExecute, "<html>Execute the source text");
        var clear = newButton("Clear", this::doClear, "Clears the output test");
        var help = newButton("Help", this::doHelp, "<html>Show  short help dialog");
        buttons = List.of(execute, clear, help);
        
        var buttonBox = newHorizontalBox();
        buttonBox.add(buttonBox.createHorizontalStrut(16));
        buttonBox.add(buttonBox.createGlue());
        buttonBox.add(execute);
        buttonBox.add(buttonBox.createGlue());
        buttonBox.add(clear);
        buttonBox.add(buttonBox.createGlue());
        buttonBox.add(help);
        buttonBox.add(buttonBox.createHorizontalStrut(16));
        
        source = newTextArea(16, 100, prefs.get(PREF_FORMULA, ""), "<html>Source text");

        var sourcePanel = newScrollPane(source);
        
        output = newTextArea(16, 100, "", "<html>Output");
        output.setEditable(false);
        
        var outputPanel = newScrollPane(output);
        
        var split = newSplitPane(JSplitPane.VERTICAL_SPLIT, sourcePanel, outputPanel);
        
        setLayout(new BorderLayout());
        add(buttonBox, BorderLayout.PAGE_START);
        add(split, BorderLayout.CENTER);
    }

    private void doExecute(ActionEvent ev) {
        status(false);
        try {
            String text = source.getText();
            prefs.put(PREF_FORMULA, text);
            var interpreter = new Interpreter(this::printf);
            interpreter.execute(text);
        } finally {
            status(true);
        }
    }
    
    private void doClear(ActionEvent ev) {
        output.setText(null);
    }
    
    private void doHelp(ActionEvent ev) {
        if (true || helpDialog == null) {
            var helpArea = newTextArea(0, 0, Interpreter.HELP, "");
            helpArea.setEditable(false);
            
            helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this));
            helpDialog.setTitle("Help");
            helpDialog.setModalityType(ModalityType.MODELESS);
            helpDialog.add(newScrollPane(helpArea));
            helpDialog.pack();
        }
        helpDialog.setVisible(true);
    }
    
    private void status(boolean enable) {
        source.setEnabled(enable);
        buttons.forEach(b -> b.setEnabled(enable));
    }
    
    private void printf(String format, Object... args) {
        output.append(String.format(format, args));
        output.setCaretPosition(output.getText().length()); // TODO only if previously on end
    }

    private Box newHorizontalBox() {
        return Box.createHorizontalBox();
    }
    
    private JPanel newPanel(String title) {
        var panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }
    
    private JTextArea newTextArea(int rows, int cols, String text, String tooltip) {
        var area = new JTextArea(rows, cols);
        area.setText(text);
        area.setFont(FONT);
        area.setToolTipText(tooltip);
        return area;
    }
    
    private JButton newButton(String text, ActionListener action, String tooltip) {
        var button = new JButton(text);
        button.addActionListener(action);
        button.setToolTipText(tooltip);
        return button;
    }
    
    private JScrollPane newScrollPane(Component view) {
        var pane = new JScrollPane(view);
        pane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return pane;
    }
    
    private JSplitPane newSplitPane(int orientation, Component first, Component second) {
        var pane = new JSplitPane(orientation, first, second);
        return pane;
    }
}

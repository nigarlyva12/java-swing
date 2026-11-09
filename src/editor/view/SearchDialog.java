package editor.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Dialog "Suchen und Ersetzen".
 *
 * Nicht modal: Man kann weiter im Text arbeiten, während der Dialog offen ist.
 * Die Buttons haben hier keine Logik – die hängt der Controller an.
 */
public class SearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField searchField = new JTextField(20);
    private final JTextField replaceField = new JTextField(20);
    private final JCheckBox matchCaseBox = new JCheckBox("Groß-/Kleinschreibung beachten");
    private final JButton findButton = new JButton("Weitersuchen");
    private final JButton replaceButton = new JButton("Ersetzen");
    private final JButton replaceAllButton = new JButton("Alle ersetzen");
    private final JButton closeButton = new JButton("Schließen");

    public SearchDialog(JFrame owner) {
        super(owner, "Suchen und Ersetzen", false);

        // Links: Beschriftungen und Eingabefelder
        JPanel fields = new JPanel(new GridLayout(3, 2, 6, 6));
        fields.add(new JLabel("Suchen nach:"));
        fields.add(searchField);
        fields.add(new JLabel("Ersetzen durch:"));
        fields.add(replaceField);
        fields.add(new JLabel());
        fields.add(matchCaseBox);

        // Rechts: Buttons untereinander
        JPanel buttons = new JPanel(new GridLayout(4, 1, 6, 6));
        buttons.add(findButton);
        buttons.add(replaceButton);
        buttons.add(replaceAllButton);
        buttons.add(closeButton);

        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(fields, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.EAST);
        setContentPane(content);

        getRootPane().setDefaultButton(findButton); // Enter = Weitersuchen
        closeButton.addActionListener(e -> setVisible(false));
        // Escape schließt den Dialog
        getRootPane().registerKeyboardAction(e -> setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /** Öffnet den Dialog und setzt den Cursor ins Such- bzw. Ersetzen-Feld. */
    public void open(boolean focusReplace) {
        setVisible(true);
        JTextField field = focusReplace ? replaceField : searchField;
        field.requestFocusInWindow();
        field.selectAll();
    }

    public String getSearchText()    { return searchField.getText(); }
    public String getReplaceText()   { return replaceField.getText(); }
    public boolean isMatchCase()     { return matchCaseBox.isSelected(); }
    public JButton getFindButton()       { return findButton; }
    public JButton getReplaceButton()    { return replaceButton; }
    public JButton getReplaceAllButton() { return replaceAllButton; }
}

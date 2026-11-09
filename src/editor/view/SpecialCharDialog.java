package editor.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Sonderzeichen-Tabelle: ein Button pro Zeichen.
 * Ein Klick fügt das Zeichen an der Cursorposition ein (Logik im Controller).
 */
public class SpecialCharDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /** Alle Zeichen der Tabelle (Umlaute, Akzente für die Übersetzer, Währungen, Symbole ...). */
    public static final String CHARACTERS =
            "äöüÄÖÜßéèêëàâáçñ" +
            "ôóòúùûíìîïøåæœ¿¡" +
            "€£¥$¢©®™§¶°±×÷≠≈" +
            "½¼¾²³µ«»„“”‚‘’–—" +
            "…•←→↑↓✓✗∞√πΩαβ∑#";

    private final List<JButton> buttons = new ArrayList<>();

    public SpecialCharDialog(JFrame owner) {
        super(owner, "Sonderzeichen einfügen", false);

        JPanel grid = new JPanel(new GridLayout(0, 16, 3, 3));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (char c : CHARACTERS.toCharArray()) {
            JButton button = new JButton(String.valueOf(c));
            button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
            button.setMargin(new Insets(4, 4, 4, 4));
            button.setFocusable(false); // Fokus bleibt im Textfeld
            buttons.add(button);
            grid.add(button);
        }

        setContentPane(grid);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public List<JButton> getButtons() {
        return buttons;
    }
}

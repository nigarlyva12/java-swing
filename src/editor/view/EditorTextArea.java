package editor.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import editor.model.EditorModel;

/**
 * Textfeld des Editors.
 *
 * Erweitert JTextArea um
 * - eine rote, senkrechte Linie ab Spalte 60
 * - eine gelbe Markierung für Suchtreffer
 */
public class EditorTextArea extends JTextArea {

    private static final long serialVersionUID = 1L;

    private final Highlighter.HighlightPainter yellow =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(0xFDE047));
    private Object searchMark; // aktuell markierter Suchtreffer (oder null)

    public EditorTextArea() {
        // Schrift mit fester Zeichenbreite, damit jede Spalte gleich breit ist
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        setMargin(new Insets(4, 6, 4, 6));
        setTabSize(4);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Breite von 59 Zeichen messen -> die Linie steht genau links vor Spalte 60.
        // (Ganze Zeichenkette messen, weil ein Zeichen oft "krumme" Pixelbreiten hat.)
        String before = "m".repeat(EditorModel.MARK_COLUMN - 1);
        int x = getInsets().left + (int) Math.round(
                getFont().getStringBounds(before, ((Graphics2D) g).getFontRenderContext()).getWidth());
        g.setColor(Color.RED);
        g.fillRect(x, 0, 2, getHeight());
    }

    /** Markiert den Bereich start..end gelb (bleibt sichtbar, auch wenn der Suchdialog den Fokus hat). */
    public void markText(int start, int end) {
        clearMark();
        try {
            searchMark = getHighlighter().addHighlight(start, end, yellow);
        } catch (BadLocationException e) {
            searchMark = null;
        }
    }

    /** Entfernt die gelbe Markierung. */
    public void clearMark() {
        if (searchMark != null) {
            getHighlighter().removeHighlight(searchMark);
            searchMark = null;
        }
    }
}

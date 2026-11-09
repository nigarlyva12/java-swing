package editor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.model.EditorModel;

/**
 * VIEW (das "V" in MVC).
 *
 * Baut das Fenster auf: Menüleiste, Textfeld und Statusleiste
 * (Meldungsfeld, Zeile, Spalte, Logo unten rechts).
 * Die View enthält keine Programmlogik. Sie bietet nur Getter für ihre
 * Bedienelemente und Hilfsmethoden für Dialoge an. Was beim Klicken passiert,
 * legt der Controller fest.
 */
public class EditorView extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int CTRL = InputEvent.CTRL_DOWN_MASK;
    private static final int SHIFT = InputEvent.SHIFT_DOWN_MASK;

    private final EditorTextArea textArea = new EditorTextArea();

    // Statusleiste
    private final JTextField messageField = new JTextField();
    private final JTextField lineField = new JTextField("1", 4);
    private final JTextField columnField = new JTextField("1", 4);

    // Dialoge
    private final SearchDialog searchDialog = new SearchDialog(this);
    private final SpecialCharDialog specialCharDialog = new SpecialCharDialog(this);

    // Menü "Datei"
    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;
    // Menü "Bearbeiten"
    private JMenuItem undoItem, redoItem, cutItem, copyItem, pasteItem, selectAllItem;
    private JMenuItem findItem, findNextItem, replaceItem, goToItem;
    // Menü "Sonderzeichen"
    private JMenuItem charTableItem;
    private final List<JMenuItem> quickCharItems = new ArrayList<>();
    // Menü "Extras" (Zusatzfunktion)
    private JMenuItem statisticsItem;
    // Menü "Hilfe"
    private JMenuItem manualItem, featureItem, aboutItem;

    public EditorView() {
        setIconImages(LogoPanel.createIcons());
        setJMenuBar(createMenuBar());

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        // Schließen über das X wird vom Controller behandelt (Nachfrage "Speichern?")
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    // Aufbau der Oberfläche
    // ------------------------------------------------------------------

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("Datei");
        newItem    = addItem(file, "Neu", KeyStroke.getKeyStroke(KeyEvent.VK_N, CTRL));
        openItem   = addItem(file, "Öffnen ...", KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL));
        saveItem   = addItem(file, "Speichern", KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL));
        saveAsItem = addItem(file, "Speichern unter ...", KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL | SHIFT));
        file.addSeparator();
        exitItem   = addItem(file, "Beenden", KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL));

        JMenu edit = new JMenu("Bearbeiten");
        undoItem      = addItem(edit, "Rückgängig", KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL));
        redoItem      = addItem(edit, "Wiederholen", KeyStroke.getKeyStroke(KeyEvent.VK_Y, CTRL));
        edit.addSeparator();
        cutItem       = addItem(edit, "Ausschneiden", KeyStroke.getKeyStroke(KeyEvent.VK_X, CTRL));
        copyItem      = addItem(edit, "Kopieren", KeyStroke.getKeyStroke(KeyEvent.VK_C, CTRL));
        pasteItem     = addItem(edit, "Einfügen", KeyStroke.getKeyStroke(KeyEvent.VK_V, CTRL));
        selectAllItem = addItem(edit, "Alles markieren", KeyStroke.getKeyStroke(KeyEvent.VK_A, CTRL));
        edit.addSeparator();
        findItem      = addItem(edit, "Suchen ...", KeyStroke.getKeyStroke(KeyEvent.VK_F, CTRL));
        findNextItem  = addItem(edit, "Weitersuchen", KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        replaceItem   = addItem(edit, "Ersetzen ...", KeyStroke.getKeyStroke(KeyEvent.VK_H, CTRL));
        goToItem      = addItem(edit, "Gehe zu Zeile ...", KeyStroke.getKeyStroke(KeyEvent.VK_G, CTRL));

        JMenu special = new JMenu("Sonderzeichen");
        charTableItem = addItem(special, "Sonderzeichen-Tabelle ...", KeyStroke.getKeyStroke(KeyEvent.VK_T, CTRL));
        special.addSeparator();
        for (char c : "äöüÄÖÜß€©§°„“".toCharArray()) {   // häufige Zeichen direkt im Menü
            quickCharItems.add(addItem(special, String.valueOf(c), null));
        }

        JMenu extras = new JMenu("Extras");
        statisticsItem = addItem(extras, "Textstatistik", KeyStroke.getKeyStroke(KeyEvent.VK_I, CTRL));

        JMenu help = new JMenu("Hilfe");
        manualItem  = addItem(help, "Bedienung und Tastenkürzel", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        featureItem = addItem(help, "Zusatzfunktion: Textstatistik", null);
        help.addSeparator();
        aboutItem   = addItem(help, "Über " + EditorModel.APP_NAME, null);

        bar.add(file);
        bar.add(edit);
        bar.add(special);
        bar.add(extras);
        bar.add(help);
        return bar;
    }

    /** Hilfsmethode: Menüeintrag anlegen, Tastenkürzel setzen und ins Menü hängen. */
    private JMenuItem addItem(JMenu menu, String text, KeyStroke shortcut) {
        JMenuItem item = new JMenuItem(text);
        if (shortcut != null) {
            item.setAccelerator(shortcut);
        }
        menu.add(item);
        return item;
    }

    private JPanel createStatusBar() {
        messageField.setEditable(false); // Meldungsfeld ist nicht beschreibbar
        messageField.setText("Bereit");

        JPanel position = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        position.add(new JLabel("Zeile:"));
        position.add(lineField);
        position.add(new JLabel("Spalte:"));
        position.add(columnField);
        position.add(new LogoPanel(40)); // Logo unten rechts

        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        bar.add(messageField, BorderLayout.CENTER);
        bar.add(position, BorderLayout.EAST);
        return bar;
    }

    // ------------------------------------------------------------------
    // Dialoge und Meldungen
    // ------------------------------------------------------------------

    /** Schreibt eine Meldung ins Meldungsfeld der Statusleiste. */
    public void showMessage(String message) {
        messageField.setText(message);
    }

    public void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    /** Fragt, ob ungespeicherte Änderungen gespeichert werden sollen (JA / NEIN / ABBRECHEN). */
    public int askSaveChanges() {
        return JOptionPane.showConfirmDialog(this,
                "Das Dokument wurde geändert.\nMöchten Sie die Änderungen speichern?",
                "Ungespeicherte Änderungen", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public boolean askYesNo(String title, String question) {
        return JOptionPane.showConfirmDialog(this, question, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Dateiauswahl, voreingestellt auf .txt, .csv und .java. */
    private JFileChooser createFileChooser(File currentFile) {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileFilter(new FileNameExtensionFilter("Textdateien (*.txt, *.csv, *.java)", "txt", "csv", "java"));
        if (currentFile != null) {
            chooser.setSelectedFile(currentFile);
        }
        return chooser;
    }

    /** @return gewählte Datei oder null bei Abbruch */
    public File chooseOpenFile() {
        JFileChooser chooser = createFileChooser(null);
        return chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    /** @return gewählte Datei oder null bei Abbruch */
    public File chooseSaveFile(File currentFile) {
        JFileChooser chooser = createFileChooser(currentFile);
        return chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public void showManual() {
        showInfo("Bedienung und Tastenkürzel",
                "DATEI\n"
              + "  Strg+N  Neu          Strg+O  Öffnen\n"
              + "  Strg+S  Speichern    Strg+Umschalt+S  Speichern unter\n"
              + "  Strg+Q  Beenden\n\n"
              + "BEARBEITEN\n"
              + "  Strg+Z  Rückgängig   Strg+Y  Wiederholen\n"
              + "  Strg+X / C / V  Ausschneiden / Kopieren / Einfügen\n"
              + "  Strg+A  Alles markieren\n\n"
              + "SUCHEN\n"
              + "  Strg+F  Suchen       F3  Weitersuchen\n"
              + "  Strg+H  Ersetzen     Strg+G  Gehe zu Zeile\n"
              + "  Die Suche beginnt immer an der Cursorposition.\n\n"
              + "WEITERES\n"
              + "  Strg+T  Sonderzeichen-Tabelle\n"
              + "  Strg+I  Textstatistik\n\n"
              + "Zeile und Spalte unten rechts zeigen die Cursorposition.\n"
              + "Man kann dort auch Zahlen eingeben und mit Enter hinspringen.\n"
              + "Die rote Linie markiert Spalte " + EditorModel.MARK_COLUMN + ".");
    }

    public void showFeatureHelp() {
        showInfo("Zusatzfunktion: Textstatistik",
                "Die Textstatistik (Menü Extras oder Strg+I) zeigt auf einen Blick:\n\n"
              + "  • Anzahl der Wörter\n"
              + "  • Zeichen mit und ohne Leerzeichen\n"
              + "  • Anzahl der Zeilen und die längste Zeile\n"
              + "  • wie viele Zeilen über die rote Linie (Spalte " + EditorModel.MARK_COLUMN + ") hinausgehen\n"
              + "  • geschätzte Lesezeit\n\n"
              + "Nutzen für die Übersetzungsabteilung:\n"
              + "Übersetzungen werden häufig nach Wörtern oder Zeichen abgerechnet\n"
              + "und müssen oft eine maximale Zeilenlänge einhalten. Beides lässt sich\n"
              + "damit sofort prüfen, ohne ein zusätzliches Programm zu benötigen.");
    }

    public void showAbout() {
        String text = EditorModel.APP_NAME + "  Version " + EditorModel.VERSION + "\n"
                + "Texteditor für die Abteilung Übersetzung\n"
                + "We-Do-IT GmbH\n\n"
                + "© " + EditorModel.YEAR + " " + EditorModel.AUTHOR + "\n"
                + "Klasse: " + EditorModel.SCHOOL_CLASS;
        JOptionPane.showMessageDialog(this, text, "Über " + EditorModel.APP_NAME,
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(LogoPanel.createImage(96)));
    }

    // ------------------------------------------------------------------
    // Getter für den Controller
    // ------------------------------------------------------------------

    public EditorTextArea getTextArea()             { return textArea; }
    public JTextField getLineField()                { return lineField; }
    public JTextField getColumnField()              { return columnField; }
    public SearchDialog getSearchDialog()           { return searchDialog; }
    public SpecialCharDialog getSpecialCharDialog() { return specialCharDialog; }

    public JMenuItem getNewItem()        { return newItem; }
    public JMenuItem getOpenItem()       { return openItem; }
    public JMenuItem getSaveItem()       { return saveItem; }
    public JMenuItem getSaveAsItem()     { return saveAsItem; }
    public JMenuItem getExitItem()       { return exitItem; }
    public JMenuItem getUndoItem()       { return undoItem; }
    public JMenuItem getRedoItem()       { return redoItem; }
    public JMenuItem getCutItem()        { return cutItem; }
    public JMenuItem getCopyItem()       { return copyItem; }
    public JMenuItem getPasteItem()      { return pasteItem; }
    public JMenuItem getSelectAllItem()  { return selectAllItem; }
    public JMenuItem getFindItem()       { return findItem; }
    public JMenuItem getFindNextItem()   { return findNextItem; }
    public JMenuItem getReplaceItem()    { return replaceItem; }
    public JMenuItem getGoToItem()       { return goToItem; }
    public JMenuItem getCharTableItem()  { return charTableItem; }
    public List<JMenuItem> getQuickCharItems() { return quickCharItems; }
    public JMenuItem getStatisticsItem() { return statisticsItem; }
    public JMenuItem getManualItem()     { return manualItem; }
    public JMenuItem getFeatureItem()    { return featureItem; }
    public JMenuItem getAboutItem()      { return aboutItem; }
}

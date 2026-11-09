package editor.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

import editor.model.EditorModel;
import editor.view.EditorTextArea;
import editor.view.EditorView;
import editor.view.SearchDialog;

/**
 * CONTROLLER (das "C" in MVC).
 *
 * Verbindet View und Model: Er hängt an alle Menüpunkte und Buttons der View
 * die passenden Aktionen, ruft dafür das Model auf und aktualisiert
 * anschließend die View (Titel, Meldungsfeld, Zeile/Spalte ...).
 */
public class EditorController {

    private final EditorModel model;
    private final EditorView view;
    private final EditorTextArea textArea;
    private final UndoManager undoManager = new UndoManager();

    /** true, während der Controller selbst Text setzt (zählt nicht als Änderung durch den Benutzer). */
    private boolean loading = false;

    public EditorController(EditorModel model, EditorView view) {
        this.model = model;
        this.view = view;
        this.textArea = view.getTextArea();
        registerListeners();
        updateTitle();
    }

    // ------------------------------------------------------------------
    // Listener anmelden
    // ------------------------------------------------------------------

    private void registerListeners() {
        // Datei
        view.getNewItem().addActionListener(e -> newFile());
        view.getOpenItem().addActionListener(e -> openFile());
        view.getSaveItem().addActionListener(e -> save());
        view.getSaveAsItem().addActionListener(e -> saveAs());
        view.getExitItem().addActionListener(e -> exit());
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit(); // X-Button oben rechts
            }
        });

        // Bearbeiten
        view.getUndoItem().addActionListener(e -> { if (undoManager.canUndo()) undoManager.undo(); });
        view.getRedoItem().addActionListener(e -> { if (undoManager.canRedo()) undoManager.redo(); });
        view.getCutItem().addActionListener(e -> textArea.cut());
        view.getCopyItem().addActionListener(e -> textArea.copy());
        view.getPasteItem().addActionListener(e -> textArea.paste());
        view.getSelectAllItem().addActionListener(e -> textArea.selectAll());
        view.getGoToItem().addActionListener(e -> {
            view.getLineField().requestFocusInWindow();
            view.getLineField().selectAll();
        });

        // Suchen und Ersetzen
        SearchDialog dialog = view.getSearchDialog();
        view.getFindItem().addActionListener(e -> dialog.open(false));
        view.getReplaceItem().addActionListener(e -> dialog.open(true));
        view.getFindNextItem().addActionListener(e -> findNext());
        dialog.getFindButton().addActionListener(e -> findNext());
        dialog.getReplaceButton().addActionListener(e -> replace());
        dialog.getReplaceAllButton().addActionListener(e -> replaceAll());

        // Sonderzeichen: Text des Buttons / Menüeintrags wird eingefügt
        view.getCharTableItem().addActionListener(e -> view.getSpecialCharDialog().setVisible(true));
        for (JButton button : view.getSpecialCharDialog().getButtons()) {
            button.addActionListener(e -> insertText(button.getText()));
        }
        for (JMenuItem item : view.getQuickCharItems()) {
            item.addActionListener(e -> insertText(item.getText()));
        }

        // Extras und Hilfe
        view.getStatisticsItem().addActionListener(e ->
                view.showInfo("Textstatistik", model.getStatistics(textArea.getText())));
        view.getManualItem().addActionListener(e -> view.showManual());
        view.getFeatureItem().addActionListener(e -> view.showFeatureHelp());
        view.getAboutItem().addActionListener(e -> view.showAbout());

        // Zeile / Spalte: Anzeige bei jeder Cursorbewegung, Eingabe mit Enter
        textArea.addCaretListener(e -> updatePosition());
        view.getLineField().addActionListener(e -> goToPosition());
        view.getColumnField().addActionListener(e -> goToPosition());

        // Jede Textänderung merken (für Titel-Stern und "Speichern?"-Nachfrage)
        textArea.getDocument().addUndoableEditListener(undoManager);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { textChanged(); }
            @Override public void removeUpdate(DocumentEvent e)  { textChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { /* nur Formatierung, unwichtig */ }
        });
    }

    // ------------------------------------------------------------------
    // Datei-Funktionen
    // ------------------------------------------------------------------

    private void newFile() {
        if (!askToSave()) {
            return;
        }
        model.newDocument();
        setDocumentText("");
        view.showMessage("Neues Dokument erstellt");
    }

    private void openFile() {
        if (!askToSave()) {
            return;
        }
        File file = view.chooseOpenFile();
        if (file == null) {
            return; // abgebrochen
        }
        try {
            String text = model.load(file);
            setDocumentText(text);
            view.showMessage("Geöffnet: " + file.getName() + "  (" + model.getFileSize() + " Bytes)");
        } catch (IOException e) {
            view.showMessage("FEHLER: " + file.getName() + " konnte nicht geöffnet werden");
            view.showError("Die Datei konnte nicht geöffnet werden:\n" + e.getMessage());
        }
    }

    /** Speichert in die aktuelle Datei, bei neuem Dokument wird nach dem Namen gefragt. */
    private boolean save() {
        if (model.getCurrentFile() == null) {
            return saveAs();
        }
        return writeFile(model.getCurrentFile());
    }

    private boolean saveAs() {
        File file = view.chooseSaveFile(model.getCurrentFile());
        if (file == null) {
            return false; // abgebrochen
        }
        file = model.ensureExtension(file);
        if (file.exists() && !file.equals(model.getCurrentFile())
                && !view.askYesNo("Speichern unter", file.getName() + " existiert bereits. Überschreiben?")) {
            return false;
        }
        return writeFile(file);
    }

    private boolean writeFile(File file) {
        try {
            model.save(file, textArea.getText());
            updateTitle();
            view.showMessage("Erfolgreich gespeichert: " + file.getName() + "  (" + model.getFileSize() + " Bytes)");
            return true;
        } catch (IOException e) {
            view.showMessage("FEHLER: " + file.getName() + " konnte nicht gespeichert werden");
            view.showError("Die Datei konnte nicht gespeichert werden:\n" + e.getMessage());
            return false;
        }
    }

    private void exit() {
        if (askToSave()) {
            view.dispose();
            System.exit(0);
        }
    }

    /**
     * Fragt bei ungespeicherten Änderungen, ob gespeichert werden soll.
     * @return true = weitermachen, false = Aktion abbrechen
     */
    private boolean askToSave() {
        if (!model.isModified()) {
            return true;
        }
        int answer = view.askSaveChanges();
        if (answer == JOptionPane.YES_OPTION) {
            return save();
        }
        return answer == JOptionPane.NO_OPTION;
    }

    /** Ersetzt den gesamten Text (z. B. nach dem Öffnen), ohne dass es als Änderung zählt. */
    private void setDocumentText(String text) {
        loading = true;
        textArea.setText(text);
        loading = false;
        textArea.setCaretPosition(0);
        textArea.clearMark();
        undoManager.discardAllEdits();
        model.setModified(false);
        updateTitle();
    }

    private void textChanged() {
        if (!loading && !model.isModified()) {
            model.setModified(true);
            updateTitle();
        }
    }

    private void updateTitle() {
        view.setTitle(model.getTitle());
    }

    private void insertText(String text) {
        textArea.replaceSelection(text);
        textArea.requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // Zeile und Spalte
    // ------------------------------------------------------------------

    /** Zeigt die Cursorposition (Zeile und Spalte, ab 1 gezählt) in der Statusleiste an. */
    private void updatePosition() {
        try {
            int caret = textArea.getCaretPosition();
            int line = textArea.getLineOfOffset(caret);
            int column = caret - textArea.getLineStartOffset(line);
            view.getLineField().setText(String.valueOf(line + 1));
            view.getColumnField().setText(String.valueOf(column + 1));
        } catch (BadLocationException e) {
            // kann bei gültiger Cursorposition nicht passieren
        }
    }

    /** Springt zu der Zeile und Spalte, die der Benutzer eingegeben hat. */
    private void goToPosition() {
        try {
            int line = Integer.parseInt(view.getLineField().getText().trim());
            int column = Integer.parseInt(view.getColumnField().getText().trim());

            // Werte auf gültigen Bereich begrenzen
            line = Math.max(1, Math.min(line, textArea.getLineCount()));
            int start = textArea.getLineStartOffset(line - 1);
            int end = textArea.getLineEndOffset(line - 1);
            int lineLength = end - start;
            if (line < textArea.getLineCount()) {
                lineLength--; // Zeilenumbruch nicht mitzählen
            }
            column = Math.max(1, Math.min(column, lineLength + 1));

            textArea.setCaretPosition(start + column - 1);
            textArea.requestFocusInWindow();
        } catch (NumberFormatException e) {
            view.showMessage("Bitte bei Zeile und Spalte nur Zahlen eingeben");
        } catch (BadLocationException e) {
            view.showMessage("Ungültige Position");
        }
    }

    // ------------------------------------------------------------------
    // Suchen und Ersetzen
    // ------------------------------------------------------------------

    /** Sucht ab dem Cursor den nächsten Treffer; am Ende wird angeboten, von vorne zu suchen. */
    private void findNext() {
        SearchDialog dialog = view.getSearchDialog();
        String query = dialog.getSearchText();
        if (query.isEmpty()) {
            dialog.open(false);
            return;
        }
        String text = textArea.getText();
        int from = textArea.getCaretPosition();
        int index = model.find(text, query, from, dialog.isMatchCase());

        if (index < 0 && from > 0
                && view.askYesNo("Suchen", "Ende des Textes erreicht.\nVon vorne weitersuchen?")) {
            index = model.find(text, query, 0, dialog.isMatchCase());
        }
        if (index < 0) {
            textArea.clearMark();
            view.showMessage("\"" + query + "\" wurde nicht gefunden");
            view.showInfo("Suchen", "\"" + query + "\" wurde nicht gefunden.");
            return;
        }
        // Treffer markieren; der Cursor steht danach hinter dem Treffer -> nächste Suche geht weiter
        textArea.markText(index, index + query.length());
        textArea.setCaretPosition(index);
        textArea.moveCaretPosition(index + query.length());
        view.showMessage("\"" + query + "\" gefunden in Zeile " + view.getLineField().getText());
    }

    /** Ersetzt den nächsten Treffer ab dem Cursor (bzw. den gerade markierten Treffer). */
    private void replace() {
        SearchDialog dialog = view.getSearchDialog();
        String query = dialog.getSearchText();
        String replacement = dialog.getReplaceText();
        // Anfang der Markierung, damit ein gerade gefundener Treffer selbst ersetzt wird
        int from = textArea.getSelectionStart();
        int index = model.find(textArea.getText(), query, from, dialog.isMatchCase());

        if (index < 0) {
            view.showMessage("\"" + query + "\" ab Cursor nicht gefunden");
            view.showInfo("Ersetzen", "\"" + query + "\" wurde ab der Cursorposition nicht gefunden.");
            return;
        }
        textArea.replaceRange(replacement, index, index + query.length());
        textArea.setCaretPosition(index + replacement.length());
        textArea.markText(index, index + replacement.length());
        view.showMessage("Ersetzt in Zeile " + view.getLineField().getText());
    }

    /** Ersetzt alle Treffer ab dem Cursor bis zum Textende. */
    private void replaceAll() {
        SearchDialog dialog = view.getSearchDialog();
        String query = dialog.getSearchText();
        String replacement = dialog.getReplaceText();
        int position = textArea.getSelectionStart();
        int count = 0;

        int index;
        while ((index = model.find(textArea.getText(), query, position, dialog.isMatchCase())) >= 0) {
            textArea.replaceRange(replacement, index, index + query.length());
            position = index + replacement.length();
            count++;
        }
        textArea.clearMark();
        if (count == 0) {
            view.showMessage("\"" + query + "\" ab Cursor nicht gefunden");
            view.showInfo("Ersetzen", "\"" + query + "\" wurde ab der Cursorposition nicht gefunden.");
        } else {
            view.showMessage(count + " Stelle(n) ersetzt");
        }
    }
}

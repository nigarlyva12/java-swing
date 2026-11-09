package editor.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * MODEL (das "M" in MVC).
 *
 * Enthält die Daten und die Logik des Editors – ganz ohne Swing.
 * - welche Datei gerade offen ist und ob sie geändert wurde
 * - Laden und Speichern von Dateien
 * - Suchen im Text (mit / ohne Groß- und Kleinschreibung)
 * - Berechnung der Textstatistik (Zusatzfunktion)
 *
 * Das Model kennt weder View noch Controller.
 */
public class EditorModel {

    // ---- Programminformationen (Copyright) ----
    public static final String APP_NAME = "WeDoText";
    public static final String VERSION = "1.0";
    public static final String AUTHOR = "Nigar Aliyeva";
    public static final String SCHOOL_CLASS = "ITO6";
    public static final int YEAR = 2026;

    /** Ab dieser Spalte wird im Textfeld die rote Linie gezeichnet. */
    public static final int MARK_COLUMN = 60;

    private File currentFile;   // null = noch nie gespeichert ("Unbenannt")
    private boolean modified;   // true = ungespeicherte Änderungen vorhanden

    // ------------------------------------------------------------------
    // Datei-Zustand
    // ------------------------------------------------------------------

    public File getCurrentFile() {
        return currentFile;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /** Setzt den Zustand für ein neues, leeres Dokument zurück. */
    public void newDocument() {
        currentFile = null;
        modified = false;
    }

    /** Fenstertitel, z. B. "*brief.txt - WeDoText" (Stern = ungespeichert). */
    public String getTitle() {
        String name = (currentFile == null) ? "Unbenannt" : currentFile.getName();
        return (modified ? "*" : "") + name + " - " + APP_NAME;
    }

    /** Größe der aktuellen Datei auf der Festplatte in Bytes. */
    public long getFileSize() {
        return (currentFile == null) ? 0 : currentFile.length();
    }

    // ------------------------------------------------------------------
    // Laden und Speichern
    // ------------------------------------------------------------------

    /** Liest eine Datei (UTF-8) und gibt ihren Inhalt zurück. */
    public String load(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        currentFile = file;
        modified = false;
        // Windows-Zeilenumbrüche (\r\n) in \n umwandeln, damit das Textfeld sie richtig anzeigt
        return new String(bytes, StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    /** Schreibt den Text (UTF-8) in die Datei. */
    public void save(File file, String text) throws IOException {
        String content = text.replace("\n", System.lineSeparator());
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
        currentFile = file;
        modified = false;
    }

    /** Hängt ".txt" an, wenn der Benutzer keine Dateiendung angegeben hat. */
    public File ensureExtension(File file) {
        if (file.getName().contains(".")) {
            return file;
        }
        return new File(file.getPath() + ".txt");
    }

    // ------------------------------------------------------------------
    // Suchen
    // ------------------------------------------------------------------

    /**
     * Sucht {@code query} in {@code text} ab Position {@code from}.
     *
     * @param matchCase true = Groß-/Kleinschreibung beachten
     * @return Position des Fundes oder -1, wenn nichts gefunden wurde
     */
    public int find(String text, String query, int from, boolean matchCase) {
        if (query.isEmpty()) {
            return -1;
        }
        for (int i = Math.max(0, from); i <= text.length() - query.length(); i++) {
            // regionMatches vergleicht ein Textstück; der erste Parameter = Groß/Klein ignorieren
            if (text.regionMatches(!matchCase, i, query, 0, query.length())) {
                return i;
            }
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // Zusatzfunktion: Textstatistik
    // ------------------------------------------------------------------

    /** Erstellt eine Statistik über den Text (Wörter, Zeichen, Zeilen ...). */
    public String getStatistics(String text) {
        String[] lines = text.split("\n", -1);

        int longestLine = 0;
        int linesOverMark = 0;
        for (String line : lines) {
            longestLine = Math.max(longestLine, line.length());
            if (line.length() >= MARK_COLUMN) {
                linesOverMark++;
            }
        }

        String trimmed = text.trim();
        int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        int chars = text.replace("\n", "").length();
        int charsNoSpaces = text.replaceAll("\\s", "").length();
        int readingMinutes = (int) Math.ceil(words / 200.0); // ca. 200 Wörter pro Minute

        return "Wörter:  " + words + "\n"
             + "Zeichen (mit Leerzeichen):  " + chars + "\n"
             + "Zeichen (ohne Leerzeichen):  " + charsNoSpaces + "\n"
             + "Zeilen:  " + lines.length + "\n"
             + "Längste Zeile:  " + longestLine + " Zeichen\n"
             + "Zeilen über der roten Linie (ab Spalte " + MARK_COLUMN + "):  " + linesOverMark + "\n"
             + "Geschätzte Lesezeit:  " + readingMinutes + " Min.";
    }
}

package editor;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import editor.controller.EditorController;
import editor.model.EditorModel;
import editor.view.EditorView;

/**
 * Startpunkt des Programms.
 * Erzeugt Model, View und Controller und verbindet sie miteinander (MVC).
 */
public class Main {

    public static void main(String[] args) {
        // Swing-Oberflächen müssen im Event-Dispatch-Thread erstellt werden
        SwingUtilities.invokeLater(() -> {
            try {
                // Aussehen an das Betriebssystem (Windows) anpassen
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Standard-Aussehen von Swing verwenden
            }

            EditorModel model = new EditorModel();
            EditorView view = new EditorView();
            new EditorController(model, view);
            view.setVisible(true);
        });
    }
}

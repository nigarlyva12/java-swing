package TextEditor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EditorPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	
	EditorPanel(){
		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		setLayout(new BorderLayout());
		
		add(scrollPane, BorderLayout.CENTER);
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}
	
}

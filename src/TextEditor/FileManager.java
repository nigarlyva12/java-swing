package TextEditor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

public class FileManager {
	private JTextArea textArea;
	
	FileManager(JTextArea textArea){
		this.textArea = textArea;
	}

	public void save() throws IOException {
		File currentFile = new File("rand.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
	
		writer.write(textArea.getText());
		writer.close();
	}
}

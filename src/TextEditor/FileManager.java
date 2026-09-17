package TextEditor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileManager {
	private JTextArea textArea;
	private  File currentFile;
	
	FileManager(JTextArea textArea){
		this.textArea = textArea;
	}
	
	public void save() throws IOException {
		
		if(currentFile == null) {
			JFileChooser fc = new JFileChooser("C:\\Users\\");
			int result = fc.showSaveDialog(null);
		
	        if(result == JFileChooser.APPROVE_OPTION) {
	        	currentFile = fc.getSelectedFile();
	        }else {
	        	return;
	        }
        }
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));

        writer.write(textArea.getText());
        writer.close();
    } 
	
	public void saveAs() throws IOException {

		JFileChooser fc = new JFileChooser(new File("C:\\Users\\"));
		FileNameExtensionFilter restrict = new FileNameExtensionFilter("Text & CSV & Java Files", "txt", "csv", "java");
		fc.setFileFilter(restrict);
		
		int result = fc.showSaveDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION) 
			currentFile = fc.getSelectedFile();
		else 
			return;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
		writer.write(textArea.getText());
		writer.close();
	}
	
	public void open() throws IOException {
		JFileChooser fc = new JFileChooser(new File("C:\\Users\\"));
		
		FileNameExtensionFilter restrict = new FileNameExtensionFilter("Text & CSV & Java Files", "txt", "csv", "java");
		fc.setFileFilter(restrict);
		fc.showOpenDialog(null);
		
		File f = fc.getSelectedFile();
		
		try (FileReader fr = new FileReader(f)) {
			String text = "";
			int i=0;
			while((i = fr.read()) !=-1) {
				text+=(char) i;
			}
			textArea.setText(text);
		}
		
	}
	
	public void newPage() throws IOException {
		if(!textArea.getText().isBlank()) {
			int result = JOptionPane.showConfirmDialog(null, 
				    "Do you want to save your changes?",
				    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				save();
				textArea.setText("");
				currentFile = null;
			}else if(result == JOptionPane.NO_OPTION) {
				textArea.setText("");
				currentFile = null;
			}else {
				return;
			}
		}
		else
			textArea.setText("");
	}
}

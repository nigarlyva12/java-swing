package TextEditor;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TextEditor {
	private JFrame mainFrame;
	private Image icon;
	private JMenuBar mb;
	private JMenuItem newPage, openFiles, saveFile, saveAs, aboutFakeWordPad, exit;
	private JMenu menu;
	
	public TextEditor() throws IOException {
		icon = ImageIO.read(new File("notepad.png")); 
		
		mainFrame = new JFrame("Fake WordPad");
		mb = new JMenuBar();
		menu = new JMenu("File");
		
		newPage = new JMenuItem("New");
		openFiles = new JMenuItem("Open");
		saveFile = new JMenuItem("Save");
		saveAs = new JMenuItem("Save as");
		aboutFakeWordPad = new JMenuItem("About");
		exit = new JMenuItem("Exit");

		menu.add(newPage);
		menu.add(openFiles);
		menu.add(saveFile);
		menu.add(saveAs);
		menu.add(aboutFakeWordPad);
		menu.add(exit);
		
		mb.add(menu);
		
		mainFrame.setJMenuBar(mb);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(720,480);
		mainFrame.setIconImage(icon);
		mainFrame.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException {
		new TextEditor();
	}
}

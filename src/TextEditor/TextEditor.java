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
	private final int W = 720;
	private final int H = 480;
	
	private JFrame mainFrame;
	private Image icon;
	private JMenuBar mb;
	private JMenuItem newPage, openFiles, saveFile, saveAs, aboutFakeWordPad, exit;
	private JMenu menu;
	
	public TextEditor() throws IOException {
		EditorPanel panel = new EditorPanel();
		icon = ImageIO.read(new File("notepad.png")); 
		
		mainFrame = new JFrame("Fake WordPad");
		mb = new JMenuBar();
		menu = new JMenu("File");
		
		FileManager fileManager = new FileManager(panel.getTextArea());

		newPage = new JMenuItem("New");
		openFiles = new JMenuItem("Open");
		saveFile = new JMenuItem("Save");
		saveAs = new JMenuItem("Save as");
		aboutFakeWordPad = new JMenuItem("About");
		exit = new JMenuItem("Exit");
		
		newPage.addActionListener(e -> {
			try {
				fileManager.newPage();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		saveFile.addActionListener(e -> {
			try {
				fileManager.save();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		saveAs.addActionListener(e -> {
			try {
				fileManager.saveAs();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		openFiles.addActionListener(e -> {
			try {
				fileManager.open();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		aboutFakeWordPad.addActionListener(e -> {
			
		});
		
		exit.addActionListener(e -> {
			try {
				fileManager.exit();
				System.exit(0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		
		menu.add(newPage);
		menu.add(openFiles);
		menu.add(saveFile);
		menu.add(saveAs);
		menu.add(aboutFakeWordPad);
		menu.add(exit);
		
		mb.add(menu);
		
		mainFrame.setJMenuBar(mb);
		mainFrame.add(panel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(W, H);
		mainFrame.setIconImage(icon);
		mainFrame.setVisible(true);
		
	}
	
	public static void main(String[] args) throws IOException {
		new TextEditor();
	}
}

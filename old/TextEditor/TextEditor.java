package TextEditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class TextEditor {
	private final int W = 720;
	private final int H = 480;
	
	private JFrame mainFrame;
	private Image icon;
	private JMenuBar mb;
	private JMenuItem newPage, openFiles, saveFile, saveAs, aboutFakeWordPad, exit;
	private JMenu fileMenu;
	private JMenu homeMenu;
	
	public TextEditor() throws IOException {
		EditorPanel panel = new EditorPanel();
		icon = ImageIO.read(new File("notepad.png")); 
		
		mainFrame = new JFrame("Fake WordPad");
		mb = new JMenuBar();
		
		fileMenu = new JMenu("File");
		fileMenu.setForeground(Color.white);
		fileMenu.addSeparator();
		homeMenu = new JMenu("Home");
		homeMenu.setForeground(Color.white);
		
		fileMenu.setFont(new Font("Arial", Font.BOLD, 14));
		homeMenu.setFont(new Font("Arial", Font.BOLD, 14));
		
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
				JOptionPane.showMessageDialog(null, "Datei wurde erfolgreich gespeichert", "Info",
						JOptionPane.INFORMATION_MESSAGE);
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
			JOptionPane.showMessageDialog(null, 
				    "Fake WordPad\nVersion 1.0\n© 2026",
				    "About",
				    JOptionPane.INFORMATION_MESSAGE);
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
		
		fileMenu.add(newPage);
		fileMenu.add(openFiles);
		fileMenu.add(saveFile);
		fileMenu.add(saveAs);
		fileMenu.add(aboutFakeWordPad);
		fileMenu.add(exit);
		
		mb.add(fileMenu);
		mb.add(homeMenu);
		
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

package cherriew_CSCI201_Assignment2;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextEdit extends JFrame {
	
	// member variables 
	private JTextArea area = new JTextArea(20,120);
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
	private String currFile = "Untitled";
	private boolean change = false;
	private JTextComponent textComp;
	private JTabbedPane tabs;
	protected UndoManager undo = new UndoManager();
	
	// constructor
	public TextEdit() {
		// title 
		super("Super swag Text Editor!!!!");
		textComp = createTextComponent();
		
		// making a menu bar
		JMenuBar menu = new JMenuBar();
		setJMenuBar(menu);
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu spell = new JMenu("Spellcheck");
		menu.add(file); 
		menu.add(edit);
		menu.add(spell);
		
		// functionalities for edit
		ActionMap huh = area.getActionMap();
		Action Cut = huh.get(DefaultEditorKit.cutAction);
		Action Copy = huh.get(DefaultEditorKit.copyAction);
		Action Paste = huh.get(DefaultEditorKit.pasteAction);
		Action SelectAll = huh.get(DefaultEditorKit.selectAllAction);

		
		// drop down menu options for FILE
		file.add(NewTab);
		file.add(Open);
		file.add(Save);
		file.add(Quit);
		
		// drop down menu options for EDIT
		

		edit.add(undoAction);
		edit.add(redoAction);
		edit.addSeparator();
		edit.add(Cut);
		edit.add(Copy);
		edit.add(Paste);
		edit.addSeparator();
		edit.add(SelectAll);
		

		edit.getItem(3).setText("Cut");
		edit.getItem(4).setText("Copy");
		edit.getItem(5).setText("Paste");
		edit.getItem(7).setText("Select All");
		
		spell.add(Run);
		spell.add(Config);
		
		edit.getItem(0).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.META_MASK));
		edit.getItem(1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.META_MASK));
		edit.getItem(3).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.META_MASK));
		edit.getItem(4).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		edit.getItem(5).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.META_MASK));
		edit.getItem(7).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.META_MASK));
		
		file.getItem(0).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		file.getItem(1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
		file.getItem(2).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
		
		spell.getItem(0).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.META_MASK));
		spell.getItem(1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.META_MASK));
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// sets the size of the window to not be tiny lmao
		setSize(320, 240);
	}
	// FUNCTION THAT CREATES A TEXT AREA
	protected JTextComponent createTextComponent() {
		JTextArea ta = new JTextArea();
		ta.setLineWrap(true);
	    return ta;
	}
	
	// FUNCTION FOR UNDO AND REDO
	protected class MyUndoableEditListener implements UndoableEditListener {

		@Override
		public void undoableEditHappened(UndoableEditEvent e) {
			undo.addEdit(e.getEdit());
			//undoAction.update1();
			//redoAction.updateUndoState();
			
		}
	}
	
	// ACTIONS FOR REDO AND UNDO
	Action undoAction = new AbstractAction("Undo"){
		public void actionPerformed(ActionEvent ev) {
			try{
				undo.undo();
				
			} catch(CannotUndoException ex) {
				System.out.println("Unable to undo");
			}
			update1();
			//redoAction.update1();
		}
	};
	protected void update1(){
		if(undo.canUndo()){
			setEnabled(true);
			
		} else {
			setEnabled(false);
		}
	}
	Action redoAction = new AbstractAction("Redo"){
		public void actionPerformed(ActionEvent ev) {
			try{
				undo.redo();
				
			} catch(CannotUndoException ex) {
				System.out.println("Unable to redo");
			}
			updateUndoState();
			//undoAction.updateUndoState();
		}
	};
	protected void updateUndoState() {
		if(undo.canRedo()){
			setEnabled(true);
		} else {
			setEnabled(false);
		}
			
	}
	
	
	// ACITONS FOR SPELL CHECK
	Action Run = new AbstractAction("Run") {
		public void actionPerformed(ActionEvent ev) {
			tabs = new JTabbedPane();
			tabs.add(textComp, BorderLayout.CENTER);
			setVisible(true);
			
		}
	};
	Action Config = new AbstractAction("Configure") {
		public void actionPerformed(ActionEvent ev) {
			tabs = new JTabbedPane();
			tabs.add(textComp, BorderLayout.CENTER);
			setVisible(true);
			
		}
	};
	
	
	
	// FUNCTION FOR NEW FILE
	Action NewTab = new AbstractAction("New") {
		public void actionPerformed(ActionEvent ev) {
			JScrollPane scroll = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			add(scroll,BorderLayout.CENTER);
			setVisible(true);
//			tabs = new JTabbedPane();
//			tabs.add(area, BorderLayout.CENTER);
//			setVisible(true);
			
		}
	};
	
	// FUNCTION FOR OPEN
	Action Open = new AbstractAction("Open"){
		public void actionPerformed(ActionEvent ev) {
		      JFileChooser chooser = new JFileChooser();
		      if (chooser.showOpenDialog(TextEdit.this) != JFileChooser.APPROVE_OPTION) {
		    	  	return;
		      }
		      
		      File file = chooser.getSelectedFile();
		      if (file == null) {
		        return;
		      }

		      FileReader reader = null;
		      try {
		        reader = new FileReader(file);
		        area.read(reader, null);  
				JScrollPane scroll = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				add(scroll,BorderLayout.CENTER);
				setVisible(true);
		      }
		      
		      catch (IOException ex) {
		        JOptionPane.showMessageDialog(TextEdit.this,
		        "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
		      }
		      
		      finally {
		        if (reader != null) {
		          try {
		            reader.close();
		          } catch (IOException x) {}
		        }
		      }
		    }
	};
	
	
	// FUNCTION FOR SAVE AS
	Action Save = new AbstractAction("Save") {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
		      if (chooser.showSaveDialog(TextEdit.this) != JFileChooser.APPROVE_OPTION) {
		    	  return;
		      }
		      
		      File file = chooser.getSelectedFile();
		      if (file == null) {
		        return;
		      }
		      
		      FileWriter writer = null;
		      try {
		        writer = new FileWriter(file);
		        area.write(writer);
		      }
		      catch (IOException ex) {
		        JOptionPane.showMessageDialog(TextEdit.this,
		        "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
		      }
		      finally {
		        if (writer != null) {
		          try {
		            writer.close();
		          } catch (IOException x) {}
		        }
		      }
			
		}
	};
	
	// ACTION FOR QUIT
	Action Quit = new AbstractAction("Quit") {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
		
	
	// MAIN
	public static void main(String[] args){
		// blurb that makes it cross platform compatible
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e ){
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		
		TextEdit editor = new TextEdit();
	    editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    editor.setVisible(true);
		
	}
	
}

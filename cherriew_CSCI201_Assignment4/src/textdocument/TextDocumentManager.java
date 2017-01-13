package textdocument;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import customui.OfficeTabbedPane;
import fileChooser.SingleTypeFileChooser;
import fileChooser.customChooser;
import fileChooser.openChooser;
import library.ImageLibrary;

public class TextDocumentManager extends OfficeTabbedPane {
	private static final long serialVersionUID = -4649936834531540925L;
	
	private final JMenuBar mMenuBar;
	private final JMenu mFileMenu;
	private final JMenuItem mNewItem;
	private final JMenuItem mOpenItem;
	private final JMenuItem mSaveItem;
	private final JMenuItem mCloseItem;
	private boolean isOnline;
	private String username;
	private TextDocumentManager myself;
	
	{
		mFileMenu = new JMenu("File");
		mFileMenu.setMnemonic('F');
		
		mNewItem = new JMenuItem("New");
		mNewItem.setMnemonic('N');
		mNewItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/new.png")));
		mNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		mNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel toAdd = new TextDocumentPanel();
				TextDocumentManager.this.addTab("New",toAdd);
				TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount()-1);
			}
		});
		
		mOpenItem = new JMenuItem("Open");
		mOpenItem.setMnemonic('O');
		mOpenItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/open.png")));
		mOpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		mOpenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean wantOnline = true;
				// program is currently online
				if (isOnline) {
					// open jDialog prompting whether user wants online or offline options
					int n = JOptionPane.showConfirmDialog(
	        			    TextDocumentManager.this,
	        			    "Would you like to work from online files?",
	        			    "Online/Offline Options",
	        			    JOptionPane.YES_NO_OPTION,
	        			    JOptionPane.WARNING_MESSAGE);
	        		if(n != 0) wantOnline = false;
				}
				if(wantOnline == false) {
					// offline options
					SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
					txtChooser.setDialogTitle("Open File...");
					int returnValue = txtChooser.showOpenDialog(null);
			        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        	File file = txtChooser.getSelectedFile();
			        	if(!isFileOpen(file)) {
							try {
								TextDocumentPanel toAdd = new TextDocumentPanel(file);
		    		        	TextDocumentManager.this.addTab(file.getName(),toAdd);
		    		        	TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount()-1);
							} catch (IOException e) {
								JOptionPane.showMessageDialog(
										null,
									    file.getName() + " failed to be read.",
									    "File Error",
									    JOptionPane.ERROR_MESSAGE);
							}
			        	} else {
			        		selectTabByFile(file);
			        		JOptionPane.showMessageDialog(null, file.getName()+"is already open.");
			        	}
			        }		
				} else {
					// OPEN CUSTOM JDIALOG TO ALLOW USER TO SELECT FROM SERVER 
					// TODO:
					openChooser onlineOpen = new openChooser();
					onlineOpen.setVisible(true);
				}
		
			}
		});
		
		mSaveItem = new JMenuItem("Save");
		mSaveItem.setMnemonic('S');
		mSaveItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/save.png")));
		mSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		mSaveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				boolean wantOnline = true;
				// program is currently online
				if (isOnline) {
					// open jDialog prompting whether user wants online or offline options
					int n = JOptionPane.showConfirmDialog(
	        			    TextDocumentManager.this,
	        			    "Would you like to work from online files?",
	        			    "Online/Offline Options",
	        			    JOptionPane.YES_NO_OPTION,
	        			    JOptionPane.WARNING_MESSAGE);
	        		if(n != 0) wantOnline = false;
				}
				if(wantOnline == false) {
					TextDocumentPanel toSave = getActiveDocumentPanel();
					if(toSave == null) return;
					SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
					txtChooser.setDialogTitle("Save File...");
					int returnValue = txtChooser.showSaveDialog(null);
			        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        	File file = txtChooser.getSelectedFile();
			        	boolean shouldSave = true;
			        	if(file.exists()) {
			        		int n = JOptionPane.showConfirmDialog(
			        			    TextDocumentManager.this,
			        			    file.getName() + " already exists\nDo you want to replace it?",
			        			    "Confirm Save As",
			        			    JOptionPane.YES_NO_OPTION,
			        			    JOptionPane.WARNING_MESSAGE);
			        		if(n != 0) shouldSave = false;
			        	}
			        	if(shouldSave) {
			        		try {
								getActiveDocumentPanel().save(file);
								setActiveTabText(file.getName());
							} catch (IOException e) {
								JOptionPane.showMessageDialog(
										null,
									    file.getName() + " failed to be saved.",
									    "Saving Error",
									    JOptionPane.ERROR_MESSAGE);
							}
			        	}
			        }		
				} else {
					// TODO:
					// OPEN CUSTOM JDIALOG FOR SAVING ONLINE
					customChooser saveOnline = new customChooser(myself);
					saveOnline.setVisible(true);
				}	
				
			}
		});
		
		mCloseItem = new JMenuItem("Close");
		mCloseItem.setMnemonic('C');
		mCloseItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/close.png")));
		mCloseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selected = TextDocumentManager.this.getSelectedIndex();
				if(selected == -1) return;
				TextDocumentManager.this.remove(selected);
			}
		});
		
		addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            refreshMenuBar();
	        }
	    });
		
		mFileMenu.add(mNewItem);
		mFileMenu.add(mOpenItem);
		mFileMenu.add(mSaveItem);
		mFileMenu.add(mCloseItem);
	}
	
	public TextDocumentManager(JMenuBar inMenuBar, boolean online, String username) {
		mMenuBar = inMenuBar;
		refreshMenuBar();
		isOnline = online;
		this.username = username;
		this.myself = this;
	}
	
	public TextDocumentPanel getActiveDocumentPanel() {
		int selected = getSelectedIndex();
		if(selected == -1) return null;
		Component toReturn = getComponentAt(selected);
		if (toReturn instanceof TextDocumentPanel) return (TextDocumentPanel)toReturn;
		else return null;
	}
	
	private void selectTabByFile(File file) {
		int index = -1;
		for(int i = 0; i < getComponentCount(); ++i) {
			Component atIndex = getComponentAt(i);
			if (atIndex instanceof TextDocumentPanel) {
				if(file.equals(((TextDocumentPanel)atIndex).getFile())) {
					index = i;
					break;
				}
			}
		}
		if(index != -1) setSelectedIndex(index);
	}
	
	private void setActiveTabText(String title) {
		int selected = getSelectedIndex();
		if(selected == -1) return;
		this.setTitleAt(selected, title);
	}
	
	public boolean isFileOpen(File file) {
		for(Component component : TextDocumentManager.this.getComponents()) {
    		if(component instanceof TextDocumentPanel) {
    			if(file.equals(((TextDocumentPanel)component).getFile())) {
    				return true;
    			}
    		}
    	}
		return false;
	}

	
	public static class SaveDocumentHolder implements Serializable{
		
		public String username, filename, data;
		
		public SaveDocumentHolder(String filename, String username, String data)
		{
			this.filename = filename;
			this.username = username;
			this.data = data;
		}
		
	}
	
	public void saveDocumentOnline(String filename) throws ClassNotFoundException, IOException{
		
		// Save it on the server hereth
		   // make a socket here
        Socket ss = null;
        ObjectInputStream ois = null;
			try {
				ss = new Socket("localhost", 6789);
				ObjectOutputStream oos = new ObjectOutputStream(ss.getOutputStream());
				ois = new ObjectInputStream(ss.getInputStream());
				String textBody = this.getActiveDocumentPanel().mTextPane.getText();
				SaveDocumentHolder save = new SaveDocumentHolder(filename, username, textBody);
				oos.writeObject(save);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// show failure here
				e.printStackTrace();
			}
			
			if (ois != null && (boolean)ois.readObject() == true) {
				// Show success
				System.out.println("Save success");
			}else{
				// show Failure
				System.out.println("Save failure");
			}

	}
	
	private void refreshMenuBar() {
		mMenuBar.removeAll();
		mMenuBar.add(mFileMenu);
		TextDocumentPanel activeDocument = getActiveDocumentPanel();
		if(activeDocument != null) {
			mMenuBar.add(activeDocument.getEditMenu());
			mMenuBar.add(activeDocument.getSpellCheckMenu());
		}
	}
}

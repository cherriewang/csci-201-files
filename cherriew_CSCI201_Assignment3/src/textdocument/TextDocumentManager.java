package textdocument;


import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fileChooser.SingleTypeFileChooser;

public class TextDocumentManager extends JTabbedPane {
	private static final long serialVersionUID = -4649936834531540925L;
	
	/*Menu for the document manager*/
	private final JMenuBar mMenuBar;
	private final JMenu mFileMenu;
	private final JMenuItem mNewItem;
	private final JMenuItem mOpenItem;
	private final JMenuItem mSaveItem;
	private final JMenuItem mCloseItem;
	private TextDocumentPanel toAdd;
	private frame.OfficeFrame myOffice;
	
	{

		// TODO: MenuBar Background
//		mMenuBar = new JMenuBar() {
//			  @Override
//			  public void paintComponent(Graphics g) {
//				  g.drawImage(Toolkit.getDefaultToolkit().getImage("Assignment3Resources/img/menu/red_button11.png"),0,0,this);
//			  }
//		};
	
		myOffice = null;
		mFileMenu = new JMenu("File");
		mFileMenu.setMnemonic('F');
		
		mNewItem = new JMenuItem("New");
		mNewItem.setMnemonic('N');
		mNewItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/new.png"));
		mNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// removes JLabel
				//myOffice.trojanOffice.setVisible(false);
				//myOffice.myPanelThing.setVisible(false);
//				myOffice.getContentPane().remove(myOffice.myPanelThing);
//				myOffice.revalidate();
//				myOffice.repaint();

				toAdd = new TextDocumentPanel();
				TextDocumentManager.this.addTab("New",toAdd);
				TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount()-1);
			}
		});
		
		
		mOpenItem = new JMenuItem("Open");
		mOpenItem.setMnemonic('O');
		mOpenItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/open.png"));
		mOpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mOpenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
				txtChooser.setDialogTitle("Open File...");
				int returnValue = txtChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	File file = txtChooser.getSelectedFile();
		        	if(!isFileOpen(file)) {
						try {
							// removes JLabel
							//myOffice.trojanOffice.setVisible(false);
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
			}
		});
		
		mSaveItem = new JMenuItem("Save");
		mSaveItem.setMnemonic('S');
		mSaveItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/save.png"));
		mSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mSaveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
		        		if(!file.getName().endsWith(".txt")) {
		        			JOptionPane.showMessageDialog(
									null,
								    "The file must be .txt!",
								    "Saving Error",
								    JOptionPane.ERROR_MESSAGE);
		        		} else {
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
		        }
			}
		});
		
		mCloseItem = new JMenuItem("Close");
		mCloseItem.setMnemonic('C');
		mCloseItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/close.png"));
		mCloseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selected = TextDocumentManager.this.getSelectedIndex();
				if(selected == -1) return;
				TextDocumentManager.this.remove(selected);
				revalidate();
				repaint();
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
	
	public void loadingFrame(frame.OfficeFrame loadFrame) {
		myOffice = loadFrame;
	}
	
	
	//The manager takes a JMenubar from the parent frame so the user can interact with it
	public TextDocumentManager(JMenuBar inMenuBar) {
		mMenuBar = inMenuBar;
		refreshMenuBar();
	}
	
	//Returns the DocumentPanel that is currently being worked on
	public TextDocumentPanel getActiveDocumentPanel() {
		int selected = getSelectedIndex();
		if(selected == -1) return null;
		Component toReturn = getComponentAt(selected);
		if (toReturn instanceof TextDocumentPanel) return (TextDocumentPanel)toReturn;
		else return null;
	}
	
	//Focuses on tab that is dealing with the file
	private void selectTabByFile(File file) {
		

		int index = -1;
		for(int i = 0; i < getComponentCount(); ++i) {
			Component atIndex = getComponentAt(i);
			if (atIndex instanceof TextDocumentPanel) {
				if(file.equals(((TextDocumentPanel)atIndex).getFile())) {
					index = i;
					this.setForegroundAt(index, Color.orange);
					break;
				}
			}
		}
		for(int j = 0; j < this.getTabCount(); j++){
			if (j != index) {
				this.setBackgroundAt(j, Color.black);
			}
		}
		if(index != -1) setSelectedIndex(index);
	}
	
	//Changes the name of the tab
	private void setActiveTabText(String title) {
		int selected = getSelectedIndex();
		if(selected == -1) return;
		this.setTitleAt(selected, title);
	}
	
	//Checks to see if the document is already opened
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

	//Called when documents are switched, sets menu bar items
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

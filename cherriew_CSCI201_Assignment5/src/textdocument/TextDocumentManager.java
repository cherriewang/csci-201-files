package textdocument;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.Client;
import customui.FileDialog;
import customui.OfficeTabbedPane;
import fileChooser.SingleTypeFileChooser;
import library.ImageLibrary;
import server.Commands;
import server.Commands.Command;
import server.Database;

public class TextDocumentManager extends OfficeTabbedPane {
	private static final long serialVersionUID = -4649936834531540925L;
	
	public static TextDocumentManager sharedManager;
	
	private final JMenuBar mMenuBar;
	private final JMenu mFileMenu;
	private final JMenuItem mNewItem;
	private final JMenuItem mOpenItem;
	private final JMenuItem mSaveItem;
	private final JMenuItem mCloseItem;
	
	public ArrayList<TextDocumentPanel> allPanelsOpen;
	
	{
		sharedManager = this;
		allPanelsOpen = new ArrayList();
		
		
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
				allPanelsOpen.add(toAdd);
			}
		});
		
		mOpenItem = new JMenuItem("Open");
		mOpenItem.setMnemonic('O');
		mOpenItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/open.png")));
		mOpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		mOpenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int choice = JOptionPane.NO_OPTION;
				if(Client.get().isAuthentic() && Client.get().isOnline()) {
					Object[] options = {"Online", "Offline"};
					choice = JOptionPane.showOptionDialog(null,
					"Where would you like to open the file?",
					"Open...",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
				}
				if(choice == JOptionPane.YES_OPTION)
					try { openFileOnline(); }
					catch (IOException e) { }
				else openFileOffline();
			}
		});
		
		mSaveItem = new JMenuItem("Save");
		mSaveItem.setMnemonic('S');
		mSaveItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/save.png")));
		mSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		mSaveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel toSave = getActiveDocumentPanel();
				if(toSave == null) return;
				int choice = JOptionPane.NO_OPTION;
				if(Client.get().isAuthentic() && Client.get().isOnline()) {
					Object[] options = {"Online", "Offline"};
					choice = JOptionPane.showOptionDialog(null,
					"Where would you like to open the file?",
					"Save...",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
				}
				if(choice == JOptionPane.YES_OPTION) saveFileOnline();
				else saveFileOffline();
			}
		});
		
		mCloseItem = new JMenuItem("Close");
		mCloseItem.setMnemonic('C');
		mCloseItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/close.png")));
		mCloseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				allPanelsOpen.remove(getActiveDocumentPanel());
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
	
	public TextDocumentManager(JMenuBar inMenuBar) {
		mMenuBar = inMenuBar;
		refreshMenuBar();
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

	private void refreshMenuBar() {
		mMenuBar.removeAll();
		mMenuBar.add(mFileMenu);
		TextDocumentPanel activeDocument = getActiveDocumentPanel();
		if(activeDocument != null) {
			mMenuBar.add(activeDocument.getEditMenu());
			mMenuBar.add(activeDocument.getSpellCheckMenu());
			if(Client.get().isOnline()){
				mMenuBar.add(activeDocument.getUsers());
			}
		}
	}
	
	private void openFileOffline() {
		SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
		txtChooser.setDialogTitle("Open File...");
		int returnValue = txtChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File file = txtChooser.getSelectedFile();
        	openFile(file, false);
        }
	}
	
	private void openFileOnline() throws IOException {
//		Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
//		Client.get().flush();
//		String[] files = Client.get().read().split(" ");
//		String selectedFileName = FileDialog.showDialog(files, "Open...", false);
//		if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
//		Client.get().writeln(Commands.buildCommand(Command.OpenUserFile, selectedFileName));
//		Client.get().flush();
//		File temp = new File("clientfiles/"+selectedFileName);
//		Client.get().readFile(temp);
//		openFile(temp);
//		temp.delete();
		
		// create new dialog
		SharedUserDialog outside = new SharedUserDialog();
		outside.setVisible(true);
		// if they decide to just view their own files
		outside.addConfirmListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e){
	        	 if (outside.isVisible()) {
	                 outside.setVisible(false);
	              }
	        	try {
	        		
	        		File userDirectory = new File("serverfiles/" + Client.get().getUser());
	        		
	        		ArrayList<String> files = new ArrayList();
	        		
	        		for (File f : userDirectory.listFiles()) 
	        		{
	        			files.add(f.getName());
	        		}
	        		
	        		String[] strings = new String[files.size()];
					 strings = files.toArray(strings);
	        		
					//Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
		     		//Client.get().flush();
	        		
		     		//String[] files = Client.get().read().split(" ");
		     		String selectedFileName = FileDialog.showDialog(strings, "Open...", false);
		     		if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
		     		//Client.get().writeln(Commands.buildCommand(Command.OpenUserFile, selectedFileName));
		     		//Client.get().flush();
		     		File temp = new File("serverfiles/"+ Client.get().getUser() + "/" + selectedFileName);
		     		//Client.get().readFile(temp);
		     		openFile(temp, true);
		     		temp.delete();
	        	} catch (Exception e1) {
					e1.printStackTrace();
				}
	        	  	 
	         }
	      });
		// opens up the client
		outside.addSelectUserListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e){
				 
				 String sharingWithMe; 
				 sharingWithMe = (String) outside.listbox.getSelectedValue();
				 ArrayList<String> ar = Database.get().getSharedFilesWithMe(Client.get().getUser(), sharingWithMe);
				 String[] strings = new String[ar.size()];
				 strings = ar.toArray(strings);
				 
				 try {
//					Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
//		     		Client.get().flush();
		     		//String[] files = Client.get().read().split(" ");
					 
//					 File userDirectory = new File("serverfiles/" + sharingWithMe);
//		        		
//		        		ArrayList<String> list = new ArrayList();
//		        		
//		        		for (File f : userDirectory.listFiles()) 
//		        		{
//		        			list.add(f.getName());
//		        		}
//		        		
//		        		String[] files = new String[list.size()];
//						 strings = list.toArray(strings);
//					 
		     		String selectedFileName = FileDialog.showDialog(strings, "Open...", false);
		     		if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
//		     		Client.get().writeln(Commands.buildCommand(Command.OpenUserFile, selectedFileName));
//		     		Client.get().flush();
		     		//File temp = new File("clientfiles/"+selectedFileName);
		     		File temp = new File("serverfiles/"+sharingWithMe+"/"+selectedFileName);
		     		//Client.get().readFile(temp);
		     		
		     		//byte[] encoded = Files.readAllBytes(Paths.get("serverfiles/"+sharingWithMe+"/"+selectedFileName));
		     		//String textStuff = new String(encoded, StandardCharsets.UTF_8);
		     		
		     		openFile(temp, true);
		     		temp.delete();
	        	} catch (Exception e1) {
					e1.printStackTrace();
				}
				 
				 
//				 if (outside.isVisible()) {
//	                 outside.setVisible(false);
//	             }
//
//				 String[] newData;
//				 newData = outside.getDocs();
//				 
//				 try {
//						Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
//			     		Client.get().flush();
//			     		//String[] files = Client.get().read().split(" ");
//			     		String[] files = newData;
//			     		String selectedFileName = FileDialog.showDialog(files, "Open...", false);
//			     		if(selectedFileName == null || selectedFileName.length() < 3 || !selectedFileName.endsWith(".txt")) return;
//			     		Client.get().writeln(Commands.buildCommand(Command.OpenUserFile, selectedFileName));
//			     		Client.get().flush();
//			     		File temp = new File("clientfiles/"+selectedFileName);
//			     		Client.get().readFile(temp);
//			     		openFile(temp);
//			     		temp.delete();
//		        	} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				 
			 }
		});
		
	}
	
	
	
	private void openFile(File file, boolean amIonline) {
		
		if(!isFileOpen(file)) {
			try {
				TextDocumentPanel toAdd = new TextDocumentPanel(file);
				allPanelsOpen.add(toAdd);
	        	toAdd.isOnline = amIonline;
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
	
	private void saveFileOffline() {
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
					
					// make it offline
					getActiveDocumentPanel().isOnline = false;
					
					
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
	
	private boolean checkExists(String m) {
		Connection conn = null;
		try{
		      // register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");

			  PreparedStatement pt = conn.prepareStatement("SELECT * FROM file_owner WHERE filename=?");
	            pt.setString(1, m);
  
			  ResultSet rs = pt.executeQuery();
			  if (rs.next()){
				  // a result is found!!! 
				  System.out.println("This file already exists in the table.");
				  return true;
			  } else{
				  return false;
			  }

		    } catch (SQLException sqle) {
				System.out.println ("SQLException: " + sqle.getMessage());
				sqle.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
			}	
		return true;
	}
	
	
	private void saveFileOnline() {
		try {
			Client.get().writeln(Commands.buildCommand(Command.GetUserFileNames));
			Client.get().flush();
			String[] files = Client.get().read().split(" ");
			String selectedFileName = FileDialog.showDialog(files, "Save...", true);
			if(selectedFileName == null || !selectedFileName.endsWith(".txt") || selectedFileName.length() < 3) {
				JOptionPane.showMessageDialog(null, "File failed to save.", "", JOptionPane.ERROR_MESSAGE);
				}
			else {
				File temp = new File("clientfiles/"+selectedFileName);
				getActiveDocumentPanel().save(temp);
				getActiveDocumentPanel().isOnline = true;
				setActiveTabText(temp.getName());
				Client.get().writeln(Commands.buildCommand(Command.SaveUserFile, selectedFileName));
				Client.get().flush();
				Client.get().writeFile(temp);
				
				// TODO: SQL script here that adds FILE - USER_OWNER 
				String ownerString = Client.get().getUser();
				String fileName = selectedFileName;
				System.out.println("owner: " + ownerString+ " filename: " + fileName);
				// interact with SQL
				   Connection conn = null;
				   Statement stmt = null;
				   try{
				      // register JDBC driver
				      Class.forName("com.mysql.jdbc.Driver");
				      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
					  stmt = conn.createStatement();

					  // check username isn't already in table
					  if (!checkExists(fileName)){

						  String sql = "INSERT INTO file_owner (filename, owner) VALUES ('" + fileName + 
									"', '" + ownerString + "');";
						  stmt.executeUpdate(sql);
					      // now that we have successfully signed up we want to write object true
						  System.out.println("Just inserted into mySQL table");
						  
					  } else {
						  JOptionPane.showMessageDialog(null, "File already added under owner", "Notice", JOptionPane.WARNING_MESSAGE);
					  }
				     
				    } catch (SQLException sqle) {
						System.out.println ("SQLException: " + sqle.getMessage());
						sqle.printStackTrace();
					} catch (ClassNotFoundException cnfe) {
						System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
					}
				
				
				temp.delete();
				JOptionPane.showMessageDialog(null, "File successfully saved.", "", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File failed to save.", "", JOptionPane.ERROR_MESSAGE);
		}
	}
}

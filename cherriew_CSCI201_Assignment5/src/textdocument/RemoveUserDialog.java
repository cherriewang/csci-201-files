package textdocument;

import java.awt.BorderLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Client;
import customui.OfficeButton;

public class RemoveUserDialog extends JDialog {
	private	JPanel topPanel;
	private	JList listbox;
	private JLabel removeLabel;
	private File mWorkingFile;
	private String mFilename;
	private String[] listData;
	
	//private OfficeButton removeButton;
	public RemoveUserDialog(File currFile) {
		// Set the frame characteristics
		setTitle( "Remove Users" );
		setSize( 300, 150 );
		mWorkingFile = currFile;
		mFilename = mWorkingFile.getName();
		//removeButton = new OfficeButton("Remove");
		removeLabel = new JLabel("Select a User to remove: ");
		
		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		//TODO: fill listData with the authorized users for the file we're on

		listData = getSharedUsers();
		System.out.println("listData: " + Arrays.toString(listData));
		// Create a new listbox control
		listbox = new JList( listData );
		addListenerToMenuList();
		topPanel.add( listbox, BorderLayout.CENTER );
		//topPanel.add(removeButton, BorderLayout.SOUTH);
		topPanel.add(removeLabel, BorderLayout.NORTH);
	}
	
	// get shared user for this file
	private String [] getSharedUsers(){
	
		   Connection conn = null;
		   Statement stmt = null;
		   try{
		      // register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
			  stmt = conn.createStatement();
			  
			  PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_shared WHERE filename=?");
			  ps.setString(1, mFilename);
			  ResultSet rs = ps.executeQuery();
			  Vector<String> userIDs = new Vector<String>();
			  
			  // putting into vector
			  while(rs.next()){
				  String id = rs.getString(2);
				  userIDs.addElement(id);
			  }
			  //converting it into string array
			  String[] strings = userIDs.toArray(new String[userIDs.size()]);
			  System.out.println("converted strings: " + Arrays.toString(strings));
		      // now that we have successfully signed up we want to write object true
			  System.out.println("Just got from mySQL resultset of shared users");
			  return strings;
		     
		    } catch (SQLException sqle) {
				System.out.println ("SQLException: " + sqle.getMessage());
				sqle.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
			}
		String temp[] = {"Sorry, no shared users"};
		return temp;
	}
	
	
	private void addListenerToMenuList() {
	  listbox.addListSelectionListener(new ListSelectionListener() {
	    public void valueChanged(ListSelectionEvent e) {
	      if (e.getValueIsAdjusting() == false) {
	        String userString = (String)listbox.getSelectedValue();
	        System.out.println("You selected the user: " +userString); 
	        	if(isOwner()) {
				   Connection conn = null;
				   Statement stmt = null;
				   try{
				      // register JDBC driver
				      Class.forName("com.mysql.jdbc.Driver");
				      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
					  stmt = conn.createStatement();
			  
					  PreparedStatement ps = conn.prepareStatement("DELETE FROM file_shared WHERE filename=? AND shareduser=?");
					  ps.setString(1, mFilename);
					  ps.setString(2, userString);
					  ps.executeUpdate();
					  
				      // now that we have successfully signed up we want to write object true
					  JOptionPane.showMessageDialog(null, "You removed the selected User", "Notice", JOptionPane.WARNING_MESSAGE);
					  System.out.println("Just removed the user from this file in mySQL");
	     
				    } catch (SQLException sqle) {
						System.out.println ("SQLException: " + sqle.getMessage());
						sqle.printStackTrace();
					} catch (ClassNotFoundException cnfe) {
						System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
					}   
	        	}
	        dispose();
	      }
	      else {
	        System.out.println("I think the value is adjusting");
	      }
	    }
	  });
	}
	
	// check if you are the owner of this file
	private boolean isOwner(){
		Connection conn = null;
		try{
		      // register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");

		      PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_owner WHERE filename=?");
			  ps.setString(1, mFilename);
			  ResultSet rs = ps.executeQuery();
 
			  while(rs.next()){
				  String id = rs.getString(2);
				  if (id.equals(Client.get().getUser())) {
					  // you are the owner!!
					  System.out.println(Client.get().getUser()+" is the owner of "+mFilename);
					  return true;
				  } else{
					  System.out.println(Client.get().getUser()+" is NOT the owner of "+mFilename);
					  return false;
				  }

			  }

		    } catch (SQLException sqle) {
				System.out.println ("SQLException: " + sqle.getMessage());
				sqle.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
			}
		System.out.println("Something went wrong, so we're defaulting to NOT OWNER");
		return false;
	}
	
	
}

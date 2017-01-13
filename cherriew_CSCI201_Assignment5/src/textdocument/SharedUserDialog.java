package textdocument;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Client;
import customui.FileDialog;
import customui.OfficeButton;
import server.Commands;
import server.Commands.Command;

public class SharedUserDialog extends JDialog {
	private	JPanel topPanel;
	public	JList listbox;
	private JLabel removeLabel;
	private String userString1;
	private String[] listData;
	private String[] strings1;
	private String[] passThisOut;
	
	private OfficeButton myFilesButton;
	private JButton selectButton;
	
	public SharedUserDialog() {
		// Set the frame characteristics
		setTitle( "Users that have shared files with me" );
		setSize( 300, 150 );

		myFilesButton = new OfficeButton("My Files...");
		removeLabel = new JLabel("Choose a User: ");
		selectButton = new JButton("Select User...");
		
		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		//fill listData with the users that have shared files with me
		listData = getSharedUsers();
		System.out.println("listData: " + Arrays.toString(listData));
		// Create a new listbox control
		listbox = new JList( listData );
		
		// add actionlistener to select button that repopulates list with files of selected user
//		selectButton.addActionListener(new ActionListener() {
//			 public void actionPerformed(ActionEvent e){
//				 // returns string array of whatever's selected
//				 addListenerToMenuList();
//				 String[] newData;
//				 newData = addListenerToMenuList();
//				 
//				 
//			 }
//		});
		//passThisOut = addListenerToMenuList();
		
		topPanel.add( listbox, BorderLayout.CENTER);
		topPanel.add(myFilesButton, BorderLayout.SOUTH);
		topPanel.add(removeLabel, BorderLayout.NORTH);
		topPanel.add(selectButton,BorderLayout.EAST);
	}
	
	public void addConfirmListener(ActionListener listener){
	      myFilesButton.addActionListener(listener);
	}
	
	public void addSelectUserListener(ActionListener listener){
		// TODO
		selectButton.addActionListener(listener);
	}
	
	public String[] getDocs(){
		return passThisOut;
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
			  
			  PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_shared WHERE shareduser=?");
			  ps.setString(1, Client.get().getUser());
			  ResultSet rs = ps.executeQuery();
			  Vector<String> fileIDs = new Vector<String>();
			  
			  // putting into vector
			  while(rs.next()){
				  String id = rs.getString(1);
				  fileIDs.addElement(id);
			  }
			  // to put our shared users in for now
			  ArrayList<String> ar = new ArrayList<String>();
			  
			  for(int i =0; i < fileIDs.size(); i++){
				  try{
				      // register JDBC driver
				      Class.forName("com.mysql.jdbc.Driver");
				      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
					  stmt = conn.createStatement();
					  
					  PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM file_owner WHERE filename=?");
					  ps2.setString(1, fileIDs.elementAt(i));
					  ResultSet rs2 = ps2.executeQuery();
					  // putting into arraylist
					  while(rs2.next()){
						  String owner_id = rs2.getString(2);
						  ar.add(owner_id);
					  }
					  
				  } catch (SQLException sqle) {
						System.out.println ("SQLException: " + sqle.getMessage());
						sqle.printStackTrace();
					} catch (ClassNotFoundException cnfe) {
						System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
					}
			  }
			  
			  //converting it into string array
			 // String[] strings = fileIDs.toArray(new String[fileIDs.size()]);
			  String[] strings = new String[ar.size()];
			  strings = ar.toArray(strings);
			  
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
	
	
//	
//	private String[] addListenerToMenuList(){
//		listbox.addListSelectionListener(new ListSelectionListener() {
//		    public void valueChanged(ListSelectionEvent e) {
//		      if (e.getValueIsAdjusting() == false) {
//		        //String userString = (String)listbox.getSelectedValue();
//		        userString1 = (String)listbox.getSelectedValue();
//		        System.out.println("You selected the user: " +userString1); 
//		        
//		        // do the query things and put into a string array
//		          Connection conn = null;
//				  Statement stmt = null;
//			      // first get all the files that selected user owns
//			      try{
//					      // register JDBC driver
//					      Class.forName("com.mysql.jdbc.Driver");
//					      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
//						  stmt = conn.createStatement();
//						  
//						  PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_owner WHERE owner=?");
//						  ps.setString(1, userString1);
//						  ResultSet rs = ps.executeQuery();
//						  Vector<String> fileIDs = new Vector<String>();
//						  
//						  // putting into vector
//						  while(rs.next()){
//							  String id = rs.getString(1);
//							  fileIDs.addElement(id);
//						  }
//						  
//						  // ArrayList  
//						  ArrayList<String> ar = new ArrayList<String>();
//						  
//						  // then loop through list of fileIDs and check if current user is a shared user
//						  for(int i =0; i < fileIDs.size(); i++){
//							  try{
//							      // register JDBC driver
//							      Class.forName("com.mysql.jdbc.Driver");
//							      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
//								  stmt = conn.createStatement();
//								  
//								  PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM file_shared WHERE filename=?");
//								  ps2.setString(1, fileIDs.elementAt(i));
//								  ResultSet rs2 = ps2.executeQuery();
//								  // putting into arraylist
//								  while(rs2.next()){
//									  // if shared user of file is equal to me
//									  if(rs2.getString(2).equals(Client.get().getUser())){
//										  ar.add(rs2.getString(2));
//									  }
//								  }
//								  // convert arraylist to string array and return
//								  strings1 = new String[ar.size()];
//								  strings1 = ar.toArray(strings1);
//								  
//							  } catch (SQLException sqle) {
//									System.out.println ("SQLException: " + sqle.getMessage());
//									sqle.printStackTrace();
//								} catch (ClassNotFoundException cnfe) {
//									System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
//								}
//						  }
//						  
//				        } catch (SQLException sqle) {
//							System.out.println ("SQLException: " + sqle.getMessage());
//							sqle.printStackTrace();
//						} catch (ClassNotFoundException cnfe) {
//							System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
//						}
//		        
//		        
//		        dispose();
//		      }
//		      else {
//		        System.out.println("I think the value is adjusting");
//		      }
//		    }
//		  });
//	  
//	  if (userString1 != null){
//		  // we have a valid thing
//		  return strings1;
//	  }
//	  else{
//		   // default return statement
//		   JOptionPane.showMessageDialog(null, "Make sure to select a user", "Notice", JOptionPane.WARNING_MESSAGE);
//	  	   dispose();
//		   String temp[] = {"ERROR 404"};
//		   return temp;
//	  }
//	}
	
	
//	String[] addListenerToMenuList() {
//	  listbox.addListSelectionListener(new ListSelectionListener() {
//	    public void valueChanged(ListSelectionEvent e) {
//	      if (e.getValueIsAdjusting() == false) {
//	        userString1 = (String)listbox.getSelectedValue();
//	        System.out.println("You selected the user: " +userString1); 
//	      }
//	      else {
//	    	  System.out.println("I think the value is adjusting");
//	      }
//	    }
//	  });
//	  
//	  // TODO
//	  //
//      if (userString1 == null){
//    	  JOptionPane.showMessageDialog(null, "You must select a user", "Error", JOptionPane.ERROR_MESSAGE);
//      } else{  
//		  Connection conn = null;
//		  Statement stmt = null;
//	      // first get all the files that selected user owns
//	      try{
//			      // register JDBC driver
//			      Class.forName("com.mysql.jdbc.Driver");
//			      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
//				  stmt = conn.createStatement();
//				  
//				  PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_owner WHERE owner=?");
//				  ps.setString(1, userString1);
//				  ResultSet rs = ps.executeQuery();
//				  Vector<String> fileIDs = new Vector<String>();
//				  
//				  // putting into vector
//				  while(rs.next()){
//					  String id = rs.getString(1);
//					  fileIDs.addElement(id);
//				  }
//				  
//				  // ArrayList  
//				  ArrayList<String> ar = new ArrayList<String>();
//				  
//				  // then loop through list of fileIDs and check if current user is a shared user
//				  for(int i =0; i < fileIDs.size(); i++){
//					  try{
//					      // register JDBC driver
//					      Class.forName("com.mysql.jdbc.Driver");
//					      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
//						  stmt = conn.createStatement();
//						  
//						  PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM file_shared WHERE filename=?");
//						  ps2.setString(1, fileIDs.elementAt(i));
//						  ResultSet rs2 = ps2.executeQuery();
//						  // putting into arraylist
//						  while(rs2.next()){
//							  // if shared user of file is equal to me
//							  if(rs2.getString(2).equals(Client.get().getUser())){
//								  ar.add(rs2.getString(2));
//							  }
//						  }
//						  // convert arraylist to string array and return
//						  String[] strings = new String[ar.size()];
//						  strings = ar.toArray(strings);
//						  return strings;
//						  
//					  } catch (SQLException sqle) {
//							System.out.println ("SQLException: " + sqle.getMessage());
//							sqle.printStackTrace();
//						} catch (ClassNotFoundException cnfe) {
//							System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
//						}
//				  }
//				  
//		        } catch (SQLException sqle) {
//					System.out.println ("SQLException: " + sqle.getMessage());
//					sqle.printStackTrace();
//				} catch (ClassNotFoundException cnfe) {
//					System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
//				}
//       }
//       dispose();
//       String temp[] = {"Sorry, no shared files"};
//	   return temp;
//	}
	
	
	
	
	
	
	
	
	
}

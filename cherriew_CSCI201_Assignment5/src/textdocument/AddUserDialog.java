package textdocument;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import client.Client;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import customui.OfficeButton;

public class AddUserDialog extends JDialog {
	private static final long serialVersionUID = 1203713931331563215L;
	
	private JLabel mAddLabel;
	private JTextField mAddUserField;
	private OfficeButton mOKButton;
	private OfficeButton mCancelButton;
	private File mWorkingFile;
	private String mFilename;

	
	public AddUserDialog(File currFile) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Users");
		setLayout(new FlowLayout());
		setSize(400,60);
		mWorkingFile = currFile;
		mFilename = mWorkingFile.getName();

		//Create an array of the text and components to be displayed.
		mAddUserField = new JTextField(10);
      
		mOKButton = new OfficeButton("OK");
		mOKButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isOwner()) {
					// adding a user here
					String addText = mAddUserField.getText();
					if(addText.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter User", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					} else {
						   Connection conn = null;
						   Statement stmt = null;
						   try{
						      // register JDBC driver
						      Class.forName("com.mysql.jdbc.Driver");
						      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
							  stmt = conn.createStatement();
	
							  // check username is a valid username
							  if (checkExists(addText)){
	
								  String sql = "INSERT INTO file_shared (filename, shareduser) VALUES ('" + mFilename + 
											"', '" + addText + "');";
								  stmt.executeUpdate(sql);
							      // now that we have successfully signed up we want to write object true
								  System.out.println("Just inserted into mySQL table");
								  
							  } else {
								  JOptionPane.showMessageDialog(null, "User does not exist", "Error", JOptionPane.ERROR_MESSAGE);
							  }
						     
						    } catch (SQLException sqle) {
								System.out.println ("SQLException: " + sqle.getMessage());
								sqle.printStackTrace();
							} catch (ClassNotFoundException cnfe) {
								System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
							}
								
					}
				}	else {
					 JOptionPane.showMessageDialog(null, "You are not the owner of this file", "Error", JOptionPane.ERROR_MESSAGE);
				}
				dispose();	
			}
		});
		mCancelButton = new OfficeButton("CANCEL");
		mCancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// cancel and close window
				dispose();
			}
		});
		mAddLabel = new JLabel("Add User:");
		
		add(mAddLabel);
		add(mAddUserField);
		add(mOKButton);
		add(mCancelButton);
		
	}
	
	// check to make sure the username is valid
	private boolean checkExists(String m) {
		Connection conn = null;
		try{
		      // register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");

			  PreparedStatement pt = conn.prepareStatement("SELECT * FROM accounts WHERE username=?");
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

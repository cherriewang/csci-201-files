package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import com.mysql.jdbc.Driver;


import client.Client;

public class Database {
	
	private static Database sDatabase;
	public static Database get() {
		return sDatabase;
	}
	
	static {
		sDatabase = new Database();
	}
	
	private final static String newAccount = "INSERT INTO ACCOUNTS(USERNAME,PASSWORD) VALUES(?,?)";
	private final static String selectUser = "SELECT PASSWORD FROM ACCOUNTS WHERE USERNAME=?";
	private final static String selectedSharedFilesThings = "SELECT FILENAME FROM FILE_SHARED WHERE SHAREDUSER=?";
	
	private Connection con;
	
	{
		try{
			new Driver();
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
		} catch(SQLException sqle){
			System.out.println("SQL:"+sqle.getMessage());
		}
	}
	
	public void stop() {
		try {con.close();} catch (SQLException e) {e.printStackTrace();}
	}
	
	public boolean signup(String username, String password) {
		try {
			{ //check if the user exists
				PreparedStatement ps = con.prepareStatement(selectUser);
				ps.setString(1,username);
				ResultSet result = ps.executeQuery();
				if(result.next()) {//if we have any results, don't let the user sign up.
					return false;
				}
			}
			{ //sign up
				PreparedStatement ps = con.prepareStatement(newAccount);
				ps.setString(1, username);
				ps.setString(2, password);
				ps.executeUpdate();
			}
		} catch (SQLException e) {return false;}
		return true;
	}
	
	public String getOwnerOfFile(String filename){
		 Connection conn = null;
		  Statement stmt = null;
		try{
		      // register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
			  stmt = conn.createStatement();
			  
			  PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_owner WHERE filename=?");
			  ps.setString(1, filename);
			  ResultSet rs = ps.executeQuery();

			  // putting into vector
			  while(rs.next()){
				  String id = rs.getString(2);
				  return id;
			  }
			 
			  
	        } catch (SQLException sqle) {
				System.out.println ("SQLException: " + sqle.getMessage());
				sqle.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
			}
		
		return null;
		
	}
	
	
	
	public ArrayList<String> getSharedFilesWithMe(String whoIAm, String sharingWithMe){
		 ArrayList<String> ar = new ArrayList<String>();
		  
		 Connection conn = null;
		  Statement stmt = null;
	      // first get all the files that selected user owns
	      try{
			      // register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");
			      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
				  stmt = conn.createStatement();
				  
				  PreparedStatement ps = conn.prepareStatement("SELECT * FROM file_owner WHERE owner=?");
				  ps.setString(1, sharingWithMe);
				  ResultSet rs = ps.executeQuery();
				  Vector<String> fileIDs = new Vector<String>();
				  
				  // putting into vector
				  while(rs.next()){
					  String id = rs.getString(1);
					  fileIDs.addElement(id);
				  }
				  
				  // ArrayList  
	
				  // then loop through list of fileIDs and check if current user is a shared user
				  for(int i =0; i < fileIDs.size(); i++){
					  try{
					      // register JDBC driver
					      Class.forName("com.mysql.jdbc.Driver");
					      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cherriew?user=root&password=Clw3566");
						  stmt = conn.createStatement();
						  
						  PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM file_shared WHERE filename=?");
						  ps2.setString(1, fileIDs.elementAt(i));
						  ResultSet rs2 = ps2.executeQuery();
						  // putting into arraylist
						  while(rs2.next()){
							  // if shared user of file is equal to me
							  if(rs2.getString(2).equals(whoIAm)){
								  ar.add(rs2.getString(1));
							  }
						  }
						  return ar;
						  // convert arraylist to string array and return
//						  String[] strings = new String[ar.size()];
//						  strings = ar.toArray(strings);
//						  return strings;
						  
					  } catch (SQLException sqle) {
							System.out.println ("SQLException: " + sqle.getMessage());
							sqle.printStackTrace();
						} catch (ClassNotFoundException cnfe) {
							System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
						}
				  }
				  
		        } catch (SQLException sqle) {
					System.out.println ("SQLException: " + sqle.getMessage());
					sqle.printStackTrace();
				} catch (ClassNotFoundException cnfe) {
					System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
				}
	      return ar;
	}
	
	
	
	
	
	public boolean login(String username, String password) {
		try {
			PreparedStatement ps = con.prepareStatement(selectUser);
			ps.setString(1,username);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				if(password.equals(result.getString("password"))) return true;
			}
		} catch (SQLException e) {return false;}
		return false;
	}
	
	
	
	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
}
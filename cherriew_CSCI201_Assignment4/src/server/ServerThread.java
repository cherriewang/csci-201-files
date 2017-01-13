package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import textdocument.TextDocumentManager.SaveDocumentHolder;


public class ServerThread extends Thread{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private TextServer cs;
	
	public ServerThread(Socket s, TextServer cs) {
		try {
			this.cs = cs;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	

	private boolean checkExists(UserInfoBundle m) {
		Connection conn = null;
	
		try{
		      // register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
			  conn = DriverManager.getConnection("jdbc:mysql://localhost/DatabaseCherriew?user=root&password=Clw3566");

			  PreparedStatement pt = conn.prepareStatement("SELECT * FROM text_cherriew WHERE uname=?");
	            pt.setString(1, m.username);
  
			  ResultSet rs = pt.executeQuery();
			  if (rs.next()){
				  // a result is found!!! 
				  System.out.println("This username already exists in the table.");
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
	
	
	public void run() {
		try {
			while(true) {
				Object readObject = ois.readObject();
				if(readObject instanceof UserInfoBundle){
					UserInfoBundle message = (UserInfoBundle)readObject;
				// check whether you are signing up or logging in
				
				
				// WE'RE SIGNING UP
				if (message.signup == true) {
					// means we are signing up ––––––––––––––––––––––––––––––––––––––––––––
					cs.tsg.addMessage("Sign-up attempt, User: " + message.username + " Pass: " + message.password.hashCode());
					// interact with SQL
					   Connection conn = null;
					   Statement stmt = null;
					   try{
					      // register JDBC driver
					      Class.forName("com.mysql.jdbc.Driver");
						  conn = DriverManager.getConnection("jdbc:mysql://localhost/DatabaseCherriew?user=root&password=Clw3566");
						  stmt = conn.createStatement();

						  // check username isn't already in table
						  if (!checkExists(message)){

							  String sql = "INSERT INTO text_cherriew (uname, pword) VALUES ('" + message.username + 
										"', '" + message.password + "');";
							  stmt.executeUpdate(sql);
						      // now that we have successfully signed up we want to write object true
							  System.out.println("Just inserted into mySQL table");
							  cs.tsg.addMessage("Sign-up success, User: " + message.username);
							  
						      oos.writeObject(true);
						      oos.flush();
						  } else {
							  cs.tsg.addMessage("Sign-up failure, User: " + message.username);
							  oos.writeObject(false);
							  oos.flush();
						  }
					     
					    } catch (SQLException sqle) {
							System.out.println ("SQLException: " + sqle.getMessage());
							sqle.printStackTrace();
						} catch (ClassNotFoundException cnfe) {
							System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
						}
					
				} else {
					// WE'RE LOGGING IN ––––––––––––––––––––––––––––––––––––––––––––
					cs.tsg.addMessage("Login attempt, User: " + message.username + " Pass: " + message.password.hashCode());
					// this is a login
					// interact with SQL
					Connection connection = null; // manages connection
				    PreparedStatement pt = null; // manages prepared statement 
					try {
				            // establish connection to database
							Class.forName("com.mysql.jdbc.Driver");
							connection = DriverManager.getConnection("jdbc:mysql://localhost/DatabaseCherriew?user=root&password=Clw3566");

				            // query database
				            pt = connection.prepareStatement("SELECT * FROM text_cherriew WHERE uname=? AND pword=?");
				            pt.setString(1, message.username);
				            pt.setString(2, message.password);
				            
				            // process query results
				            ResultSet rs = pt.executeQuery(); 
				            if (rs.next()) {
				                // correct login
				            	// wait to receive boolean from file stream
				            	// while true loop here
				            	// either receive a file or send a dialog
				            	// file selection
				                System.out.println("Correct!");
				                cs.tsg.addMessage("Login success, User: " + message.username);
				                oos.writeObject(true);
				                oos.flush();
				                rs.close();
				            } else {
				                //do something
				            	// incorrect login
				            	// send dialog 
				            	cs.tsg.addMessage("Login failure, User: " + message.username);
				            	oos.writeObject(false);
				            	oos.flush();
				            }
				        } catch (SQLException sqle) {
							System.out.println ("SQLException: " + sqle.getMessage());
							sqle.printStackTrace();
						} catch (ClassNotFoundException cnfe) {
							System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
						}
				}				
			
			}else if(readObject instanceof SaveDocumentHolder){
				// Save it yo
				SaveDocumentHolder save = (SaveDocumentHolder)readObject;
				System.out.println(save.data);
				
				// Write to the file system here
				File fileForMe = new File(save.username+"/");
				fileForMe.mkdir();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(save.username+"/"+save.filename));
				bw.close();
				oos.writeObject(true);
				
			}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());

		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}

}


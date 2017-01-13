package fileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import customui.OfficeMenuBar;
import server.ServerThread;
import textdocument.TextDocumentManager;




public class customChooser extends JDialog {
private static final long serialVersionUID = 8449988690975761611L;
	
	private String mSelection;
	private JButton mCancelButton;
	private JButton mSelectButton;
	
	private Integer portNum;
	private String hostThing;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private static customChooser dialog;
    private static String value = "";
    private JList list;
    private JTextField inputTextField;
	
    private TextDocumentManager manage;
    
    public  customChooser(TextDocumentManager manage){
    	this.manage = manage;
    }
    
	{
		setSize(200,200);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mSelection = "";
		mSelectButton = new JButton("Save As");
		mCancelButton = new JButton("Cancel");
		
		inputTextField = new JTextField();
		
		mSelectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSelection = inputTextField.getText();
				// testing print out
				System.out.println("I just saved file as: " + mSelection);
				try {
					manage.saveDocumentOnline(inputTextField.getText());
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dispose();
			}
		});
		
		// name
		// contents --> server
		// server checks if already exists
		// make new file
		

		// make a socket
		Socket ss = null;
		try {
			ss = new Socket("localhost", 6789 );
			oos = new ObjectOutputStream(ss.getOutputStream());
			ois = new ObjectInputStream(ss.getInputStream());
			
			
			// login info
//			oos.writeObject(loginInfo);
		} catch (IOException e1) {
    	
			System.out.println("Program in offline mode.");
			
		} 
        finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	
		
		
		
		mCancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSelection = "";
				dispose();
			}
		});
		Box contentBox = Box.createVerticalBox();
		contentBox.add(inputTextField);
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(mSelectButton);
		buttonBox.add(mCancelButton);
		contentBox.add(buttonBox);
		add(contentBox);
		pack();
		setVisible(true);
	}
	
	public String select() {
		mSelection = "";
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
		return mSelection;
	}
	
//	public static void main(String[] args) {
//		customChooser fd = new customChooser();
//		System.out.println(fd.select());
//	}
	
	
}

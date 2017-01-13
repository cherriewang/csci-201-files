package server;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import customui.OfficeButton;

public class TextServerGUI extends JFrame {
	public static final long serialVersionUID = 1;
	private OfficeButton submitPortButton;
	private static JTextArea textArea;
	private JScrollPane textAreaScrollPane;
	private TextServer ts;
	private TextServerGUI name;

	public TextServerGUI() {
		super("Text Server");
		initializeVariables();
		createGUI();
		setVisible(true);
		name = this;
	}
	
	private void createGUI() {
		setSize(300, 200);
		GridLayout gl = new GridLayout(2, 1);
		setLayout(gl);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textAreaScrollPane = new JScrollPane(textArea);
		add(textAreaScrollPane);
		add(submitPortButton);
	}
	
	private void initializeVariables() {

		submitPortButton = new OfficeButton("Start");
		submitPortButton.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
        	       if (submitPortButton.getText().equals("Start")) {
        	    	   
        	    	   // doing port things here
        	    	    try {
        	    	    	ts = new TextServer(name);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
        	    	    
        	    	    
        	    	   ts.start();
        	    	   addMessage("Server started on Port:" + 6789);
        	    	   submitPortButton.setText("Stop");
        	          
        	       } else if (submitPortButton.getText().equals("Stop")) {
        	    	    submitPortButton.setText("Start");
						// super cheap way of making the server stop ok
        	    	    try {
							ts.end();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						addMessage("Server stopped");
						// do more stuff
        	       }	   	
            }
        });

		
		// closes the program once you close the thing
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  System.exit(0);
			  }
		});
		
	}
	
	public void addMessage(String msg) {
		if (textArea.getText() != null && textArea.getText().trim().length() > 0) {
			textArea.append("\n" + msg);
		}
		else {
			textArea.setText(msg);
		}
	}
	
	public static void main(String [] args) {
		new TextServerGUI();
		
	}
	
}

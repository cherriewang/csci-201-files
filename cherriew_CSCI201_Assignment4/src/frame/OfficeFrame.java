package frame;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import customui.OfficeButton;
import customui.OfficeMenuBar;
import library.ImageLibrary;
import server.SaveInfoBundle;
import server.UserInfoBundle;
import textdocument.TextDocumentManager;

public class OfficeFrame extends JFrame {
	private static final long serialVersionUID = 9183816558021947333L;
	JButton login;
	private OfficeButton loginButton;
	private OfficeButton signButton;
	private OfficeButton offButton;
	private JTextField login_txt;
	private JTextField pass_txt;
	private JTextField pass_txt_2;
	private String fileName = "config_client.txt";
	private Integer portNum;
	private String hostThing;
	private JPanel textFieldPane;
	
	// this one sends the signal
	private OfficeButton realButtonS;
	private OfficeButton realButtonL;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	{
		setTitle("Trojan Office");
		setSize(640,480);
		setMinimumSize(new Dimension(640,480));
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		initializeVariables();
			
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	private void initializeVariables() {
		JPanel pane = new JPanel(new FlowLayout());
		
		loginButton = new OfficeButton("LOGIN");
		loginButton.setPreferredSize(new Dimension(100, 30));
		
		signButton = new OfficeButton("SIGNUP");
		signButton.setPreferredSize(new Dimension(100, 30));
		
		offButton = new OfficeButton("OFFLINE");
		offButton.setPreferredSize(new Dimension(100, 30));

		realButtonS = new OfficeButton("Let's go!");
		realButtonS.setPreferredSize(new Dimension(100, 30));
		
		realButtonL = new OfficeButton("Let's go!");
		realButtonL.setPreferredSize(new Dimension(100, 30));
		
		
		// ACTION LISTENER FOR LOGIN BUTTON
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
                // should add text fields here
            	pane.remove(signButton);
            	pane.remove(offButton);
            	pane.remove(loginButton);
            	
            	// creating textfields and labels
            	login_txt = new JTextField(15);
                pass_txt = new JPasswordField(15);
                login_txt.setBounds(70,30,150,20);
                pass_txt.setBounds(70,65,150,20);
                JLabel u = new JLabel("Username: ");
                JLabel p = new JLabel("Password: ");
             
                textFieldPane = new JPanel();
                textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.Y_AXIS));
                textFieldPane.add(u);      
                textFieldPane.add(login_txt);
                textFieldPane.add(p);
                textFieldPane.add(pass_txt);
                textFieldPane.add(realButtonL);
               
                pane.add(textFieldPane);
                pane.getParent().revalidate();
                pane.getParent().repaint();
            }
        });
		
		// ACTION LISTENER FOR SIGNUP BUTTON
		signButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                // should add text fields here
            	pane.remove(signButton);
            	pane.remove(offButton);
            	pane.remove(loginButton);
            	
            	// creating text fields and labels
            	login_txt = new JTextField(15);
                pass_txt = new JPasswordField(15);
                pass_txt_2 = new JPasswordField(15);
                login_txt.setBounds(70,30,150,20);
                pass_txt.setBounds(70,65,150,20);
                pass_txt_2.setBounds(70,65,150,20);
                JLabel u = new JLabel("Username: ");
                JLabel p = new JLabel("Password: ");
                JLabel r = new JLabel("Repeat: ");
             
                textFieldPane = new JPanel();
                textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.Y_AXIS));
                textFieldPane.add(u);      
                textFieldPane.add(login_txt);
                textFieldPane.add(p);
                textFieldPane.add(pass_txt);
                textFieldPane.add(r);
                textFieldPane.add(pass_txt_2);
                textFieldPane.add(realButtonS);
                     
                pane.add(textFieldPane);
                pane.getParent().revalidate();
                pane.getParent().repaint();
            }
        });
		
		// OFFLINE BUTTON ACTION LISTENER
		offButton.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	setJMenuBar(new OfficeMenuBar());
        		getContentPane().add(new TextDocumentManager(getJMenuBar(), false, ""));
            	
            	// should add text fields here
            	pane.remove(signButton);
            	pane.remove(offButton);
            	pane.remove(loginButton);
            	pane.getParent().revalidate();
                pane.getParent().repaint();
            }
        });
		
		// MORE ACTION LISTENERS
		
		// SIGNUP
		realButtonS.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                // reading in config files
            	BufferedReader br = null;
        		try {
        			br = new BufferedReader(new FileReader(fileName));
        		    String line = br.readLine();
        		    portNum = Integer.parseInt(line);
        		    hostThing = br.readLine();

        		} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
        		}	
            	
            	// we need to check that the password has one uppercase and one lower case
        		String checkPass = pass_txt.getText();
        		// checks for uppercase
        		boolean hasUppercase = !checkPass.equals(checkPass.toLowerCase());
        		boolean hasNum = checkPass.matches(".*\\d.*");
        		boolean same = false;
        		if (pass_txt_2.getText().equals(checkPass)) {
        			same = true;
        		}
        		
        		// password is okay to make
            	if (hasUppercase && hasNum && same) {
            		// hashing the password
            		String hashedPass;
            		hashedPass = Integer.toString(pass_txt.getText().hashCode());
            		
            		// make a credential bundle here, passing in TRUE because it is a sign up
                    UserInfoBundle loginInfo = new UserInfoBundle(login_txt.getText(), hashedPass, true);
                    SaveInfoBundle saveInfo = new SaveInfoBundle(login_txt.getText(), hashedPass, true);
                    
                    // make a socket here
                    Socket ss = null;
                    try {
    					ss = new Socket(hostThing, portNum);
    					oos = new ObjectOutputStream(ss.getOutputStream());
    					ois = new ObjectInputStream(ss.getInputStream());
    					oos.writeObject(loginInfo);
    					oos.writeObject(saveInfo);
    					
    					if ((boolean)ois.readObject() == true) {
    						// sign up successful
    						System.out.println("We signed up! -client");
    						
    						// makes a directory for my user when successful sign in
    						File fileForMe = new File(login_txt.getText());
    						fileForMe.mkdir();
    						
    						// clears 
    						pane.remove(textFieldPane);
    						
    						// puts in menu bar
    						setJMenuBar(new OfficeMenuBar());
        	        		getContentPane().add(new TextDocumentManager(getJMenuBar(), true, login_txt.getText()));
        	        		
    	            		pane.getParent().revalidate();
    	                    pane.getParent().repaint();
    	                    
    					} else {
    						// error pop up
    	            		JOptionPane.showMessageDialog(pane,
    	            			    "Username or password invalid",
    	            			    "Sign-up Failed",
    	            			    JOptionPane.ERROR_MESSAGE);
    					}
    					
    				} catch (IOException e1) {
    					// opens dialog box
    					JOptionPane.showMessageDialog(pane,
                			    "Server cannot be reached. Program in offline mode.",
                			    "Sign-up Failed",
                			    JOptionPane.ERROR_MESSAGE);
    					pane.remove(textFieldPane);
    					
    					// inserts offline menu bar
    					setJMenuBar(new OfficeMenuBar());
    	        		getContentPane().add(new TextDocumentManager(getJMenuBar(), false, login_txt.getText()));
    	            		
    					pane.getParent().revalidate();
                        pane.getParent().repaint();
                    	
    					System.out.println("Program in offline mode.");
    					
    				} catch (ClassNotFoundException e2){
    					e2.printStackTrace();
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
            	} else {
            		// print error message regarding password
            		JOptionPane.showMessageDialog(pane,
            			    "Password must contain at least: 1 uppercase letter, 1 number",
            			    "Sign-up Failed",
            			    JOptionPane.ERROR_MESSAGE);
            	}
 
            }
        });
		
		
		// LOGIN
		realButtonL.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	// reading in config files
            	BufferedReader br = null;
        		try {
        			br = new BufferedReader(new FileReader(fileName));
        		    String line = br.readLine();
        		    portNum = Integer.parseInt(line);
        		    hostThing = br.readLine();

        		} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
        		}	
            	
        		// hashing the password
        		String hashedPass;
        		hashedPass = Integer.toString(pass_txt.getText().hashCode());
        		
            	// make a credential bundle here, passing in FALSE because it is a login
                UserInfoBundle loginInfo = new UserInfoBundle(login_txt.getText(), hashedPass, false);
                // make a socket here
                Socket ss = null;
                try {
					ss = new Socket(hostThing, portNum);
					oos = new ObjectOutputStream(ss.getOutputStream());
					ois = new ObjectInputStream(ss.getInputStream());
					oos.writeObject(loginInfo);
					
					if ((boolean)ois.readObject() == true) {
						// login successful
						System.out.println("We logged in! -client");
						
						pane.remove(textFieldPane);
						
						// puts in menu bar
						setJMenuBar(new OfficeMenuBar());
    	        		getContentPane().add(new TextDocumentManager(getJMenuBar(), true, login_txt.getText()));
						
	            		pane.getParent().revalidate();
	                    pane.getParent().repaint();
						
					} else {
						// error pop up
	            		JOptionPane.showMessageDialog(pane,
	            			    "Username or password invalid",
	            			    "Login Failed",
	            			    JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (IOException e1) {
					// error pop up
            		JOptionPane.showMessageDialog(pane,
            			    "Server cannot be reached. Program in offline mode.",
            			    "Login Failed",
            			    JOptionPane.ERROR_MESSAGE);
            		
            		pane.remove(textFieldPane);
            		
            		// inserts offline menu bar
            		setJMenuBar(new OfficeMenuBar());
            		getContentPane().add(new TextDocumentManager(getJMenuBar(), false, login_txt.getText()));
                	
            		pane.getParent().revalidate();
                    pane.getParent().repaint();
                	            		
					System.out.println("Program in offline mode.");
				} catch (ClassNotFoundException e2){
					e2.printStackTrace();
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
                
            }
        });
		
		
			
		pane.add(loginButton);
		pane.add(signButton);
		pane.add(offButton);
		add(pane);
		
	}
	
	

	
	
	
	//http://stackoverflow.com/questions/7434845/setting-the-default-font-of-swing-program
	public static void setUIFont (javax.swing.plaf.FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	}
	
	public static void setUITabs() {
		UIManager.put("TabbedPane.shadow",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.darkShadow",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.light",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.highlight",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.tabAreaBackground",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.unselectedBackground",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.background",ThemeColors.SecondColor);
	    UIManager.put("TabbedPane.foreground",ThemeColors.WhiteColor);
	    UIManager.put("TabbedPane.focus",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.contentAreaColor",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.selected",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.selectHighlight",ThemeColors.MainColor);
	    UIManager.put("TabbedPane.borderHightlightColor",ThemeColors.MainColor);
	}
	
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			setUIFont(new javax.swing.plaf.FontUIResource(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(12.0f)));
			setUITabs();
			
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
	        Method getApplicationMethod = applicationClass.getMethod("getApplication");
	        Method setDockIconMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
	        Object macOSXApplication = getApplicationMethod.invoke(null);
	        setDockIconMethod.invoke(macOSXApplication, ImageLibrary.getImage("img/icon/office.png"));
	        
		} catch(Exception e){
			System.out.println("Warning! Cross-platform L&F not used!");
		} finally {
			/*Not necessary for Assignment 2 - but this is good practice*/
			SwingUtilities.invokeLater(() -> {
				OfficeFrame of = new OfficeFrame();
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Cursor c = toolkit.createCustomCursor(ImageLibrary.getImage("img/icon/cursor.png") , new Point(0, 0), "img");
				of.setCursor(c);
				of.setIconImage(ImageLibrary.getImage("img/icon/office.png"));
				of.setVisible(true);
				}
			);
		}
	}
}

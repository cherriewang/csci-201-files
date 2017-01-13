package customui;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JTabbedPane;

import frame.ThemeColors;


public class OfficeTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 7141608019316770268L;
	
	private static Image mBackgroundImage;
	private static final String mTitle = "Trojan Office";
	
/*	
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
	
	
	//static 
	{
		mBackgroundImage = ImageLibrary.getImage("img/backgrounds/darkgrey_panel.png");
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
            		// make a credential bundle here, passing in TRUE because it is a sign up
                    UserInfoBundle loginInfo = new UserInfoBundle(login_txt.getText(), pass_txt.getText(), true);
                    // make a socket here
                    Socket ss = null;
                    try {
    					ss = new Socket(hostThing, portNum);
    					oos = new ObjectOutputStream(ss.getOutputStream());
    					ois = new ObjectInputStream(ss.getInputStream());
    					oos.writeObject(loginInfo);
    					
    					if ((boolean)ois.readObject() == true) {
    						// sign up successful
    						System.out.println("We signed up! -client");
    						// clears 
    						pane.remove(textFieldPane);
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
            	
            	// make a credential bundle here, passing in FALSE because it is a login
                UserInfoBundle loginInfo = new UserInfoBundle(login_txt.getText(), pass_txt.getText(), false);
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
*/
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(mBackgroundImage, 0, 0, getWidth(), getHeight(), null);
		g.setColor(ThemeColors.MainColor);
		Font font = g.getFont().deriveFont(36.0f);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		int heightc = metrics.getHeight()/2;
		int widthc = metrics.stringWidth(mTitle)/2;
		g.drawString(mTitle, (getWidth()/2) - widthc, (getHeight()/2) - heightc);
		super.paintComponent(g);
	}
}

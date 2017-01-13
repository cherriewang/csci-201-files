//CSCI 201 Solution - Assignment 2
//Professor Miller Spring 2016
//University of Southern California
//Solution by Matt Carey

package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

//import textdocument.FontUIResource;
import textdocument.TextDocumentManager;

/*
 * The Office Frame simply holds the TextDocumentManager
 * It could have probably been the TextDocumentManager itself,
 * but I like to keep this small and separated.
 * */

public class OfficeFrame extends JFrame {
	public JLabel trojanOffice;
	public JPanel myPanelThing;
	private TextDocumentManager tdm;
	
	private static final long serialVersionUID = 9183816558021947333L;

	{	
		setTitle("Trojan Office");
		setSize(640,480);
		
		
		setJMenuBar(new JMenuBar() {
			  @Override
			  public void paintComponent(Graphics g) {
				  g.drawImage(Toolkit.getDefaultToolkit().getImage("Assignment3Resources/img/menu/red_button11.png"),0,0,this);
			  }
		});
		
		// create a panel
		myPanelThing = new Panel2();
		this.setContentPane(new BackgroundPainter());
		setBackground(Color.gray);
		this.setLayout(new BorderLayout());
		
		
		tdm = new TextDocumentManager(getJMenuBar());
		getContentPane().add(tdm, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		
		tdm.loadingFrame(this);
		getContentPane().setBackground(Color.GRAY); 
		
		//setLabelFrame();
		

		
		// change the cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorImage = toolkit.getImage("Assignment3Resources/img/icon/cursor.png");
		Cursor c = toolkit.createCustomCursor(cursorImage , new Point(0, 
		           0), "img");
		setCursor(c);

		// change the program icon
		try {
			ImageIcon img = new ImageIcon("Assignment3Resources/img/icon/office.png");
			setIconImage(img.getImage());
		} catch(Exception e){
			System.out.println("Icon not found!");
		}
		
		Class<?> applicationClass;
		try {
			applicationClass = Class.forName("com.apple.eawt.Application");
			Method getApplicationMethod = applicationClass.getMethod("getApplication");
			Method setDockIconMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
			Object macOSXApplication = getApplicationMethod.invoke(null);
			setDockIconMethod.invoke(macOSXApplication, ImageIO.read(new File("Assignment3Resources/img/icon/office.png")));
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// TODO: make the centered logo
//		trojanOffice = new JLabel("Trojan Office");
//		trojanOffice.setHorizontalAlignment(JLabel.CENTER);
//		add(trojanOffice);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JLabel getMyLabel() {
		return trojanOffice;
	}
	
	public void addTextDocumentManager(){
		tdm = new TextDocumentManager(getJMenuBar());
		getContentPane().add(tdm);
		setLocationRelativeTo(null);
		
		tdm.loadingFrame(this);
		getContentPane().setBackground(Color.GRAY); 
	}
	
	class Panel2 extends JPanel {
		 Panel2() {
	            // set a preferred size for the custom panel.
	            setPreferredSize(new Dimension(640,480));
	     }
		 
		 @Override
			protected void paintComponent(Graphics g) {
				String titleLabel = "Trojan Office";
				int width = this.getWidth();
				int height = this.getHeight();
				//Image img = ImageLibrary.getImage("Assignment3Resources/img/background/darkgrey_panel.png");
				Toolkit tk = Toolkit.getDefaultToolkit();
				Image img = tk.getImage("Assignment3Resources/img/background/darkgrey_panel.png");
				g.drawImage(img, 0, 0, width, height, null);
				//Font font = new Font("Times New Roman", Font.BOLD, width/12);
				Font font = getMyFont();
				g.setColor(Color.black);
				int strWidth = g.getFontMetrics(font).stringWidth(titleLabel);
				g.drawString(titleLabel, (width-strWidth)/2, -1*g.getFontMetrics(font).getHeight()/2 + height/2);
				
			}
	}
	
	
	// to get the font
	public static Font getMyFont(){
		Font myFont = null;
		Font myFontActual = null;
		try {
			InputStream myFontInput = new BufferedInputStream(new FileInputStream("Assignment3Resources/fonts/kenvector_future.ttf"));
			myFont = Font.createFont(Font.TRUETYPE_FONT, myFontInput);
			float num = 14;
			myFontActual = myFont.deriveFont(num);
		}catch (Exception ex) {
			System.err.println("Font file not loaded.");
		}
		return myFontActual;	
	}
	
	public static void main(String[] args) {
		try{//Set the UI to cross platform for better portability
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
			// CHANGING THE FONT
			Font font = getMyFont();
			UIManager.put("TextPane.font", new FontUIResource(font));
			UIManager.put("Label.font", new FontUIResource(font));
			UIManager.put("Button.font", new FontUIResource(font));
			UIManager.put("MenuItem.font", new FontUIResource(font));
			UIManager.put("Menu.font", new FontUIResource(font));
			UIManager.put("ComboBox.font", new FontUIResource(font));
			UIManager.put("TabbedPane.font", new FontUIResource(font));
			UIManager.put("TitledBorder.font", new FontUIResource(font));
			
			// changing the menubar
			UIManager.put("MenuBar.background", new Color(209, 125, 0));
			
			// tabbed pane stuff
			UIManager.put("TabbedPane.light", new Color(209, 125, 0));
			UIManager.put("TabbedPane.selected", new Color(209, 125, 0));
			UIManager.put("TabbedPane.shadow", new Color(209, 125, 0));
			UIManager.put("TabbedPane.focus", new Color(209, 125, 0));
			UIManager.put("TabbedPane.darkShadow", new Color(209, 125, 0));
			
			// does this fix the close button issue
			UIManager.put("Button.background", Color.gray);
			
			
		} catch(Exception e){
			System.out.println("Warning! Cross-platform L&F not used!");
		} finally {
			/*Not necessary for Assignment 2 - but this is good practice*/
			SwingUtilities.invokeLater(() -> { new OfficeFrame().setVisible(true); });
		}
	}
}

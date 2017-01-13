package spellcheck;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fileChooser.SingleTypeFileChooser;
import wordhelper.SpellChecker;

/*
 * The Configuration Panel takes in a SpellChecker in which
 * it can modify the current keyboard and wordlist setup
 * */

public class ConfigurationPanel extends JPanel {
	private static final long serialVersionUID = 2796044961643727103L;

	private final SpellChecker mSpellChecker;
	private final JLabel mWlFileLabel;
	private final JButton mWlFileChooserButton;
	private final JLabel mKbFileLabel;
	private final JButton mKbFileChooserButton;
	private final JButton mCloseButton;
	
	{
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBorder(new TitledBorder("Configure"));
		
//		this.add(new JPanel() {
//			@Override protected void paintComponent(Graphics g)
//			{
//			   super.paintComponent(g);
//			   setBackground(Color.GRAY);
//			}
//		});
		
		mWlFileLabel = new JLabel("N/A"){
			private static final long serialVersionUID = 1L;
			@Override
			public void setText(String text) {
				super.setText(".wl: " + text);
			}
		};
		mWlFileChooserButton = new JButton("Select WordList...");
		mWlFileChooserButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mWlFileChooserButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mWlFileChooserButton.setBorder(BorderFactory.createEmptyBorder());
		mWlFileChooserButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mWlFileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTypeFileChooser wlChooser = new SingleTypeFileChooser("Word List", ".wl");
				wlChooser.setDialogTitle("Select Wordlist...");
				int returnValue = wlChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	File file = wlChooser.getSelectedFile();
		        	if(file.exists()) {
		        		mSpellChecker.loadWordList(file);
		        		updateFields();
		        	} else {
		        		JOptionPane.showMessageDialog(null,
		        			    file.getName() + " was not found!",
		        			    "File Not Found",
		        			    JOptionPane.ERROR_MESSAGE);
		        	}
		        }
			}
		});
		
		mKbFileLabel = new JLabel("N/A"){
			private static final long serialVersionUID = 1L;
			@Override
			public void setText(String text) {
				super.setText(".kb: " + text);
			}
		};
		mKbFileChooserButton = new JButton("Select Keyboard...");
		mKbFileChooserButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mKbFileChooserButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mKbFileChooserButton.setBorder(BorderFactory.createEmptyBorder());
		mKbFileChooserButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mKbFileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTypeFileChooser wlChooser = new SingleTypeFileChooser("Keyboard", ".kb");
				wlChooser.setDialogTitle("Select Keyboard...");
				int returnValue = wlChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		        	File file = wlChooser.getSelectedFile();
		        	if(file.exists()) {
		        		mSpellChecker.loadKeyboard(file);
		        		updateFields();
		        	} else {
		        		JOptionPane.showMessageDialog(null,
		        			    file.getName() + " was not found!",
		        			    "File Not Found",
		        			    JOptionPane.ERROR_MESSAGE);
		        	}
		        }
			}
		});
		
		mCloseButton = new JButton("Close");
		mCloseButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mCloseButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mCloseButton.setBorder(BorderFactory.createEmptyBorder());
		mCloseButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ConfigurationPanel.this.getParent() != null)
				ConfigurationPanel.this.getParent().remove(ConfigurationPanel.this);
			}
		});
		
		add(mWlFileLabel);
		add(mWlFileChooserButton);
		add(Box.createVerticalStrut(20));
		add(mKbFileLabel);
		add(mKbFileChooserButton);
		add(Box.createVerticalGlue());
		add(mCloseButton);
		
		
	 
		
	}
	
	
	@Override 
	protected void paintComponent(Graphics g)
	{
	   super.paintComponent(g);
	   setBackground(Color.GRAY);
	}
	
	
	public ConfigurationPanel(SpellChecker inSpellChecker) {
		mSpellChecker = inSpellChecker;
		/*Not necessary for assignment, but loads files in another thread*/
		new Thread(() -> {
			mSpellChecker.loadWordList(new File("src/wordlist.wl"));
			mSpellChecker.loadKeyboard(new File("src/qwerty-us.kb"));
			updateFields();
		}).start();
	}

	//Updates the text labels with the currently selected file names
	private void updateFields() {
		mWlFileLabel.setText(mSpellChecker.getFileByType(SpellChecker.WORDLIST).getName());
		mKbFileLabel.setText(mSpellChecker.getFileByType(SpellChecker.KEYBOARD).getName());
	}
	
}

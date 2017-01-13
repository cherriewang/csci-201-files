package spellcheck;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.MatchResult;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;

import wordhelper.SpellChecker;

/*
 * The spell check panel runs the actual spell check
 * It takes in a text component in which it scans through
 * it offers suggestions based on its SpellChecker
 * and updates the text component based on user choice
 * */

public class SpellCheckPanel extends JPanel {
	private static final long serialVersionUID = 1203713931331563215L;

	private final SpellChecker mSpellChecker;
	private Scanner mScanner;
	private MatchResult mMatchResult;
	private JTextComponent mTextComponent;
	
	private final JLabel mSpellingLabel;
	private final JButton mIgnoreButton;
	private final JButton mAddButton;
	private final JComboBox<String> mChangeOptions;
	private final JButton mChangeButton;
	private final JButton mCloseButton;
	
	private int offset;
	
	{ 
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Spell Check"));
		
		mSpellingLabel = new JLabel("N/A"){
			private static final long serialVersionUID = -438929501552222784L;
			{setFont(getFont().deriveFont(16.0f));}
			@Override
			public void setText(String text) {
				super.setText("Spelling: " + text);
			}
		};
		
		mIgnoreButton = new JButton("Ignore");
		mIgnoreButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mIgnoreButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mIgnoreButton.setBorder(BorderFactory.createEmptyBorder());
		mIgnoreButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mIgnoreButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mIgnoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				next();
			}
		});
		
		mAddButton = new JButton("Add");
		mAddButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mAddButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mAddButton.setBorder(BorderFactory.createEmptyBorder());
		mAddButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mAddButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mSpellChecker.addWordToDictionary(mTextComponent.getText().substring(mMatchResult.start()+offset, mMatchResult.end()+offset));
					next();
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(null,
						    "Word failed to save!\n Please check configurations.",
						    "File Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		mChangeOptions = new JComboBox<String>();
		mChangeOptions.setUI( new BasicComboBoxUI() {
			 @Override 
			    protected JButton createArrowButton() {
			        
			    	JButton button = new JButton();
					button.setOpaque(false);
					button.setBackground(new Color(0,0,0,0));
			        button.setBorderPainted(false);
					try {
						    Image img = ImageIO.read(new File("Assignment3Resources/img/menu/red_sliderDown.png"));
						    button.setIcon(new ImageIcon(img));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
			    	
					return button;
				}
			
		});
		
		mChangeButton = new JButton("Change");
		mChangeButton.setBorder(BorderFactory.createEmptyBorder());
		mChangeButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mChangeButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mChangeButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mChangeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				focusSpellingError();
				String choice = mChangeOptions.getSelectedItem().toString();
				offset += choice.length() - (mMatchResult.end() - mMatchResult.start());
				mTextComponent.setEditable(true);
				mTextComponent.replaceSelection(choice);
				mTextComponent.setEditable(false);
				next();
			}
		});
		
		mCloseButton = new JButton("Close");
		mCloseButton.setIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11.png"));
		mCloseButton.setRolloverIcon(new ImageIcon("Assignment3Resources/img/menu/red_button11_selected.png"));
		mCloseButton.setBorder(BorderFactory.createEmptyBorder());
		mCloseButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		JPanel titlePanel = new JPanel();
		titlePanel.add(mSpellingLabel);
		titlePanel.setBackground(Color.GRAY);
		
		JPanel addIgnorePanel = new JPanel();
		addIgnorePanel.add(mIgnoreButton);
		addIgnorePanel.add(mAddButton);
		addIgnorePanel.setBackground(Color.GRAY);
		
		JPanel changePanel = new JPanel();
		changePanel.add(mChangeOptions);
		changePanel.add(mChangeButton);
		changePanel.setBackground(Color.GRAY);
		
		JPanel optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(addIgnorePanel, "North");
		optionsPanel.add(changePanel, "Center");
		optionsPanel.setBackground(Color.GRAY);
		
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BorderLayout());
		footerPanel.add(mCloseButton, "South");
		footerPanel.setBackground(Color.GRAY);
		
		add(titlePanel);
		add(optionsPanel);
		add(Box.createVerticalGlue());
		add(footerPanel);
		
		offset = 0;
	}
	
	@Override 
	protected void paintComponent(Graphics g)
	{
	   super.paintComponent(g);
	   setBackground(Color.GRAY);
	}
	
	SpellCheckPanel(SpellChecker inSpellChecker) {
		mSpellChecker = inSpellChecker;
	}
	
	//Starts the spell check sequence
	public void runSpellCheck(JTextComponent inTextComponent) {
		mTextComponent = inTextComponent;
		mTextComponent.setEditable(false);
		mScanner = new Scanner(mTextComponent.getText());
		mScanner.useDelimiter("([^A-Za-z])");
		offset = 0;
		next();
	}
	
	//Moves to the next word, exits if done
	private void next() {
		String word = null;
		while(mScanner.hasNext() && !mSpellChecker.isSpellingError(word = mScanner.next())) word = null;
		if(word == null) {
			close();
			JOptionPane.showMessageDialog(null,"The SpellCheck is Complete.");
			return;
		}
		word = word.toLowerCase();
		mMatchResult = mScanner.match();
		mSpellingLabel.setText(word);
		mChangeOptions.removeAllItems();
		for(String suggestion : mSpellChecker.getSpellingSuggestions(word, 10))
			mChangeOptions.addItem(suggestion);
		if(mChangeOptions.getSelectedIndex() == -1) mChangeButton.setEnabled(false);
		else mChangeButton.setEnabled(true);
		focusSpellingError();
	}
	
	//Selects the spelling error in the text component
	private void focusSpellingError() {
		mTextComponent.requestFocus();
		mTextComponent.setCaretPosition(mMatchResult.start()+offset);
		mTextComponent.setSelectionStart(mMatchResult.start()+offset);
		mTextComponent.setSelectionEnd(mMatchResult.end()+offset);
	}
	
	//Exits the spell checker
	private void close() {
		offset = 0;
		mTextComponent.setEditable(true);
		Container parent = getParent();
		if(parent != null) {
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
		}
	}

}

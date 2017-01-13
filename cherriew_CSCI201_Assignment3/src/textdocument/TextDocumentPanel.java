package textdocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicScrollBarUI;

import spellcheck.SpellCheckHelper;
import spellcheck.SpellCheckPanel;

/*
This Panel is responsible for displaying the TextComponent
It contains a menu that aids in editing the text component it owns
It also has a SpellChekerHelper that it can use to check the spelling of the text
*/
public class TextDocumentPanel extends JPanel {

	private static final long serialVersionUID = -3927634294406617454L;
	
	private final JScrollPane mScrollPane;
	private final JTextPane mTextPane;
	private File mFile;
	
	private final TextDocumentHistoryHelper mTextDocumentHistoryHelper;
	private final JMenu mEditMenu;
	private final JMenuItem mUndoItem;
	private final JMenuItem mRedoItem;
	private final JMenuItem mCutItem;
	private final JMenuItem mCopyItem;
	private final JMenuItem mPasteItem;
	private final JMenuItem mSelectAllItem;
	
	private final SpellCheckHelper mSpellCheckHelper;
	private final JMenu mSpellCheckMenu;
	private final JMenuItem mRunItem;
	private final JMenuItem mConfigureItem;
	
	{
		setLayout(new BorderLayout());
		mScrollPane = new JScrollPane();
		mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		// MAKIN SOME SCROLL PANE MAGIC HAPPEN HERE
		mScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
		    protected JButton createDecreaseButton(int orientation) {
		        JButton button = new JButton();
				button.setOpaque(false);
				button.setBackground(new Color(0,0,0,0));
		        button.setBorderPainted(false);
				try {
					    Image img = ImageIO.read(new File("Assignment3Resources/img/scrollbar/red_sliderUp.png"));
					    button.setIcon(new ImageIcon(img));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		        return button;
		    }
			
		    @Override
		    protected JButton createIncreaseButton(int orientation) {
		    	JButton button = new JButton();
				button.setOpaque(false);
				button.setBackground(new Color(0,0,0,0));
		        button.setBorderPainted(false);
				try {
					    Image img = ImageIO.read(new File("Assignment3Resources/img/scrollbar/red_sliderDown.png"));
					    button.setIcon(new ImageIcon(img));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		        return button;
		    }
			
		    @Override
		    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)  
		    {
		        g.setColor(new Color(245, 245, 245));
		        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
		        //paintDecreaseHighlight(g);
		        //paintIncreaseHighlight(g);

		    }
		    
		    @Override
		    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
		    {
		        if(thumbBounds.isEmpty() || !scrollbar.isEnabled())     {
		            return;
		        }

		        int w = 16;
		        int h = 16;

		        Graphics2D g2 = (Graphics2D) g;
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);

		        g.translate(thumbBounds.x, thumbBounds.y);

		        GradientPaint gr = new GradientPaint(2, 0, new Color(209, 125, 0), 18, 16, new Color(209, 125, 0));
		        g2.setPaint(gr);
		        g2.fill(new Ellipse2D.Double(2, 0, w-1, h-1));

		        g2.setPaint(new Color(203,207,203));
		        g2.fill(new Ellipse2D.Double(6, 4, 7, 7));

		        g.translate(-thumbBounds.x, -thumbBounds.y);
		    }
		    
		});
		
		
		mTextPane = new JTextPane();
		// changes the highlight color
		mTextPane.setSelectionColor(new Color(228, 146, 24));
		mScrollPane.getViewport().add(mTextPane);
		add(mScrollPane,"Center");
		mTextDocumentHistoryHelper = new TextDocumentHistoryHelper(mTextPane.getDocument());
		
		mEditMenu = new JMenu("Edit");
		mEditMenu.setMnemonic('E');
		

		
		mUndoItem = mTextDocumentHistoryHelper.getUndoMenuItem();
		mRedoItem = mTextDocumentHistoryHelper.getRedoMenuItem();
		mUndoItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/undo.png"));
		mRedoItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/redo.png"));
		
		mCutItem = new JMenuItem("Cut");
		mCutItem.setMnemonic('C');
		mCutItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/cut.png"));
		mCutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mCutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.cut();
			}
		});
		
		mCopyItem = new JMenuItem("Copy");
		mCopyItem.setMnemonic('C');
		mCopyItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/copy.png"));
		mCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mCopyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.copy();
			}
		});
		
		mPasteItem = new JMenuItem("Paste");
		mPasteItem.setMnemonic('P');
		mPasteItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/paste.png"));
		mPasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mPasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.paste();
			}
		});
		
		mSelectAllItem = new JMenuItem("Select All");
		mSelectAllItem.setMnemonic('S');
		mSelectAllItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/select.png"));
		mSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mSelectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.selectAll();
			}
		});
		
		mEditMenu.add(mUndoItem);
		mEditMenu.add(mRedoItem);
		mEditMenu.add(new JSeparator());
		mEditMenu.add(mCutItem);
		mEditMenu.add(mCopyItem);
		mEditMenu.add(mPasteItem);
		mEditMenu.add(new JSeparator());
		mEditMenu.add(mSelectAllItem);
		
		mSpellCheckHelper = new SpellCheckHelper();
		mSpellCheckMenu = new JMenu("SpellCheck");
		mSpellCheckMenu.setMnemonic('S');
		
		mRunItem = new JMenuItem("Run");
		mRunItem.setMnemonic('R');
		mRunItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/run.png"));
		mRunItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		mRunItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel spellCheckPanel = mSpellCheckHelper.getSpellCheckPanel();
				if(TextDocumentPanel.this.getComponentCount() == 1) {
				TextDocumentPanel.this.add(spellCheckPanel,"East");
				if(spellCheckPanel instanceof SpellCheckPanel)
					((SpellCheckPanel) spellCheckPanel).runSpellCheck(mTextPane);
				}
				revalidate();
				repaint();
			}
		});
		
		mConfigureItem = new JMenuItem("Configure");
		mConfigureItem.setMnemonic('C');
		mConfigureItem.setIcon(new ImageIcon("Assignment3Resources/img/menuitems/configure.png"));
		mConfigureItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(TextDocumentPanel.this.getComponentCount() == 1)
				TextDocumentPanel.this.add(mSpellCheckHelper.getConfigurePanel(),"East");
				revalidate();
				repaint();
			}
		});
		
		mSpellCheckMenu.add(mRunItem);
		mSpellCheckMenu.add(mConfigureItem);
		
	}
	
	//Reads in a file to the text pane so user can edit
	public TextDocumentPanel(){}
	public TextDocumentPanel(File inFile) throws IOException {
		mFile = inFile;
		FileReader fr = new FileReader(inFile);
		mTextPane.read(fr, "");
		fr.close();
	}
	
	//Gets the edit menu bar for the document
	public JMenu getEditMenu() {
		return mEditMenu;
	}
	
	//Gets the spell check menu for the document
	public JMenu getSpellCheckMenu() {
		return mSpellCheckMenu;
	}
	
	//File currently being operated on
	public File getFile() {
		return mFile;
	}
	
	//Saves the file to disk
	public void save(File inFile) throws IOException {
		mFile = inFile;
		FileWriter fw = new FileWriter(mFile);
		fw.write(mTextPane.getText());
		fw.close();
	}
	

	
	
}

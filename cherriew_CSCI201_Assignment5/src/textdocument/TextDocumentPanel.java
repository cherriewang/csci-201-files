package textdocument;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import customui.OfficePanel;
import customui.OfficeScrollBarUI;
import customui.ThemeColors;
import library.ImageLibrary;
import spellcheck.SpellCheckHelper;
import spellcheck.SpellCheckPanel;

public class TextDocumentPanel extends OfficePanel {

	private static final long serialVersionUID = -3927634294406617454L;
	
	private final JScrollPane mScrollPane;
	public final JTextPane mTextPane;
	public File mFile;
	public String olderText;
	
	public boolean isOnline = false;
	
	private final TextDocumentHistoryHelper mTextDocumentHistoryHelper;
	private final JMenu mEditMenu;
//	private final JMenuItem mUndoItem;
//	private final JMenuItem mRedoItem;
	private final JMenuItem mCutItem;
	private final JMenuItem mCopyItem;
	private final JMenuItem mPasteItem;
	private final JMenuItem mSelectAllItem;
	
	private final SpellCheckHelper mSpellCheckHelper;
	private final JMenu mSpellCheckMenu;
	private final JMenuItem mRunItem;
	private final JMenuItem mConfigureItem;
	
	private JMenu mUsersMenu;
	private JMenuItem mAddItem;
	private JMenuItem mRemoveItem;
	
	{
		setLayout(new BorderLayout());
		mScrollPane = new JScrollPane();
		mScrollPane.getVerticalScrollBar().setUI(new OfficeScrollBarUI());
		mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mTextPane = new JTextPane();
		mTextPane.setSelectionColor(ThemeColors.MainColor);
		mScrollPane.getViewport().add(mTextPane);
		add(mScrollPane,"Center");
		
		mTextDocumentHistoryHelper = new TextDocumentHistoryHelper(mTextPane.getDocument());
		
		mUsersMenu = new JMenu("Users");
		mAddItem = new JMenuItem("Add");
		mAddItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog add = new AddUserDialog(getFile());
				add.setVisible(true);
			}
		});
		mRemoveItem = new JMenuItem("Remove");
		mRemoveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog remove = new RemoveUserDialog(getFile());
				remove.setVisible(true);
			}
		});
		mUsersMenu.add(mAddItem);
		mUsersMenu.add(mRemoveItem);
		

		mEditMenu = new JMenu("Edit");
		mEditMenu.setMnemonic('E');
		
//		mUndoItem = mTextDocumentHistoryHelper.getUndoMenuItem();
//		mRedoItem = mTextDocumentHistoryHelper.getRedoMenuItem();
		
		mCutItem = new JMenuItem("Cut");
		mCutItem.setMnemonic('C');
		mCutItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/cut.png")));
		mCutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
		mCutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.cut();
			}
		});
		
		mCopyItem = new JMenuItem("Copy");
		mCopyItem.setMnemonic('C');
		mCopyItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/copy.png")));
		mCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
		mCopyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.copy();
			}
		});
		
		mPasteItem = new JMenuItem("Paste");
		mPasteItem.setMnemonic('P');
		mPasteItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/paste.png")));
		mPasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
		mPasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.paste();
			}
		});
		
		mSelectAllItem = new JMenuItem("Select All");
		mSelectAllItem.setMnemonic('S');
		mSelectAllItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/select.png")));
		mSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
		mSelectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.selectAll();
			}
		});
		
//		mEditMenu.add(mUndoItem);
//		mEditMenu.add(mRedoItem);
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
		mRunItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/run.png")));
		mRunItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		mRunItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel spellCheckPanel = mSpellCheckHelper.getSpellCheckPanel();
				TextDocumentPanel.this.add(spellCheckPanel,"East");
				if(spellCheckPanel instanceof SpellCheckPanel)
					((SpellCheckPanel) spellCheckPanel).runSpellCheck(mTextPane);
				revalidate();
				repaint();
			}
		});
		
		mConfigureItem = new JMenuItem("Configure");
		mConfigureItem.setMnemonic('C');
		mConfigureItem.setIcon(new ImageIcon(ImageLibrary.getImage("img/menuitems/configure.png")));
		mConfigureItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel.this.add(mSpellCheckHelper.getConfigurePanel(),"East");
				revalidate();
				repaint();
			}
		});
		
		mSpellCheckMenu.add(mRunItem);
		mSpellCheckMenu.add(mConfigureItem);
		
		
	}
	
	public TextDocumentPanel(){}
	public TextDocumentPanel(File inFile) throws IOException {
		mFile = inFile;
		FileReader fr = new FileReader(inFile);
		mTextPane.read(fr, "");
		olderText = mTextPane.getText();
		fr.close();
	}
	
	public JMenu getEditMenu() {
		return mEditMenu;
	}
	
	public JMenu getSpellCheckMenu() {
		return mSpellCheckMenu;
	}
	public JMenu getUsers(){
		return mUsersMenu;
	}
	
	public File getFile() {
		return mFile;
	}

	public void save(File inFile) throws IOException {
		mFile = inFile;
		FileWriter fw = new FileWriter(mFile);
		fw.write(mTextPane.getText());
		fw.close();
	}
	
}

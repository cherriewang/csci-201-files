package fileChooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;


public class openChooser extends JDialog {
	
	private String mSelection;
	private JButton mCancelButton;
	private JButton mSelectButton;
	
	
	private static customChooser dialog;
    private static String value = "";
    private JList list;
    private String[] data = {"Hello", "Testing", "One", "Two", "Three"};
	
	{	
		setSize(200,200);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mSelection = "";
		mSelectButton = new JButton("Open");
		mCancelButton = new JButton("Cancel");
		
		JTextField inputTextField = new JTextField();
		list = new JList(data); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 80));
		
		mSelectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSelection = inputTextField.getText();
				setValue(mSelection);
				mSelection = (String) list.getSelectedValue();
				// testing print out
				System.out.println("I just opened the file: " + mSelection);
				dispose();
			}
		});
		
		mCancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSelection = "";
				dispose();
			}
		});
		Box contentBox = Box.createVerticalBox();
		contentBox.add(list);
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
	
	private void setValue(String newValue) {
        value = newValue;
        list.setSelectedValue(value, true);
    }
}

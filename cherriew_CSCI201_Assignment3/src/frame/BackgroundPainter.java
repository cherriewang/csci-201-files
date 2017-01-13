package frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;


public class BackgroundPainter extends JPanel {

	BackgroundPainter() {
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
			Font font = new Font("Times New Roman", Font.BOLD, width/25);
			g.setColor(Color.black);
			int strWidth = g.getFontMetrics(font).stringWidth(titleLabel);
			g.drawString(titleLabel, (width-strWidth)/2, -1*g.getFontMetrics(font).getHeight()/2 + height/2);
			
		}
}
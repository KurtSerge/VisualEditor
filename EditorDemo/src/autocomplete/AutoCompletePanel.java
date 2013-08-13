package autocomplete;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class AutoCompletePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AutoCompletePanel() { 
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		
	    int x = 0;
	    int y = 0;
	    int w = getWidth();
	    int h = getHeight();
	    int arc = 0;

	    Graphics2D g2 = (Graphics2D) g.create();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setColor(new Color(230, 230, 230, 255));
	    g2.fillRoundRect(x, y, w, h, arc, arc);
	    g2.drawRoundRect(x, y, w, h, arc, arc); 

	    g2.dispose();
	}		
}

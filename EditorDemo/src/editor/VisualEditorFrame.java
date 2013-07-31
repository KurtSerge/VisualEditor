package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class VisualEditorFrame extends JFrame {
	private Point pt;
	private Dimension size;
	private boolean drawSelection = false;
	private Image backBuffer;
	
	public VisualEditorFrame(String in) {
		super(in);
		
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
	}
	
	@Override
	public void paint(Graphics g) {
		if(drawSelection && pt != null && size != null) {
			backBuffer = createImage(this.getWidth(), this.getHeight());
			backBuffer.getGraphics().drawRect (pt.x, pt.y, size.width, size.height);
			backBuffer.getGraphics().setColor(new Color(0, 255, 0, 255));
			backBuffer.getGraphics().fillRect(pt.x, pt.y, size.width, size.height);
			g.drawImage(backBuffer,0,0,this); // FIXME: Why flickering
		}
		
		super.paint(g);
	}
	
	public void setSelection(Point p1, Point p2) {
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		
		pt = new Point(x,y);
		size = new Dimension(width, height);
	}
	
	public void setDrawSelection(boolean drawSelection) {
		this.drawSelection = drawSelection;
	}

}

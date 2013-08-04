package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class VisualEditorFrame extends JFrame {
	private Point pt;
	private Dimension size;
	private boolean drawSelection = false;
	private Image backBuffer;
	
	private JPanel mDocumentPresentationPanel;
	private JPanel mModalPresentationPanel;
	private JTextArea mModalTextArea;
	
	public VisualEditorFrame(String in) {
		super(in);
		
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		
		this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		mDocumentPresentationPanel = new JPanel();
		mDocumentPresentationPanel.setLayout(new BoxLayout(mDocumentPresentationPanel, BoxLayout.Y_AXIS));
		mDocumentPresentationPanel.setSize(100, 100);
		
		JLayeredPane lp = getLayeredPane();
		lp.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				mDocumentPresentationPanel.setSize(VisualEditorFrame.this.getSize());
				mModalPresentationPanel.setSize(VisualEditorFrame.this.getSize());
			}			
			
			@Override
			public void componentShown(ComponentEvent arg0) {
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
		});
		
		mModalPresentationPanel = new JPanel();
		mModalPresentationPanel.setLayout(null);
		mModalPresentationPanel.setBackground(new Color(0, 171, 235));
		mModalPresentationPanel.setSize(0, 0);
		mModalPresentationPanel.setVisible(false);
		
		mModalTextArea = new JTextArea();
		mModalTextArea.setForeground(Color.white);
		mModalTextArea.setBackground(new Color(255, 255, 255, 0));
		mModalTextArea.setSize(400, 30);
		mModalTextArea.setLocation(10, 7);
		mModalTextArea.setEditable(false);	
		mModalPresentationPanel.add(mModalTextArea);

		lp.add(mDocumentPresentationPanel, 1);
		lp.add(mModalPresentationPanel, 0);
	}
	
	public JPanel getDocumentPane() { 
		return mDocumentPresentationPanel;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		mModalPresentationPanel.repaint();

//		if(drawSelection && pt != null && size != null) {
//			backBuffer = createImage(this.getWidth(), this.getHeight());
//			backBuffer.getGraphics().drawRect (pt.x, pt.y, size.width, size.height);
//			backBuffer.getGraphics().setColor(new Color(0, 255, 0, 255));
//			backBuffer.getGraphics().fillRect(pt.x, pt.y, size.width, size.height);
//			g.drawImage(backBuffer,0,0,this); // FIXME: Why flickering
//		}

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
	
	public void _resetError() { 
		mModalPresentationPanel.setVisible(false);
	}
	
	public void presentError(ConstructEditor editor, String errorString) { 
		Point topleft = editor.get_component().getLocationOnScreen();
		Dimension dim = editor.get_size();

		mModalTextArea.setText(errorString);		
		
		mModalPresentationPanel.setVisible(true);
		mModalPresentationPanel.setSize((int)mDocumentPresentationPanel.getSize().getWidth(), 30);
		mModalPresentationPanel.setLocation(new Point(0, topleft.y+(int)dim.height-45));
		mModalPresentationPanel.getParent().invalidate();
	}
}

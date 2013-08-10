package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

		mModalPresentationPanel = new JPanel();
		mModalPresentationPanel.setLayout(new BorderLayout());
		mModalPresentationPanel.setBackground(new Color(230, 230, 230));
		mModalPresentationPanel.setPreferredSize(new Dimension(100, 30));
		
		mModalTextArea = new JTextArea();
		mModalTextArea.setForeground(Color.DARK_GRAY);
		mModalTextArea.setBackground(new Color(230, 230, 230));
		mModalTextArea.setPreferredSize(new Dimension(600, 30));
		mModalTextArea.setLocation(0, 0);
		mModalTextArea.setText("");
		mModalTextArea.setEditable(false);
		mModalTextArea.setBorder(BorderFactory.createEmptyBorder(7, 8, 0, 0));

		// Setup the GridBagLayout
	    GridBagLayout gridBagLayout = new GridBagLayout();
	    setLayout(gridBagLayout);

	    // Setup the layout of the document area
	    GridBagConstraints documentConstraints = new GridBagConstraints();
	    documentConstraints.fill = GridBagConstraints.BOTH;
	    documentConstraints.gridwidth = 3;
	    documentConstraints.gridheight = 2;
	    documentConstraints.weightx = 1.0;
	    documentConstraints.weighty = 1.0;	    
	    documentConstraints.gridx = 0;
	    documentConstraints.gridy = 0;

	    // Setup the layout of the message area
	    GridBagConstraints messageConstraints = new GridBagConstraints();
	    messageConstraints.fill = GridBagConstraints.HORIZONTAL;
	    messageConstraints.insets = new Insets(10,0,0,0);
	    messageConstraints.gridwidth = 3;
	    messageConstraints.gridy = 2;
	    add(mDocumentPresentationPanel, documentConstraints);
	    add(mModalPresentationPanel, messageConstraints);
	    
	    mModalPresentationPanel.add(mModalTextArea);
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
		mModalTextArea.setText("");
	}
	
	public void presentError(String errorString) { 
		mModalTextArea.setText(errorString);	
		mModalTextArea.setForeground(Color.red);
	}
	
	public void presentInfoMessage(String message) { 
		mModalTextArea.setText(message);
		mModalTextArea.setForeground(Color.blue);
	}
}

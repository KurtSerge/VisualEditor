package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import editor.InterfaceController.EInterfaceAction;

import autocomplete.AutoCompleteDialog;
import autocomplete.IAutoCompleteListener;

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
		
		JScrollPane mpp = new JScrollPane(mDocumentPresentationPanel);
		mpp.setBorder(null);
//		mpp.add(mModalPresentationPanel);
		
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
	    add(mpp, documentConstraints);
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
	
	public void presentDebugMessage(String message) { 
		mModalTextArea.setText(message);
		mModalTextArea.setForeground(Color.darkGray);
	}
	
	private AutoCompleteDialog mAutoCompleteDialog = null;
	private ConstructEditor mAutoCompleteEditor = null;
	
	/**
	 * Presents an autocomplete dialog in the vacinity
	 * of the ConstructEditor object provided.
	 * 
	 * @param editor Editor to auto-complete into.
	 */
	public void showAutoComplete(InterfaceController controller, ConstructEditor editor, IAutoCompleteListener listener, EInterfaceAction binding)
	{ 
		if(mAutoCompleteDialog != null &&
				mAutoCompleteEditor != editor) {
			hideAutoComplete(true);
		}
		
		// Figure out top left position of dialog
		Component editorComponent = editor.get_component();
		Point locationOnScreen = editorComponent.getLocationOnScreen();
		Dimension sizeOnScreen = editorComponent.getSize();
		Point location = new Point(locationOnScreen.x, locationOnScreen.y + sizeOnScreen.height + 5);

		// Instantiate a new dialog for this editor
		if(mAutoCompleteEditor == null || mAutoCompleteEditor != editor) { 
			mAutoCompleteEditor = editor;
			mAutoCompleteDialog = new AutoCompleteDialog(controller, editor, listener, binding);		
		}
		
		// Update the location of the component
		// TODO: Constrain to desktop
		mAutoCompleteDialog.setLocation(location);
		mAutoCompleteDialog.setVisible(true);
		mAutoCompleteDialog.getEntryField().requestFocus();
	}
	
	/**
	 * Hides the auto-complete window.
	 */
	public void hideAutoComplete(boolean destroy)
	{ 
		// Make the auto-complete hidden
		if(mAutoCompleteDialog != null) { 
			mAutoCompleteDialog.setVisible(false);
			
			if(destroy == true) {
				// Destroy & clean up
				mAutoCompleteEditor = null;
				mAutoCompleteDialog = null;
			}
		}
	}
	
	/**
	 * If the dialog was hidden, but not destroyed,
	 * this can be called to bring it back into action.
	 */
	public void restoreAutoComplete() {
		if(mAutoCompleteDialog != null) { 
			mAutoCompleteDialog.setVisible(true);
		}
	}
	
	public boolean isAutoCompleteActive() { 
		return mAutoCompleteDialog != null;
	}
	
	public void onKeyPressed(KeyEvent e) { 
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { 
			hideAutoComplete(true);
			return ;
		}
		
		mAutoCompleteDialog.onKeyPressed(e);
	}
}

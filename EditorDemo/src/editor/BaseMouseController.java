package editor;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import editor.MonospaceConstructEditor.TransparentTextArea;
import editor.document.ConstructDocument;

// Using AWT because Swing "MouseListener" doesn't give coords when clicking over jtextareas
public class BaseMouseController implements AWTEventListener {
	private final BaseController bc;
	private final ConstructDocument mDocument;
	private Point mLastMouseDownPos = null;
	private final VisualEditorFrame window;
	
	 // Any dragged selection all sides < this tolerance will be ignored.  Prevents accidental selection of junk
	private static final int skMinSelection = 25;

	
	public BaseMouseController(BaseController bc, ConstructDocument document, VisualEditorFrame window) {
		this.bc = bc;
		this.mDocument = document;
		this.window = window;
	}
	
    public void eventDispatched(AWTEvent event) {
    	if (event instanceof MouseEvent) {
    		MouseEvent e = (MouseEvent)event;
    		// Mouse clicked
    		if(e.getID() == MouseEvent.MOUSE_CLICKED)  {
    			// Get clicked editors
    			Point click = MouseInfo.getPointerInfo().getLocation();
    			List<ConstructEditor> clickedEditors = new ArrayList<ConstructEditor>();
            	for(ConstructEditor editor : mDocument.getEditors()) {
               		if(editor.get_component().isDisplayable()) { // TODO:Duplicate code
            			Point topleft = editor.get_component().getLocationOnScreen();
            			Dimension dim = editor.get_size();
            		
                		Rectangle r = new Rectangle(topleft, dim);
                		if(r.contains(click)) {
                			clickedEditors.add(editor);
                		}
            		} else { 
            			System.err.println("Warning: Component for " +  editor.construct.type + "construct is not on screen!");
            		}
            	}
               		
            	// Determine deepest nested editor
            	int maxDepth = 0;
            	ConstructEditor deepEditor = null;
            	for(ConstructEditor editor : clickedEditors) {
            		int newDepth = getDepth(editor);
            		if(newDepth > maxDepth) {
            			maxDepth = newDepth; 
            			deepEditor = editor;
            		}
            	}
            	bc.selector.Select(deepEditor);
    		}
    		if(e.getID() == MouseEvent.MOUSE_PRESSED) {
    			mLastMouseDownPos = MouseInfo.getPointerInfo().getLocation();
    			window.setDrawSelection(true);
    		}
    		if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
    			Point secondPoint = MouseInfo.getPointerInfo().getLocation();
    			window.setSelection(mLastMouseDownPos, secondPoint);
    		}
    		if(e.getID() == MouseEvent.MOUSE_RELEASED) {
    			window.setDrawSelection(false);
    			if(mLastMouseDownPos == null)
    				return;
    			//System.out.println("FirstPt:" + mLastMouseDownPos + "-SecondPt:" + MouseInfo.getPointerInfo().getLocation());
    			Point secondPoint = MouseInfo.getPointerInfo().getLocation();
    			int width = Math.abs(mLastMouseDownPos.x - secondPoint.x);
    			int height = Math.abs(mLastMouseDownPos.y - secondPoint.y);
    			int x = Math.min(mLastMouseDownPos.x, secondPoint.x);
    			int y = Math.min(mLastMouseDownPos.y, secondPoint.y);
    			Rectangle selection = new Rectangle(x, y, width, height);
    			if(selection.height < skMinSelection && selection.width < skMinSelection)
    				return;
    			// Check for intersection Rect-Rect
    			List<ConstructEditor> clickedEditors = new ArrayList<ConstructEditor>();
            	for(ConstructEditor editor : mDocument.getEditors()) {
               		if(editor.get_component().isDisplayable()) { // TODO:Duplicate code
            			Point topleft = editor.get_component().getLocationOnScreen();
            			Dimension dim = editor.get_size();
            		
                		Rectangle editRect = new Rectangle(topleft, dim);
                		if(selection.contains(editRect)) {
                			clickedEditors.add(editor);
                		}
            		} else { 
            			System.err.println("Warning: Component for " +  editor.construct.type + "construct is not on screen!");
            		}
            	}
            	// Determine topmost editor
            	int maxDepth = 0;
            	ConstructEditor topEditor = null;
            	for(ConstructEditor editor : clickedEditors) {
            		int newDepth = getDepth(editor);
            		if(newDepth < maxDepth || topEditor == null) {
            			maxDepth = newDepth; 
            			topEditor = editor;
            		}
            	}

            	bc.selector.Select(topEditor);
    		}
    	}
    	
    }

    
    private int getDepth(ConstructEditor editor) {
    	Construct iter = editor.construct;
    	int i = 0;
    	for(; iter.parent != null; i++)
    		iter = iter.parent;
    	
    	return i;
    }
}
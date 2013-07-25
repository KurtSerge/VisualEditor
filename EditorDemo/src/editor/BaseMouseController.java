package editor;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import clojure.ClojureController;
import editor.document.Document;

// Using AWT because Swing "MouseListener" doesn't give coords when clicking over jtextareas
public class BaseMouseController implements AWTEventListener {
	private final BaseController bc;
	private final Document mDocument;
	
	public BaseMouseController(BaseController bc, Document document) {
		this.bc = bc;
		this.mDocument = document;
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
               		if(editor.get_component().isDisplayable()) { 
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
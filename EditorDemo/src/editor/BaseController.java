package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import json.JSONController;

import org.json.JSONObject;

public class BaseController implements KeyListener {
	private boolean delete_pressed;
	private EditSelection selector = null;
	//private final JFrame frame;
	
	public BaseController(JFrame frame, List<ConstructEditor> editors) {
		selector = new EditSelection(frame, editors);
		selector.SelectRandom();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
        if (arg0.getID() == KeyEvent.KEY_PRESSED) {
        	
    		// Check for combo key presses, such as "d + d"
			if(delete_pressed==true) {
        		delete_pressed = false;
        		switch(arg0.getKeyCode()) {
	        		case KeyEvent.VK_D: {
	        			ConstructEditor deleteMeEditor = selector.selected;

	        			if(deleteMeEditor.getParent() != null) 
	        			{
	        				if(selector.SelectAdjacentConstruct(false) == false)
	        					selector.SelectParentConstruct();
	        				
		        			deleteMeEditor.deleteMe();
		        			selector.selected.update();
	        			}
	        			break;
	        		}
        		}
        		return;
			}
			
    		// Reset first key press
    		delete_pressed = false;
    		
    		switch(arg0.getKeyCode()) {
        		case KeyEvent.VK_D:
        			delete_pressed = true;
        			break;
        		case KeyEvent.VK_UP:
        			System.out.println("Up");
        			selector.SelectAdjacentConstruct(false);
        			break;
        		case KeyEvent.VK_DOWN:
        			System.out.println("Down");
        			selector.SelectAdjacentConstruct(true);
        			break;			
        		case KeyEvent.VK_LEFT:
        			System.out.println("Left");
        			selector.SelectParentConstruct();
        			break;
        		case KeyEvent.VK_RIGHT:
        			System.out.println("Right");
        			selector.SelectFirstChildConstruct();
        			break;
        		default:
        			break;
    		}
        }

	}
        
    public ConstructEditor getSelectedEditor() {
    	return selector.selected;
    }

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	// Handles keyboard selection of constructs
	private class EditSelection {
		private final JFrame frame;
		private final List<ConstructEditor> editors;
		private ConstructEditor selected = null;
		
		public EditSelection(JFrame frame, List<ConstructEditor> editors) {
			this.frame = frame;
			this.editors = editors;
		}
		
		public void SelectRandom() {
			int select = Math.abs((new Random()).nextInt()) % editors.size();
			ConstructEditor toSelect = editors.get(select);
			Select(toSelect);
		}
		
		public void SelectParentConstruct() {
			if(selected == null)
				return;
			
			ConstructEditor parent = selected.getParent();
			if(parent == null)
				return;
			
			 Select(parent);
		}
		
		public void SelectFirstChildConstruct() {
			if(selected == null)
				return;
			if(selected.construct.children.size() == 0)
				return;
			
			Construct child = selected.construct.children.get(0);
			if(child == null)
				return;
			
			 Select(ConstructEditor.editorsByConstructs.get(child).get());
		}
		
		public boolean SelectAdjacentConstruct(boolean next) {
			if(selected == null)
				return false;
			Construct parent = selected.construct.parent;
			if(parent == null)
				return false;
			
			int myIndex = parent.children.indexOf(selected.construct);
			int selectIndex = (next) ? ++myIndex : --myIndex;
			if(selectIndex >= parent.children.size()) {
				selectIndex = 0;
			}	
			else if(selectIndex < 0) {
				selectIndex = parent.children.size()-1;
			}
			Construct newSelect = parent.children.get(selectIndex);
			if(newSelect == null)
				return false;
		
			ConstructEditor edit = ConstructEditor.editorsByConstructs.get(newSelect).get();
			if(edit == selected)
				return false;
			
			Select(edit);
			
			return true;
		}
		
		public void Select(ConstructEditor newSel) {
			if(selected != null)   {
				selected.setSelected(false);
			}
			selected = newSel;
			selected.setSelected(true);
			frame.repaint();
		}
	}
}

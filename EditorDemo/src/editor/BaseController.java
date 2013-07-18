package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import json.JSONController;

import org.json.JSONObject;

public class BaseController implements KeyListener {
	private boolean delete_pressed;
	private EditSelection selector = null;
	private BaseControllerListener theListener = null;// TODO: allow for more listeners
	private String currentInput = null;
	private Map<String, EKeyBinding> keyMap = null;
	
	public enum EKeyBinding {
		Bind_Insert,
		Bind_InsertAfter,
		Bind_InsertBefore,
		Bind_InsertWrap,
		Bind_InsertUsurp,
		Bind_InsertReplace,
		///
		Bind_DeleteAll,
	}
	

	
	public void setListener(BaseControllerListener listener) {
		theListener = listener;
	}
	
	public BaseController(JFrame frame, List<ConstructEditor> editors) {
		selector = new EditSelection(frame, editors);
		selector.SelectRandom();
		currentInput = "";
		keyMap = new HashMap<String, EKeyBinding>();
		// Internally handled hotkeys
		keyMap.put("dd", EKeyBinding.Bind_DeleteAll);
	}
	
	public void registerHotkey(EKeyBinding binding, String code) {
		keyMap.put(code, binding);
	}
	
	private void clearBindings() {
		currentInput = "";
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// Handle standard bindings************
        if (arg0.getID() == KeyEvent.KEY_PRESSED) {
    		switch(arg0.getKeyCode()) {
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
		
		// Handle combo bindings***************
		if (arg0.getID() == KeyEvent.KEY_PRESSED && arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			clearBindings();
			return;
		}
		
		// We assume all registered key bindings are 2 chars in length, but some take KeyEvent as a parameter
		if(currentInput.length() < 2)
			currentInput += arg0.getKeyChar();
		else
			currentInput += "?"; // ? For KeyEvent

		EKeyBinding bindingCheck = keyMap.get(currentInput);
		// Handled Internally vs. Handled by listener
		if(bindingCheck != null) {
			if(bindingCheck == EKeyBinding.Bind_DeleteAll) {
				DeleteAll();
				clearBindings();
			}
			else if(theListener != null) {
				theListener.receivedHotkey(bindingCheck, arg0.getKeyCode());
			}
			clearBindings();
			return;
		}
		else {
			// See if we are on the right path
			int len = currentInput.length();
			for(String str : keyMap.keySet()) {
				String check = str.substring(0, len);
				if(check.contains(currentInput))
					return;
			}
			clearBindings();
			return;
		}
	}
	
    public ConstructEditor getSelectedEditor() {
    	return selector.selected;
    }

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	// Delete construct and all children
	private void DeleteAll() {
		ConstructEditor deleteMeEditor = selector.selected;
		if(deleteMeEditor.getParent() != null) 
		{
			if(deleteMeEditor.deleteMe()) {
    			selector.selected.update();
				if(selector.SelectAdjacentConstruct(false) == false)
					selector.SelectParentConstruct();
			}
		}
	}
	
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
				if(selectIndex < 0)
					return false;
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

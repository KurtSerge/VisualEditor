package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

public class BaseController implements KeyListener {
	public EditSelection selector = null;
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
		Bind_InsertChild,
		// Below this point: Standard non-overridable bindings
		// Deletion
		Bind_DeleteAll,
		// Selection
		Bind_SelectNextSibling,
		Bind_SelectPrevSibling,
		Bind_SelectParent,
		Bind_SelectChild
	}
	

	
	public void setListener(BaseControllerListener listener) {
		theListener = listener;
	}
	
	public BaseController(JFrame frame, List<ConstructEditor> editors) {
		System.out.println("Setup BaseController");
		selector = new EditSelection(frame, editors);
	    selector.SelectRandom();
		currentInput = "";
		keyMap = new HashMap<String, EKeyBinding>();

		// Internally handled hotkeys
		this.registerHotkey(EKeyBinding.Bind_DeleteAll, String.format("%s%s", (char)KeyEvent.VK_D, (char)KeyEvent.VK_D));
		this.registerHotkey(EKeyBinding.Bind_SelectParent, String.format("%s", (char)KeyEvent.VK_LEFT));
		this.registerHotkey(EKeyBinding.Bind_SelectChild, String.format("%s", (char)KeyEvent.VK_RIGHT));
		this.registerHotkey(EKeyBinding.Bind_SelectNextSibling, String.format("%s", (char)KeyEvent.VK_DOWN));
		this.registerHotkey(EKeyBinding.Bind_SelectPrevSibling, String.format("%s", (char)KeyEvent.VK_UP));
	}
	
	// Important:Use the '?' character to indicate that an "autocomplete char" comes after the hotkey
	// Must write hotkey code in whatever form KeyEvent.getKeyText(code) uses.  Usually this means using capital letters
	public void registerHotkey(EKeyBinding binding, String code) {
		keyMap.put(code, binding);
	}
	
	private void clearBindings() {
		currentInput = "";
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		System.out.println(arg0.getKeyCode());
		
		// Handle combo bindings***************
		if (arg0.getID() == KeyEvent.KEY_PRESSED && arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			clearBindings();
			return;
		}
		
		// We assume all registered key bindings are 2 chars in length, but some take KeyEvent as a parameter
		if(currentInput.length() < 2)
			currentInput += (char)arg0.getKeyCode();
		else
			currentInput += (char)KeyEvent.VK_UNDEFINED; // ? For KeyEvent

		EKeyBinding bindingCheck = keyMap.get(currentInput);
		if(bindingCheck != null) {
		
			switch(bindingCheck) {
				case Bind_DeleteAll:
					DeleteAllSelected();
					clearBindings();
					break;
				case Bind_SelectParent:
					System.out.println("Left");
        			selector.SelectParentConstruct();
					break;
				case Bind_SelectChild:
					System.out.println("Right");
        			selector.SelectFirstChildConstruct();
					break;
				case Bind_SelectNextSibling:
					System.out.println("Down");
        			selector.SelectAdjacentConstruct(true);
					break;
				case Bind_SelectPrevSibling:
					System.out.println("Up");
        			selector.SelectAdjacentConstruct(false);
					break;
				default:
					if(theListener != null)
						theListener.receivedHotkey(this, bindingCheck, arg0.getKeyCode());
					break;
			}

			clearBindings();
			return;
		}
		else {
			// See if we are on the right path
			int len = currentInput.length();
			for(String str : keyMap.keySet()) {
				if(len > str.length())
					continue;
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
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		System.out.println("KeyTyped: " + arg0.getKeyCode());
	}

	// Delete construct and all children
	public void DeleteAllSelected() {
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
	public class EditSelection {
		private final JFrame frame;
		public final List<ConstructEditor> editors;
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
			if(newSel == null)
				return;
			if(selected != null)   {
				selected.setSelected(false);
			}
			selected = newSel;
			selected.setSelected(true);
			frame.repaint();
		}

	}
}

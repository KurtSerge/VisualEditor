package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import json.JSONController;

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
		Bind_DeleteTopmost,
		// Undo buff
		Bind_Undo,
		Bind_Redo,
		// Selection
		Bind_SelectNextSibling,
		Bind_SelectPrevSibling,
		Bind_SelectParent,
		Bind_SelectChild,
		Bind_SelectRandom// TODO: debug
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
		this.registerHotkey(EKeyBinding.Bind_DeleteTopmost, String.format("%s%s", (char)KeyEvent.VK_D, (char)KeyEvent.VK_P));
		
		this.registerHotkey(EKeyBinding.Bind_SelectParent, String.format("%s", (char)KeyEvent.VK_LEFT));
		this.registerHotkey(EKeyBinding.Bind_SelectChild, String.format("%s", (char)KeyEvent.VK_RIGHT));
		this.registerHotkey(EKeyBinding.Bind_SelectNextSibling, String.format("%s", (char)KeyEvent.VK_DOWN));
		this.registerHotkey(EKeyBinding.Bind_SelectPrevSibling, String.format("%s", (char)KeyEvent.VK_UP));
		//this.registerHotkey(EKeyBinding.Bind_SelectRandom, String.format("%s", (char)KeyEvent.VK_R));
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
					break;
				case Bind_DeleteTopmost:
					DeleteTopmost();
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
				case Bind_SelectRandom:
					selector.SelectRandom();
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
			boolean deleted = deleteMeEditor.deleteMe();
			if(deleted == true) {
				if(selector.SelectAdjacentConstruct(false) == false)
					selector.SelectParentConstruct();
			}
			getSelectedEditor().update();
		}
	}

	
	// Delete topmost construct of selected
	public void DeleteTopmost() {
		ConstructEditor deleteMeEditor = selector.selected;
		if(deleteMeEditor.getParent() != null) 
		{
			int index = deleteMeEditor.construct.parent.children.indexOf(deleteMeEditor.construct);
			int added = AddChildrenTo(deleteMeEditor.construct, deleteMeEditor.construct.parent, index);
			
			// Delete topmost construct (only if we copied some children)
			if(added > 0) {
				boolean deleted = deleteMeEditor.deleteMe();
				if(deleted == true) {
	    			selector.selected.update();
					if(selector.SelectAdjacentConstruct(false) == false)
						selector.SelectParentConstruct();
				}
			}
		}
	}
	
	// Searches for any children nested in childrenOf, and adds their tree to addToMe.
	// Returns the number of trees added to addToMe
	public int AddChildrenTo(Construct childrenOf, Construct addToMe, int index) {
		int childrenAdded = 0;
		for(Construct child : childrenOf.children) {
			Construct copy = child.deepCopy(addToMe);
			//int addIndex = .children.size();
			if(addToMe.addChild(index, copy) == false) {
				childrenAdded += AddChildrenTo(child, addToMe, index);
			}
			else {
				// Successfully add
				ConstructEditor added = JSONController.editors_from_constructs(copy);
				childrenAdded++;
				index++;
				if(added != null)  {
					selector.Select(added);
				}
			}
		}
		return childrenAdded;
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

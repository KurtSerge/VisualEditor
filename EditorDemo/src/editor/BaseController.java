package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import editor.document.ConstructDocument;

public class BaseController implements KeyListener {
	public EditSelection selector = null;
	private BaseControllerListener theListener = null;// TODO: allow for more listeners
	private String currentInput = null;
	private Map<String, EKeyBinding> keyMap = null;
	private ConstructFinder finder = null;
	
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
		// clipboard
		Bind_Copy,
		Bind_InsertPaste,
		// Selection
		Bind_SelectNextSibling,
		Bind_SelectPrevSibling,
		Bind_SelectParent,
		Bind_SelectChild,
		Bind_SelectRandom,// TODO: debug
		//
		Bind_DebugPrint,
		Bind_Find,
		Bind_FindNext,
		Bind_Save
	}
	
	private ConstructDocument mDocument;
	
	public void setListener(BaseControllerListener listener) {
		theListener = listener;
	}
	
	public BaseController(JFrame frame, ConstructDocument document) {
		System.out.println("Setup BaseController");
		selector = new EditSelection(frame, document);
	    selector.SelectRandom();
		currentInput = "";
		mDocument = document;
		keyMap = new HashMap<String, EKeyBinding>();

		// Internally handled hotkeys
		this.registerHotkey(EKeyBinding.Bind_DeleteAll, String.format("%s%s", (char)KeyEvent.VK_D, (char)KeyEvent.VK_D));
		this.registerHotkey(EKeyBinding.Bind_DeleteTopmost, String.format("%s%s", (char)KeyEvent.VK_D, (char)KeyEvent.VK_P));
		this.registerHotkey(EKeyBinding.Bind_DebugPrint, String.format("%s", (char)KeyEvent.VK_P));
		
		this.registerHotkey(EKeyBinding.Bind_SelectParent, String.format("%s", (char)KeyEvent.VK_LEFT));
		this.registerHotkey(EKeyBinding.Bind_SelectChild, String.format("%s", (char)KeyEvent.VK_RIGHT));
		this.registerHotkey(EKeyBinding.Bind_SelectNextSibling, String.format("%s", (char)KeyEvent.VK_DOWN));
		this.registerHotkey(EKeyBinding.Bind_SelectPrevSibling, String.format("%s", (char)KeyEvent.VK_UP));
		this.registerHotkey(EKeyBinding.Bind_Save, String.format("%s%s", (char)KeyEvent.VK_S, (char)KeyEvent.VK_S));
		this.registerHotkey(EKeyBinding.Bind_Find, String.format("%s%s", (char)KeyEvent.VK_F, (char)KeyEvent.VK_F));
		this.registerHotkey(EKeyBinding.Bind_FindNext, String.format("%s%s", (char)KeyEvent.VK_F, (char)KeyEvent.VK_N));
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
				case Bind_Save:
					SaveToFile();
					break;
				case Bind_Find:
					Find();
					break;
				case Bind_FindNext:
					FindNext();
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
	
	private void SaveToFile() {
		String outdir = System.getProperty("user.dir") + "\\JSONOut.json";
		//JSONController.save_json(getTopConstruct(), outdir, 4);
	}
	
	private void Find() {
		String findme = JOptionPane.showInputDialog(null,"Find:");
		if(findme != null) {
			finder = new ConstructFinder(selector.selected.construct, findme);
			FindNext();
		}
	}
	
	private void FindNext() {
		if(finder != null) {
			Construct lit = finder.nextLiteral();
			if(lit != null)
				selector.Select(ConstructEditor.editorsByConstructs.get(lit).get());
		}
	}
	
	private Construct getTopConstruct() {
		Construct iter = selector.selected.construct;
		while(iter.parent != null)
			iter = iter.parent;
			
		return iter;
	}
	
    public ConstructEditor getSelectedEditor() {
    	return selector.selected;
    }

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	// Delete construct and all children
	public void DeleteAllSelected() {
		ConstructEditor deleteMeEditor = selector.selected;
		if(deleteMeEditor.getParent() != null) 
		{
			// Determine the index of the child being deleted
			// in case we need it for selection later on
			ConstructEditor parentConstruct = deleteMeEditor.getParent(); 
			List<Construct> parentChildren = parentConstruct.construct.children;
			int childIndex = parentChildren.indexOf(deleteMeEditor.construct);
			
			// Keep track of how many siblings currently exist
			int siblingsCount = parentConstruct.construct.children.size();			
			if(deleteMeEditor.deleteMe()) {
				deleteMeEditor.getParent().update();
				selector.selected.update();
				
				int newSiblingsCount = parentConstruct.construct.children.size();
    			if(siblingsCount != newSiblingsCount) {
    				// Child was removed, move the selection
					if(selector.SelectAdjacentConstruct(false) == false)
						selector.SelectParentConstruct();
    			} else {
    				// 'Deleted' but children count didn't change, this implies
    				// that the child was actually replaced (ie, placeholder restoration)
    				Construct replacingConstruct = deleteMeEditor.getParent().construct.children.get(childIndex);
    				ConstructEditor replacingEditor = ConstructEditor.editorsByConstructs.get(replacingConstruct).get();
    				selector.Select(replacingEditor);
    			}
			} else { 
				deleteMeEditor.getParent().update();
			}
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
				ConstructEditor added = mDocument.editorsFromConstruct(copy);
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
		private final ConstructDocument mDocument;
		private ConstructEditor selected = null;
		
		public EditSelection(JFrame frame, ConstructDocument document) {
			this.frame = frame;
			mDocument = document;
		}
		
		public void SelectRandom() {
			List<ConstructEditor> editors = mDocument.getEditors();
			
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
			
			ConstructEditor lastSelected = selected;
			if(selected != null)   {
				selected.setSelected(newSel, false);
			}
			
			selected = newSel;
			selected.setSelected(lastSelected, true);
			Application.resetError();
			frame.repaint();
		}

	}
}

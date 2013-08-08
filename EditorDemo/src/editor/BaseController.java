package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.tools.javac.util.Pair;

import editor.Construct.SelectionType;
import editor.document.ConstructDocument;

public class BaseController implements KeyListener, BaseControllerListener {

	private LinkedList<BaseControllerListener> mActionListeners;
	private LinkedList<Pair<Hotkey, EKeyBinding>> mHotkeys;
	private List<Pair<Hotkey, EKeyBinding>> mCandidateKeys;
	private boolean mIsCapturingAlphaNumeric = false;
	
	// TODO: Code formatting
	private ConstructFinder finder = null;
	public EditSelection mConstructSelector = null;	

	public static class Hotkey { 
		public Hotkey(int key) { 
			mIsControlPressed = false;
			mKey = key;
		}
		
		public Hotkey(int key, boolean isControlPressed) {
			mIsControlPressed = isControlPressed;
			mKey = key;
		}
		
		public void setCaptureAlphaNumeric(boolean value) { 
			mIsAlphaNumericGlobal = value;
		}
		
		public boolean getCapturesAlphaNumeric() { 
			return mIsAlphaNumericGlobal;
		}
		
		public int getKey() { 
			return mKey;
		}
		
		public boolean isControlPressed() { 
			return mIsControlPressed;
		}
		
		public Hotkey setNext(Hotkey hotkey) { 
			mNext = hotkey;
			return hotkey;
		}
		
		public Hotkey getNext() { 
			return mNext;
		}
		
		public String serialize() { 
			String allMembers = "" + mKey + mIsControlPressed;			
			return allMembers;
		}
		
		@Override 
		public int hashCode() { 
			String serialized = serialize();
			return serialized.hashCode();
		}
		
		@Override
		public boolean equals(Object hotkey) { 
			return hotkey.hashCode() == this.hashCode();
		}
		
		private boolean mIsControlPressed;
		private Hotkey mNext;
		private boolean mIsAlphaNumericGlobal;
		private int mKey;
	}
	
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
	
	
	public void addListener(BaseControllerListener listener) {
		mActionListeners.add(listener);
		
	}
	
	public void removeListener(BaseControllerListener listener) { 
		mActionListeners.remove(listener);
	}
	
	public BaseController(JFrame frame, ConstructDocument document) {
		mActionListeners = new LinkedList<BaseControllerListener>();
		mConstructSelector = new EditSelection(frame, document);
		mHotkeys = new LinkedList<Pair<Hotkey, EKeyBinding>>();		
		mDocument = document;

		addListener(mConstructSelector);
		addListener(this);
		
		mConstructSelector.SelectRandom();

		// System hotkeys
		addHotkey(EKeyBinding.Bind_SelectNextSibling, KeyEvent.VK_TAB);
		addHotkey(EKeyBinding.Bind_DeleteAll, KeyEvent.VK_BACK_SPACE);
		addHotkey(EKeyBinding.Bind_SelectPrevSibling, KeyEvent.VK_UP);
		addHotkey(EKeyBinding.Bind_SelectNextSibling, KeyEvent.VK_DOWN);
		addHotkey(EKeyBinding.Bind_SelectParent, KeyEvent.VK_LEFT);
		addHotkey(EKeyBinding.Bind_SelectChild, KeyEvent.VK_RIGHT);
		
		// System Control-events
		addHotkey(EKeyBinding.Bind_DebugPrint, KeyEvent.VK_P, true);
		addHotkey(EKeyBinding.Bind_Save, KeyEvent.VK_S, true);
		addHotkey(EKeyBinding.Bind_DeleteTopmost, KeyEvent.VK_D, true).setNext(new Hotkey(KeyEvent.VK_P, true));
		addHotkey(EKeyBinding.Bind_Find, KeyEvent.VK_F, true).setNext(new Hotkey(KeyEvent.VK_F, true));
		addHotkey(EKeyBinding.Bind_FindNext, KeyEvent.VK_F, true).setNext(new Hotkey(KeyEvent.VK_N, true));
	}
	
	public Hotkey addHotkey(EKeyBinding bind, int key) { 
		Hotkey hotkey = new Hotkey(key, false);
		return addHotkey(bind, hotkey);
	}
	
	public Hotkey addHotkey(EKeyBinding bind, int key, boolean isControlPressed) {
		Hotkey hotkey = new Hotkey(key, isControlPressed);
		return addHotkey(bind, hotkey);
	}
	
	public Hotkey addHotkey(EKeyBinding bind, Hotkey hotkey) {
		mHotkeys.add(new Pair<Hotkey, EKeyBinding>(hotkey, bind));
		return hotkey;
	}

	
	
	
	
	
	private int getIndexOfSelectedConstruct() { 
		return mConstructSelector.selected.construct.parent.getChildren().indexOf(mConstructSelector.selected.construct);
	}
	
	private List<Pair<Hotkey, EKeyBinding>> getListOfCandiatesForRootKey(Hotkey key) {
		LinkedList<Pair<Hotkey, EKeyBinding>> hotkeys = new LinkedList<Pair<Hotkey, EKeyBinding>>();
		for(Pair<Hotkey, EKeyBinding> pair : mHotkeys) {
			if(pair.fst.equals(key)) { 
				hotkeys.add(pair);
			}
		}
		
		return hotkeys;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_ALT || 
				event.getKeyCode() == KeyEvent.VK_CONTROL || 
				event.getKeyCode() == KeyEvent.VK_META || 
				event.getKeyCode() == KeyEvent.VK_SHIFT) { 
			// If the primary key state change code is 
			// alt, control, meta or shift then ignore it
			return ;
		}
		
		if(event.getKeyCode() == KeyEvent.VK_ESCAPE)  {
			// Escape should cancel the hotkey stack
			mIsCapturingAlphaNumeric = false;
			mCandidateKeys = null;
			return ;
		}
		
		if(mIsCapturingAlphaNumeric == true) {
			// Publish this key 
			publishKeyIfAvailable(event);
			return ;
		}
		
		Hotkey emulatedHotkey = new Hotkey(event.getKeyCode(), event.isMetaDown());
		if(mCandidateKeys == null) { 
			// Fetch the parent Hotkey chains for this key
			mCandidateKeys = getListOfCandiatesForRootKey(emulatedHotkey);
			if(mCandidateKeys.size() == 0) { 

				// TODO: Support insert/remove/add (currently only 'replace')
				Construct parent = mConstructSelector.selected.construct.parent;
				int indexOfSelectedConstruct = getIndexOfSelectedConstruct();
				mConstructSelector.selected.construct.onReceivedRawKey(event);
				
				if(getIndexOfSelectedConstruct() == -1) { 
					Construct newChild = parent.getChildren().get(indexOfSelectedConstruct);
					ConstructEditor editor = mDocument.editorsFromConstruct(newChild);
					mConstructSelector.Select(SelectionType.AutoboxedReplacement, editor);
				}
			}			
			
			publishKeyIfAvailable(null);
		} else { 
			// Filter down the list of candidate keys that
			// have a next parameter that matches this key
			List<Pair<Hotkey, EKeyBinding>> filteredBindings = new LinkedList<Pair<Hotkey, EKeyBinding>>();
			for(Pair<Hotkey, EKeyBinding> bindings : mCandidateKeys) { 
				if(bindings.fst.getNext() != null && 
						bindings.fst.getNext().equals(emulatedHotkey) == true) {
					filteredBindings.add(new Pair<Hotkey, EKeyBinding>(bindings.fst.getNext(), bindings.snd));					
				}
			}

			// Filter down to the next children
			mCandidateKeys = filteredBindings;
			publishKeyIfAvailable(null);
		}
	}

	private void publishKeyIfAvailable(KeyEvent keyEvent) { 		
		if(mCandidateKeys != null && 
				mCandidateKeys.size() == 1 && 
				mCandidateKeys.get(0).fst.getNext() == null)
		{ 
			Hotkey hotkey = mCandidateKeys.get(0).fst;
			EKeyBinding binding = mCandidateKeys.get(0).snd;
			int publishKeyCode = hotkey.getKey(); 
			if(keyEvent != null) {
				publishKeyCode = keyEvent.getKeyCode();
			}
			
			// Does this key have an alpha numeric capture?
			if(mCandidateKeys.get(0).fst.getCapturesAlphaNumeric() && !mIsCapturingAlphaNumeric) {
				mIsCapturingAlphaNumeric = true;
				return ;
			}

			// Publish to all known listeners
			for(BaseControllerListener listener : mActionListeners) {
				try { 
					if(listener.receivedHotkey(this, binding, publishKeyCode) == true) { 
						break;
					}
				} catch(Exception ex) { 
					ex.printStackTrace();
				}
			}

			// Reset the candidate keys
			mIsCapturingAlphaNumeric = false;
			mCandidateKeys = null;
		}
		
		if(mCandidateKeys != null && 
			mCandidateKeys.size() == 0) {  
			mCandidateKeys = null;
		}
	}
	
	private void SaveToFile() {
		String outdir = System.getProperty("user.dir") + "\\JSONOut.json";
		//JSONController.save_json(getTopConstruct(), outdir, 4);
	}
	
	private void Find() {
		String findme = JOptionPane.showInputDialog(null,"Find:");
		if(findme != null) {
			finder = new ConstructFinder(mConstructSelector.selected.construct, findme);
			FindNext();
		}
	}
	
	private void FindNext() {
		if(finder != null) {
			Construct lit = finder.nextLiteral();
			if(lit != null)
				mConstructSelector.Select(Construct.SelectionType.Default, ConstructEditor.editorsByConstructs.get(lit).get());
		}
	}
	
	private Construct getTopConstruct() {
		Construct iter = mConstructSelector.selected.construct;
		while(iter.parent != null)
			iter = iter.parent;
			
		return iter;
	}
	
    public ConstructEditor getSelectedEditor() {
    	return mConstructSelector.selected;
    }

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	// Delete construct and all children
	public void DeleteAllSelected() {
		ConstructEditor deleteMeEditor = mConstructSelector.selected;
		if(deleteMeEditor.construct.isSoleDependantConstruct()) { 
			Construct parentConstruct = mConstructSelector.selected.construct.parent;
			deleteMeEditor = mDocument.editorsFromConstruct(parentConstruct);
		}

		if(deleteMeEditor != null && 
				deleteMeEditor.getParent() != null) 
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
				mConstructSelector.selected.update();
				
				int newSiblingsCount = parentConstruct.construct.children.size();
    			if(siblingsCount != newSiblingsCount) {
    				// Child was removed, move the selection
					if(mConstructSelector.SelectAdjacentConstruct(false) == false)
						mConstructSelector.SelectParentConstruct();
    			} else {
    				// 'Deleted' but children count didn't change, this implies
    				// that the child was actually replaced (ie, placeholder restoration)
    				Construct replacingConstruct = deleteMeEditor.getParent().construct.children.get(childIndex);
    				ConstructEditor replacingEditor = ConstructEditor.editorsByConstructs.get(replacingConstruct).get();
    				mConstructSelector.Select(Construct.SelectionType.Default, replacingEditor);
    			}
			} else { 
				deleteMeEditor.getParent().update();
			}
		}
	}

	
	// Delete topmost construct of selected
	public void DeleteTopmost() {
		ConstructEditor deleteMeEditor = mConstructSelector.selected;
		if(deleteMeEditor.getParent() != null) 
		{
			int index = deleteMeEditor.construct.parent.children.indexOf(deleteMeEditor.construct);
			int added = AddChildrenTo(deleteMeEditor.construct, deleteMeEditor.construct.parent, index);
			
			// Delete topmost construct (only if we copied some children)
			if(added > 0) {
				boolean deleted = deleteMeEditor.deleteMe();
				if(deleted == true) {
					mConstructSelector.selected.update();
					if(mConstructSelector.SelectAdjacentConstruct(false) == false)
						mConstructSelector.SelectParentConstruct();
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
					mConstructSelector.Select(Construct.SelectionType.Default, added);
				}
			}
		}
		return childrenAdded;
	}
	
	
	
	// Handles keyboard selection of constructs
	public static class EditSelection implements BaseControllerListener {
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
			Select(Construct.SelectionType.Default, toSelect);
		}
		
		public void SelectParentConstruct() {
			if(selected == null)
				return;
			
			ConstructEditor parent = selected.getParent();
			if(parent == null)
				return;
			
			 Select(Construct.SelectionType.Default, parent);
		}
		
		public void SelectFirstChildConstruct() {
			if(selected == null)
				return;
			if(selected.construct.children.size() == 0)
				return;
			
			Construct child = selected.construct.children.get(0);
			if(child == null)
				return;
			
			 Select(Construct.SelectionType.Default, ConstructEditor.editorsByConstructs.get(child).get());
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
			
			Select(Construct.SelectionType.Default, edit);
			
			return true;
		}
		
		public void Select(Construct.SelectionType selectionType, ConstructEditor newSel) {
			if(newSel == null)
				return;
			
			ConstructEditor lastSelected = selected;
			if(selected != null)   {
				selected.setSelected(newSel, false);
			}

			Construct constructForSelection = newSel.construct.getConstructForSelection(selectionType);
			ConstructEditor constructEditor = mDocument.editorsFromConstruct(constructForSelection);
			selected = constructEditor;
			selected.setSelected(lastSelected, true);
			
			Application.resetError();
			
			frame.repaint();
		}

		@Override
		public boolean receivedHotkey(BaseController baseController, EKeyBinding binding, int keyEventCode) {
			switch(binding) {
				case Bind_SelectParent:
	    			SelectParentConstruct();
					break;
					
				case Bind_SelectChild:
	    			SelectFirstChildConstruct();
					break;
					
				case Bind_SelectNextSibling:
	    			SelectAdjacentConstruct(true);
					break;
					
				case Bind_SelectPrevSibling:
	    			SelectAdjacentConstruct(false);
					break;
					
				case Bind_SelectRandom:
					SelectRandom();
					break;
					
				case Bind_DebugPrint:
					selected.construct.debugPrint();
					break;
					
				default:
					return false;
			}
			
			return true;
		}
	}
	
	@Override
	public boolean receivedHotkey(BaseController controller, EKeyBinding binding, int keyCode) {
		switch(binding) { 
			case Bind_DeleteAll:
				DeleteAllSelected();
				break;
				
			case Bind_DeleteTopmost:
				DeleteTopmost();
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
				return false;
		}
		
		return true;
	}
}

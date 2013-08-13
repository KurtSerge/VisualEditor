package editor;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import autocomplete.IAutoCompleteListener;

import com.sun.tools.javac.util.Pair;

import construct.Construct;
import construct.Construct.AutoCompleteStyle;
import construct.Construct.SelectionCause;
import editor.document.ConstructDocument;

public class BaseController implements KeyListener, BaseControllerListener, IAutoCompleteListener {

	private LinkedList<BaseControllerListener> mActionListeners;
	private LinkedList<Pair<Hotkey, EInterfaceAction>> mHotkeys;
	private List<Pair<Hotkey, EInterfaceAction>> mCandidateKeys;
	private EInterfaceAction mAutoCompletePublishBinding;
	
	// TODO: Code formatting
	private ConstructFinder finder = null;
	public EditSelection mConstructSelector = null;	
	
	public enum EInterfaceAction {
		Bind_Insert,
		Bind_InsertAfter,
		Bind_InsertBefore,
		Bind_InsertWrap,
		Bind_InsertUsurp,
		Bind_InsertReplace,
		Bind_InsertChild,
		Bind_DuplicateToAdjacent,
		Bind_DeleteAll,
		Bind_DeleteTopmost,
		Bind_Undo,
		Bind_Redo,
		Bind_Copy,
		Bind_InsertPaste,
		Bind_PresentAutoComplete,
		Bind_SelectNextSibling,
		Bind_SelectPrevSibling,
		Bind_SelectParent,
		Bind_SelectChild,
		Bind_SelectRandom,
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
	
	public Collection<BaseControllerListener> getActionListeners() { 
		return mActionListeners;
	}
	
	public BaseController(JFrame frame, ConstructDocument document) {
		mActionListeners = new LinkedList<BaseControllerListener>();
		mConstructSelector = new EditSelection(frame, document);
		mHotkeys = new LinkedList<Pair<Hotkey, EInterfaceAction>>();		
		mDocument = document;

		addListener(mConstructSelector);
		addListener(this);
		
		mConstructSelector.SelectRandom();

		// System hotkeys
		addHotkey(EInterfaceAction.Bind_SelectPrevSibling, KeyEvent.VK_TAB, false, false, true);
		addHotkey(EInterfaceAction.Bind_SelectNextSibling, KeyEvent.VK_TAB);
		addHotkey(EInterfaceAction.Bind_DuplicateToAdjacent, KeyEvent.VK_TAB, false, true);
		addHotkey(EInterfaceAction.Bind_DeleteAll, KeyEvent.VK_BACK_SPACE);
		addHotkey(EInterfaceAction.Bind_SelectPrevSibling, KeyEvent.VK_UP);
		addHotkey(EInterfaceAction.Bind_SelectNextSibling, KeyEvent.VK_DOWN);
		addHotkey(EInterfaceAction.Bind_SelectParent, KeyEvent.VK_LEFT);
		addHotkey(EInterfaceAction.Bind_SelectChild, KeyEvent.VK_RIGHT);
		
		// System Control-events
		addHotkey(EInterfaceAction.Bind_DebugPrint, KeyEvent.VK_P, true);
		addHotkey(EInterfaceAction.Bind_Save, KeyEvent.VK_S, true);
		addHotkey(EInterfaceAction.Bind_DeleteTopmost, KeyEvent.VK_D, true).setNext(new Hotkey(KeyEvent.VK_P, true));
		addHotkey(EInterfaceAction.Bind_Find, KeyEvent.VK_F, true).setNext(new Hotkey(KeyEvent.VK_F, true));
		addHotkey(EInterfaceAction.Bind_FindNext, KeyEvent.VK_F, true).setNext(new Hotkey(KeyEvent.VK_N, true));
	}
	
	public Hotkey addHotkey(EInterfaceAction bind, int key) { 
		Hotkey hotkey = new Hotkey(key, false);
		return addHotkey(bind, hotkey);
	}
	
	public Hotkey addHotkey(EInterfaceAction bind, int key, boolean isControlPressed) {
		Hotkey hotkey = new Hotkey(key, isControlPressed);
		return addHotkey(bind, hotkey);
	}
	
	public Hotkey addHotkey(EInterfaceAction bind, int key, boolean isControlPressed, boolean isAltPressed) { 
		Hotkey hotkey = new Hotkey(key, isControlPressed, isAltPressed);
		return addHotkey(bind, hotkey);
	}
	
	public Hotkey addHotkey(EInterfaceAction bind, int key, boolean isControlPressed, boolean isAltPressed, boolean isShiftPressed) { 
		Hotkey hotkey = new Hotkey(key, isControlPressed, isAltPressed, isShiftPressed);
		return addHotkey(bind, hotkey);
	}
	
	public Hotkey addHotkey(EInterfaceAction bind, Hotkey hotkey) {
		mHotkeys.add(new Pair<Hotkey, EInterfaceAction>(hotkey, bind));
		return hotkey;
	}

	private int getIndexOfSelectedConstruct() { 
		if(mConstructSelector.selected != null &&
				mConstructSelector.selected.construct.parent != null)
		{ 
			return mConstructSelector.selected.construct.parent.getChildren().indexOf(mConstructSelector.selected.construct);	
		}
		
		return -2;
	}
	
	private List<Pair<Hotkey, EInterfaceAction>> getListOfCandiatesForRootKey(Hotkey key) {
		LinkedList<Pair<Hotkey, EInterfaceAction>> hotkeys = new LinkedList<Pair<Hotkey, EInterfaceAction>>();
		for(Pair<Hotkey, EInterfaceAction> pair : mHotkeys) {
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
		
		if(Application.getApplication().isAutoCompleteActive()) {
			Application.getApplication().onKeyPressed(event);
			return ;
		}
		
		if(event.getKeyCode() == KeyEvent.VK_ESCAPE)  {
			// Escape should cancel the hotkey stack
			Application.getApplication().hideAutoComplete(true);
			mCandidateKeys = null;
			return ;
		}
		
		Hotkey emulatedHotkey = new Hotkey(event.getKeyCode(), 
											event.isMetaDown(), 
											event.isAltDown(),
											event.isShiftDown());
		if(mCandidateKeys == null) { 
			// Fetch the parent Hotkey chains for this key
			mCandidateKeys = getListOfCandiatesForRootKey(emulatedHotkey);
			if(mCandidateKeys.size() == 0) { 
				// TODO: Support insert/remove/add (currently only 'replace')
				Construct parent = mConstructSelector.selected.construct.parent;
				int indexOfSelectedConstruct = getIndexOfSelectedConstruct();
				mConstructSelector.selected.construct.onReceivedKeyEvent(event, false);
				
				if(getIndexOfSelectedConstruct() == -1) { 
					Construct newChild = parent.getChildren().get(indexOfSelectedConstruct);
					ConstructEditor editor = mDocument.editorsFromConstruct(newChild);
					mConstructSelector.Select(SelectionCause.SelectedReplacementDiscoveredDuringKeyEvent, editor);
				}
			}			
			
			publishKeyIfAvailable(null);
		} else { 
			// Filter down the list of candidate keys that
			// have a next parameter that matches this key
			List<Pair<Hotkey, EInterfaceAction>> filteredBindings = new LinkedList<Pair<Hotkey, EInterfaceAction>>();
			for(Pair<Hotkey, EInterfaceAction> bindings : mCandidateKeys) { 
				if(bindings.fst.getNext() != null && 
						bindings.fst.getNext().equals(emulatedHotkey) == true) {
					filteredBindings.add(new Pair<Hotkey, EInterfaceAction>(bindings.fst.getNext(), bindings.snd));					
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
			EInterfaceAction binding = mCandidateKeys.get(0).snd;
			
			// Does this key have an alpha numeric capture?
			if(mCandidateKeys.get(0).fst.followsWithAutoComplete()) {
				mAutoCompletePublishBinding = binding;
				Application.getApplication().showAutoComplete(this, getSelectedEditor(), this);
			} else {
				publishAction(binding, null);
			}

			// Reset the candidate keys
			mCandidateKeys = null;
		}
		
		if(mCandidateKeys != null && 
			mCandidateKeys.size() == 0) { 
			// If there are no candidate keys, then 
			// reset our candidate keys array
			mCandidateKeys = null;
		}
	}
	
	private void SaveToFile() {
//		String outdir = System.getProperty("user.dir") + "\\JSONOut.json";
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
				mConstructSelector.Select(Construct.SelectionCause.SelectedDuringFind, mDocument.editorsFromConstruct(lit));
		}
	}
	
//	private Construct getTopConstruct() {
//		Construct iter = mConstructSelector.selected.construct;
//		while(iter.parent != null)
//			iter = iter.parent;
//			
//		return iter;
//	}
	
    public ConstructEditor getSelectedEditor() {
    	return mConstructSelector.selected;
    }

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	// Set focus to topmost monospace editor
	public void requestTopFocus() {
		Component iter = mConstructSelector.selected.get_component();
		while(iter.getName() != "mono_base") {
			iter = iter.getParent();
			if(iter == null)
				return;
		}
		
		iter.requestFocus();
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
					if(mConstructSelector.SelectAdjacentConstruct(false) == false) {
						mConstructSelector.Select(Construct.SelectionCause.SelectedAfterDeletingChild, deleteMeEditor.getParent());
					}
    			} else {
    				System.out.println("'deleted' but 'replaced'");
    				
    				// 'Deleted' but children count didn't change, this implies
    				// that the child was actually replaced (ie, placeholder restoration)
    				Construct replacingConstruct = deleteMeEditor.getParent().construct.children.get(childIndex);
    				ConstructEditor replacingEditor = mDocument.editorsFromConstruct(replacingConstruct);
    				mConstructSelector.Select(Construct.SelectionCause.SelectedInPlaceOfDeletedConstruct, replacingEditor);
    			}
			} else { 
				deleteMeEditor.getParent().update();
			}
		}
		
		requestTopFocus();
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
					mConstructSelector.Select(Construct.SelectionCause.SelectedAfterInsert, added);
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
			ConstructEditorStore constructStore = mDocument.getConstructEditorStore();
			List<WeakReference<ConstructEditor>> constructEditors = constructStore.getEditors();
			
			int select = Math.abs((new Random()).nextInt()) % constructEditors.size();
			WeakReference<ConstructEditor> toSelect = constructEditors.get(select);
			Select(Construct.SelectionCause.SelectedRandomly, toSelect.get());
		}
		
		public void SelectParentConstruct() {
			if(selected == null)
				return;
			
			ConstructEditor parent = selected.getParent();
			if(parent == null)
				return;
			
			 Select(Construct.SelectionCause.SelectedParent, parent);
		}
		
		public void SelectFirstChildConstruct() {
			if(selected == null)
				return;
			if(selected.construct.children.size() == 0)
				return;
			
			Construct child = selected.construct.children.get(0);
			if(child == null)
				return;
			
			 Select(Construct.SelectionCause.SelectedFirstChild, mDocument.editorsFromConstruct(child));
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
			} else if(selectIndex < 0) {
				selectIndex = parent.children.size()-1;
				if(selectIndex < 0)
					return false;
			}
			
			Construct newSelect = parent.children.get(selectIndex);
			if(newSelect == null)
				return false;
		
			ConstructEditor edit = mDocument.editorsFromConstruct(newSelect);
			if(edit == null) {
				return false;
			}
			
			Select(Construct.SelectionCause.SelectedAdjacentConstruct, edit);
			
			return true;
		}
		
		public void Select(Construct.SelectionCause selectionType, ConstructEditor newSel) {
			if(newSel == null)
				return;
			
			Application.resetError();
			Application.getApplication().hideAutoComplete(true);
			
			
			ConstructEditor lastSelected = selected;
			if(selected != null)   {
				selected.setSelected(selectionType, newSel, false);
			}
			
			if(selected != null)
				selected.update();
			
			Construct constructForSelection = newSel.construct.getConstructForSelection(selectionType);			
			ConstructEditor constructEditor = mDocument.editorsFromConstruct(constructForSelection);
			constructEditor.update();
			selected = constructEditor;
			selected.setSelected(selectionType, lastSelected, true);

			frame.invalidate();
			frame.repaint();
		}

		@Override
		public boolean onReceievedAction(BaseController baseController, EInterfaceAction binding, SimpleAutoCompleteEntry construct) {
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
	public boolean onReceievedAction(BaseController controller, EInterfaceAction binding, SimpleAutoCompleteEntry construct) {
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
				
			case Bind_PresentAutoComplete:
				if(mConstructSelector.selected.construct.canPresentAutoComplete()) { 
					Application.presentAutoComplete(controller, mConstructSelector.selected, mConstructSelector.selected);
				}
				break;
				
			default: 
				return false;
		}
		
		return true;
	}

	@Override
	public void onAutoCompleteCreateReplacement(BaseController controller, SimpleAutoCompleteEntry entry) {
		
		System.out.println("Publishing " + mAutoCompletePublishBinding.toString());
		
		publishAction(mAutoCompletePublishBinding, entry);
		
		Application.getApplication().hideAutoComplete(true);
		
//		ConstructEditor editor = mDocument.editorsFromConstruct(construct);
//		WeakReference<ConstructEditor> weakParentEditor = mDocument.getConstructEditorStore().get(construct.parent);
//		if(weakParentEditor != null) { 
//			ConstructEditor parentEditor = weakParentEditor.get();
//			if(parentEditor != null) {
//				if(construct.parent.replaceChild(getSelectedEditor().construct, construct)) {
//					controller.mConstructSelector.Select(SelectionCause.Selected, editor);
//				} else { 
//					Application.showErrorMessage("Failed to autocomplete: parent denied replacement");
//				}
//			} else { 
//				Application.showErrorMessage("Failed to autocomplete: weak parent editor resolved to null");
//			}
//		} else { 
//			Application.showErrorMessage("Failed to autocomplete: parent editor not found");
//		}
//		
//				
	}
	
	private void publishAction(EInterfaceAction action, SimpleAutoCompleteEntry entry) { 
		for(BaseControllerListener listener : getActionListeners()) { 
			if(listener.onReceievedAction(this, action, entry) == true) 
				break;
		}
	}
}

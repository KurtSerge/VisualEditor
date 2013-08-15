package editor;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import util.Pair;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import autocomplete.IAutoCompleteListener;

import construct.Construct;
import construct.Construct.SelectionCause;
import editor.document.ConstructDocument;

/**
 * ! This class used to be BaseController, it is being repurposed as InterfaceController.
 * 
 * Controls basic interface functionality, including hotkeys, finding
 * and of constructs.
 * 
 * @author Christopher Lord
 */
public class InterfaceController implements KeyListener, IInterfaceActionListener, IAutoCompleteListener {
	
	private LinkedList<IInterfaceActionListener> mActionListeners;
	private LinkedList<Pair<Hotkey, EInterfaceAction>> mHotkeys;
	private List<Pair<Hotkey, EInterfaceAction>> mHotkeySequenceCandidates;
	private EInterfaceAction mAutoCompletePublishBinding;
	
	// TODO: Code formatting
	private ConstructFinder finder;
	public ConstructNavigator mConstructSelector = null;	
	
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

	
	public InterfaceController(JFrame frame, ConstructDocument document) {
		mActionListeners = new LinkedList<IInterfaceActionListener>();
		mConstructSelector = new ConstructNavigator(frame, document);
		mHotkeys = new LinkedList<Pair<Hotkey, EInterfaceAction>>();	

		mDocument = document;

		addInterfaceActionListener(mConstructSelector);
		addInterfaceActionListener(this);
		
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

	/** 
	 * @category InterfaceActionListener
	 * @param listener Listener to be added to the interface action listeners list.
	 */
	public void addInterfaceActionListener(IInterfaceActionListener listener) {
		mActionListeners.add(listener);
	}
	
	/**
	 * @category InterfaceActionListener
	 * @param listener Listener to be removed from the interface action listeners list.
	 */
	public void removeInterfaceActionListener(IInterfaceActionListener listener) { 
		mActionListeners.remove(listener);
	}
	
	/**
	 * @category InterfaceActionListener
	 * @return All interface action listeners.
	 */
	public Collection<IInterfaceActionListener> getInterfaceActionListeners() { 
		return mActionListeners;
	}	
	
	/**
	 * @category Hotkeys
	 * @param bind Interface action to bind. 
	 * @param key Key code to bind
	 * @return Hotkey for chaining
	 */
	public Hotkey addHotkey(EInterfaceAction bind, int key) { 
		Hotkey hotkey = new Hotkey(key, false);
		return addHotkey(bind, hotkey);
	}
	
	/**
	 * @category Hotkeys
	 * @param bind Interface action to bind. 
	 * @param key Key code to bind
	 * @param isControlPressed true if hotkey is triggered with control pressed
	 * @return Hotkey for chaining
	 */	
	public Hotkey addHotkey(EInterfaceAction bind, int key, boolean isControlPressed) {
		Hotkey hotkey = new Hotkey(key, isControlPressed);
		return addHotkey(bind, hotkey);
	}
	
	/**
	 * @category Hotkeys
	 * @param bind Interface action to bind. 
	 * @param key Key code to bind
	 * @param isControlPressed true if hotkey is triggered with control pressed
	 * @param isAltPressed true if hotkey is triggered with alt pressed
	 * @return Hotkey for chaining
	 */		
	public Hotkey addHotkey(EInterfaceAction bind, int key, boolean isControlPressed, boolean isAltPressed) { 
		Hotkey hotkey = new Hotkey(key, isControlPressed, isAltPressed);
		return addHotkey(bind, hotkey);
	}
	
	/**
	 * @category Hotkeys
	 * @param bind Interface action to bind. 
	 * @param key Key code to bind
	 * @param isControlPressed true if hotkey is triggered with control pressed
	 * @param isAltPressed true if hotkey is triggered with alt pressed
	 * @param isShiftPressed true if hotkey is triggered with shift pressed
	 * @return Hotkey for chaining
	 */	
	public Hotkey addHotkey(EInterfaceAction bind, int key, boolean isControlPressed, boolean isAltPressed, boolean isShiftPressed) { 
		Hotkey hotkey = new Hotkey(key, isControlPressed, isAltPressed, isShiftPressed);
		return addHotkey(bind, hotkey);
	}
	
	/**
	 * @category Hotkeys
	 * @param bind Interface action to bind. 
	 * @param hotkey Hotkey object to bind.
	 * @return Hotkey for chaining
	 */		
	public Hotkey addHotkey(EInterfaceAction bind, Hotkey hotkey) {
		mHotkeys.add(new Pair<Hotkey, EInterfaceAction>(hotkey, bind));
		return hotkey;
	}

	private int getIndexOfSelectedConstruct() { 
		if(mConstructSelector.getSelected() != null &&
				mConstructSelector.getSelected().construct.parent != null)
		{ 
			return mConstructSelector.getSelected().construct.parent.getChildren().indexOf(mConstructSelector.getSelected().construct);	
		}
		
		return -2;
	}
	
	private List<Pair<Hotkey, EInterfaceAction>> getListOfCandiatesForRootKey(Hotkey key) {
		LinkedList<Pair<Hotkey, EInterfaceAction>> hotkeys = new LinkedList<Pair<Hotkey, EInterfaceAction>>();
		for(Pair<Hotkey, EInterfaceAction> pair : mHotkeys) {
			if(pair.getFirst().equals(key)) { 
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
			event.getKeyCode() == KeyEvent.VK_SHIFT)
		{ 
			// Ignore the state changing calls of alt,
			// control, meta and shift as these will 
			// break the hotkey sequence.
			return ;
		}
		
		if(Application.getApplication().isAutoCompleteActive()) {
			// Redirect all keys to the hotkey system as it is open
			Application.getApplication().onKeyPressed(event);
			return ;
		}
		
		if(event.getKeyCode() == KeyEvent.VK_ESCAPE)  {
			// Escape should cancel the hotkey sequence
			Application.getApplication().hideAutoComplete(true);
			mHotkeySequenceCandidates = null;
			return ;
		}
		
		boolean isPrimaryTriggerDown = event.isControlDown(); 
		if(isMac()) { 
			isPrimaryTriggerDown = event.isMetaDown();
		}
		
		Hotkey emulatedHotkey = new Hotkey(event.getKeyCode(), isPrimaryTriggerDown, event.isAltDown(), event.isShiftDown());
		if(mHotkeySequenceCandidates == null) {
			// There is no current sequence, so find all candidates
			// that start with the current hotkey instance
			mHotkeySequenceCandidates = getListOfCandiatesForRootKey(emulatedHotkey);
			if(mHotkeySequenceCandidates.size() == 0) {
				onHotkeySequenceStartFailure(event);
				mHotkeySequenceCandidates = null;
			} else { 		
				onHotkeySequenceProgressed();
			}
		} else { 
			// Filter down the list of candidate keys that
			// have a next parameter that matches this key
			List<Pair<Hotkey, EInterfaceAction>> filteredBindings = new LinkedList<Pair<Hotkey, EInterfaceAction>>();
			for(Pair<Hotkey, EInterfaceAction> bindings : mHotkeySequenceCandidates) { 
				if(bindings.getFirst().getNext() != null && 
						bindings.getFirst().getNext().equals(emulatedHotkey) == true) {
					filteredBindings.add(new Pair<Hotkey, EInterfaceAction>(bindings.getFirst().getNext(), bindings.getSecond()));					
				}
			}

			// Filter down to the next children
			mHotkeySequenceCandidates = filteredBindings;
			onHotkeySequenceProgressed();
		}
	}
	
	/**
	 * Called when a HotKey sequence does not get started.
	 * In this case, we redirect the KeyEvent down to the
	 * currently selected ConstructEditor instance.
	 * 
	 * @param event KeyEvent not handled by HotKey system
	 */
	private void onHotkeySequenceStartFailure(KeyEvent event) { 
		// There are no candidate hotkeys available, we'll redirect
		// this KeyEvent to the currently selected candidate
		Construct parent = mConstructSelector.getSelected().construct.parent;
		int indexOfSelectedConstruct = getIndexOfSelectedConstruct();
		mConstructSelector.getSelected().construct.onReceivedKeyEvent(event, false);
		
		if(getIndexOfSelectedConstruct() == -1) { 
			Construct newChild = parent.getChildren().get(indexOfSelectedConstruct);
			ConstructEditor editor = mDocument.editorsFromConstruct(newChild);
			mConstructSelector.Select(SelectionCause.SelectedReplacementDiscoveredDuringKeyEvent, editor);
		}		
	}

	/**
	 * Called when the HotKey sequence has been
	 * updated and the sequence needs re-evaluation
	 * as to if it should be published or not.
	 */
	private void onHotkeySequenceProgressed() { 		
		if(mHotkeySequenceCandidates != null) {
			if(mHotkeySequenceCandidates.size() == 1 && 
				mHotkeySequenceCandidates.get(0).getFirst().getNext() == null) {
				// There is only one candidate left and it has no further
				// hotkeys in the sequence, publish this and reset sequence
				EInterfaceAction binding = mHotkeySequenceCandidates.get(0).getSecond();
				
				// Does this key have AutoComplete capture?
				if(mHotkeySequenceCandidates.get(0).getFirst().followsWithAutoComplete()) {
					mAutoCompletePublishBinding = binding;
					Application.getApplication().showAutoComplete(this, getSelectedEditor(), this);
				} else {
					publishAction(binding, null);
				}
				
				// Reset the candidate keys
				mHotkeySequenceCandidates = null;
				return ;
			}
			
			if(mHotkeySequenceCandidates.size() == 0) { 
				mHotkeySequenceCandidates = null;
				return ;
			}			
		}
	}
	
	/**
	 * Once AutoComplete has chosen an entry, we'll simply
	 * publish that along to our InterfaceActionListener's
	 */
	@Override
	public void onAutoCompleteCreateReplacement(InterfaceController controller, SimpleAutoCompleteEntry entry) {
		publishAction(mAutoCompletePublishBinding, entry);
		Application.getApplication().hideAutoComplete(true);		
	}	
	
	private void SaveToFile() {
//		String outdir = System.getProperty("user.dir") + "\\JSONOut.json";
		//JSONController.save_json(getTopConstruct(), outdir, 4);
	}
	
	private void Find() {
		String findme = JOptionPane.showInputDialog(null,"Find:");
		if(findme != null) {
			finder = new ConstructFinder(mConstructSelector.getSelected().construct, findme);
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

	
    public ConstructEditor getSelectedEditor() {
    	return mConstructSelector.getSelected();
    }
	
	// Set focus to topmost monospace editor
	public void requestTopFocus() {
		Component iter = mConstructSelector.getSelected().get_component();
		while(iter.getName() != "mono_base") {
			iter = iter.getParent();
			if(iter == null)
				return;
		}
		
		iter.requestFocus();
	}

	// Delete construct and all children
	public void deleteSelected() {
		ConstructEditor deleteMeEditor = mConstructSelector.getSelected();
		

		if(deleteMeEditor.construct.isSoleDependantConstruct()) { 
			Construct parentConstruct = mConstructSelector.getSelected().construct.parent;
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
			if(deleteMeEditor.delete()) {
				deleteMeEditor.getParent().update();
				mConstructSelector.getSelected().update();
				
				int newSiblingsCount = parentConstruct.construct.children.size();
    			if(siblingsCount != newSiblingsCount) {
    				// Child was removed, move the selection
					if(mConstructSelector.SelectAdjacentConstruct(false) == false) {
						mConstructSelector.Select(Construct.SelectionCause.SelectedAfterDeletingChild, deleteMeEditor.getParent());
					}
    			} else {
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
		
		if(deleteMeEditor.getParent() != null) { 
			deleteMeEditor.getParent().update();
		}
		
		requestTopFocus();
	}

	
	// Delete topmost construct of selected
	private void DeleteTopmost() {
		ConstructEditor deleteMeEditor = mConstructSelector.getSelected();
		if(deleteMeEditor.getParent() != null) 
		{
			int index = deleteMeEditor.construct.parent.children.indexOf(deleteMeEditor.construct);
			int added = AddChildrenTo(deleteMeEditor.construct, deleteMeEditor.construct.parent, index);
			
			// Delete topmost construct (only if we copied some children)
			if(added > 0) {
				boolean deleted = deleteMeEditor.delete();
				if(deleted == true) {
					mConstructSelector.getSelected().update();
					if(mConstructSelector.SelectAdjacentConstruct(false) == false)
						mConstructSelector.SelectParentConstruct();
				}
			}
		}
	}
	
	// Searches for any children nested in childrenOf, and adds their tree to addToMe.
	// Returns the number of trees added to addToMe
	private int AddChildrenTo(Construct childrenOf, Construct addToMe, int index) {
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
	
	@Override
	public boolean onReceievedAction(InterfaceController controller, EInterfaceAction binding, SimpleAutoCompleteEntry construct) {
		switch(binding) { 
			case Bind_DeleteAll:
				deleteSelected();
				break;
				
			case Bind_DeleteTopmost:
				DeleteTopmost();
				break;				
				
			case Bind_Save:
				SaveToFile();
				break;
				
			case Bind_Copy:
				Application.getApplication().getClipboard().copy(getSelectedEditor().construct);
				break;
				
			case Bind_InsertPaste:
				break;
				
			case Bind_Find:
				Find();
				break;
				
			case Bind_FindNext:
				FindNext();
				break;
				
			case Bind_PresentAutoComplete:
				if(mConstructSelector.getSelected().construct.canPresentAutoComplete()) { 
					Application.presentAutoComplete(controller, mConstructSelector.getSelected(), mConstructSelector.getSelected());
				}
				break;
				
			default: 
				return false;
		}
		
		return true;
	}

	private void publishAction(EInterfaceAction action, SimpleAutoCompleteEntry entry) {
		System.out.println("Publishing " + action.toString());		
		for(IInterfaceActionListener listener : getInterfaceActionListeners()) { 
			if(listener.onReceievedAction(this, action, entry) == true) 
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }	
	
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}
}

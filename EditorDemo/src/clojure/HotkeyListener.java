package clojure;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
import clojure.ClojureConstruct.ConstructType;
import clojure.constructs.BooleanConstruct;
import clojure.constructs.CharacterConstruct;
import clojure.constructs.DoubleConstruct;
import clojure.constructs.IntegerConstruct;
import clojure.constructs.KeywordConstruct;
import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import clojure.constructs.containers.MapConstruct;
import clojure.constructs.containers.VectorConstruct;
import clojure.constructs.special.KeywordExpressionPairConstruct;
import construct.Construct;
import construct.Construct.SelectionCause;
import editor.Application;
import editor.ConstructEditor;
import editor.IInterfaceActionListener;
import editor.InterfaceController;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ConstructDocument;

public class HotkeyListener implements IInterfaceActionListener {
	private final ConstructDocument mDocument;
	
	public HotkeyListener(ConstructDocument document) { 
		mDocument = document;
	}
	
	
	@Override
	public boolean onReceievedAction(InterfaceController controller, EInterfaceAction binding, SimpleAutoCompleteEntry entry) {
		if(binding == EInterfaceAction.Bind_Undo) { 
			mDocument.undo();
			return true;
		} else if(binding == EInterfaceAction.Bind_Redo) { 
			mDocument.redo();
			return true;
		}
		
		if(binding == EInterfaceAction.Bind_DebugPrint) {
			mDocument.debugPrint();
			return true;
		}
		
		if(binding == EInterfaceAction.Bind_DuplicateToAdjacent) {
			ConstructEditor selectedEditor = controller.getSelectedEditor();
			ClojureConstruct parent = (ClojureConstruct) selectedEditor.construct.getParentForBinding(binding);
			if(parent == null || parent.canPerformAction(binding, selectedEditor.construct) == false) {
				// Consume event: no parent / action cannot be performed
				System.err.println("Cannot duplicate adjacent with " + selectedEditor.construct.type + " (parent " + parent.type + ")");
				return true;
			}
			
			Construct selected = controller.getSelectedEditor().construct;
			Construct created = ClojureConstructFactory.duplicate(selected.getClass(), selected.getDocument(), parent);
			
			if(created != null) { 
				// If the construct duplicated (certain forms only), create an editor
				// for it and then place the focus onto this new construct 
				int indexOfExisting = parent.getChildren().indexOf(selected);
				if(parent.addChild(indexOfExisting + 1, created)) { 
					ConstructEditor added = mDocument.editorsFromConstruct(created);
					controller.mConstructSelector.Select(SelectionCause.SelectedAfterDuplicatingSibling, added);
				} else { 
					System.err.println("TODO: Clojure/Bind_DuplicateToAdjacent, addChild failed");
				}
			}
		} else { 
			String error = handleInsert(controller, binding, entry);
			if(error != null) 
				System.err.println("Clojure HotKeyListener error: " + error);
		}
		
		return true;
	}

	private String handleInsert(InterfaceController controller, EInterfaceAction binding, SimpleAutoCompleteEntry entry) {
		// Select a valid parent to select
		ConstructEditor selectedEditor = controller.getSelectedEditor();
		ClojureConstruct parent = (ClojureConstruct) selectedEditor.construct.getParentForBinding(binding);
		if(parent == null || parent.canPerformAction(binding, selectedEditor.construct) == false) {
			// Consume event: no parent / action cannot be performed			
			return "Action cannot be performed at this time";
		}

		// Initialize
		SelectionCause selectionCause = SelectionCause.SelectedAfterInsert;
		int indexOfCurrentSelectionInParent = parent.getChildren().indexOf(controller.getSelectedEditor().construct);
		Construct constructForSelecting = null;
		
		if(binding == EInterfaceAction.Bind_InsertPaste) { 
			// We are going to be taking from the clipboard
			// TODO: Validate Clipboard contents
			constructForSelecting = Application.getApplication().getClipboard().getCopyToPaste(parent); 
		} else if(entry != null) {
			// Allow the AutoCompleteEntry to create the construct
			// that we require for this EInterfaceAction
			constructForSelecting = entry.create(mDocument, parent);
		} else { 
			return "Failed to create new construct for inserting";
		}

		switch(binding) {
			case Bind_InsertAfter:
				int indexForInsert = indexOfCurrentSelectionInParent + 1;
				parent.addChild(indexForInsert, constructForSelecting);
				break;
			
			case Bind_InsertBefore:
				parent.addChild(indexOfCurrentSelectionInParent, constructForSelecting);
				break;
			
			case Bind_InsertReplace:
				if(parent.replaceChild(controller.getSelectedEditor().construct, constructForSelecting)) {
					mDocument.getConstructEditorStore().unregister(controller.getSelectedEditor());
				} else { 
					// Construct failed to replace
					constructForSelecting = null;
				}
				break;
			
			case Bind_InsertChild:
				int lastIndex = controller.getSelectedEditor().construct.getChildren().size();
				controller.getSelectedEditor().construct.addChild(lastIndex, constructForSelecting);
				break;
			
			case Bind_InsertPaste: {
				ClojureConstruct parentAsForm = (ClojureConstruct) parent;
				if(parentAsForm.getConstructType() == ConstructType.UserCollection) { 
					parent.addChild(indexOfCurrentSelectionInParent + 1, constructForSelecting);
					selectionCause = SelectionCause.SelectedAfterPaste;
				} else { 
					// Cannot paste in this construct
					// TODO: Pasting in different construct types
					constructForSelecting = null;
				}
				break;
			}
			
			default:
				break;
		}
	
		if(constructForSelecting != null) {
			// Acquire the new constructs editor, then make this the selection
			ConstructEditor added = mDocument.editorsFromConstruct(constructForSelecting);
			if(added != null)  {
				controller.mConstructSelector.Select(selectionCause, added);
			}
		}
		
		return null;
	}
}

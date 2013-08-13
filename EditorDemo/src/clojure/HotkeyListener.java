package clojure;

import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;
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
import clojure.constructs.meta.CaseConstruct;
import clojure.constructs.meta.DefineFunctionConstruct;
import clojure.constructs.meta.FunctionConstruct;
import clojure.constructs.meta.IfThenElseConstruct;
import clojure.constructs.meta.KeyValuePairConstruct;
import clojure.constructs.meta.KeywordExpressionPairConstruct;
import clojure.constructs.meta.LetConstruct;
import clojure.constructs.meta.SymbolList;
import clojure.constructs.meta.VariadicVectorConstruct;
import construct.Construct;
import construct.Construct.SelectionCause;
import editor.Application;
import editor.BaseController;
import editor.BaseController.EKeyBinding;
import editor.BaseControllerListener;
import editor.ConstructEditor;
import editor.document.ConstructDocument;

public class HotkeyListener implements BaseControllerListener {
	private final ConstructDocument mDocument;
	
	public HotkeyListener(ConstructDocument document) { 
		mDocument = document;
	}
	
	
	@Override
	public boolean onReceievedAction(BaseController controller, EKeyBinding binding, SimpleAutoCompleteEntry entry) {
		if(binding == EKeyBinding.Bind_Undo) { 
			mDocument.undo();
			return true;
		} else if(binding == EKeyBinding.Bind_Redo) { 
			mDocument.redo();
			return true;
		}
		
		if(binding == EKeyBinding.Bind_DebugPrint) {
			mDocument.debugPrint();
			return true;
		}
		
		if(binding == EKeyBinding.Bind_DuplicateToAdjacent) {
			// Duplicate this construct
			Construct parent = getParentForBinding(controller.getSelectedEditor(), binding);
			Construct selected = controller.getSelectedEditor().construct;
			Construct created = duplicate(selected, parent);
			
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
			handleInsert(controller, binding, entry);
		}
		
		return true;
	}
	
	private ClojureConstruct duplicate(Construct toBeCloned, Construct parent) {
		ClojureConstruct newConstruct = null;
		
		if(toBeCloned.getClass().equals(SymbolConstruct.class)) { 
			newConstruct = new SymbolConstruct(mDocument, parent, "symbol");
		} else if(toBeCloned.getClass().equals(VectorConstruct.class)) { 
			newConstruct = new VectorConstruct(mDocument, parent, null);
		} else if(toBeCloned.getClass().equals(ListConstruct.class)) { 
			newConstruct = new ListConstruct(mDocument, parent, null);
		} else if(toBeCloned.getClass().equals(MapConstruct.class)) { 
			newConstruct = new MapConstruct(mDocument, parent, null);
		} else if(toBeCloned.getClass().equals(KeywordExpressionPairConstruct.class)) { 
			newConstruct = new KeywordExpressionPairConstruct(mDocument, parent, null);
		} else if(toBeCloned.getClass().equals(BooleanConstruct.class)) { 
			newConstruct = new BooleanConstruct(mDocument, parent, "true");
		} else if(toBeCloned.getClass().equals(IntegerConstruct.class)) {
			newConstruct = new IntegerConstruct(mDocument, parent, "0");
		} else if(toBeCloned.getClass().equals(KeywordConstruct.class)) { 
			newConstruct = new KeywordConstruct(mDocument, parent, "keyword");
		} else if(toBeCloned.getClass().equals(DoubleConstruct.class)) { 
			newConstruct = new DoubleConstruct(mDocument, parent, "0.0");
		} else if(toBeCloned.getClass().equals(CharacterConstruct.class)) { 
			newConstruct = new CharacterConstruct(mDocument, parent, "c");
		} else if(toBeCloned.getClass().equals(StringConstruct.class)) { 
			newConstruct = new StringConstruct(mDocument, parent, "string");
		}
		
		return newConstruct;
	}	
	
	private ClojureConstruct getParentForBinding(ConstructEditor selectedEditor, EKeyBinding binding) {	
		ClojureConstruct parent = null;
		switch(binding) {
			case Bind_InsertAfter:  
			case Bind_InsertBefore:
			case Bind_InsertReplace:
				parent = (ClojureConstruct) selectedEditor.construct.parent;
				break;
				
			case Bind_InsertChild:
				// Check to see if this node can be used to insert children
				parent = (ClojureConstruct) selectedEditor.construct;
				break;
				
			case Bind_DuplicateToAdjacent:
				parent = (ClojureConstruct) selectedEditor.construct.parent;
				break;
				
			default:
				System.out.println(binding.toString());
		}
		
		if(parent == null) { 
			return null;
		}
		
		if(parent.isConstructContainer() == false && 
				(binding == EKeyBinding.Bind_InsertAfter || 
				binding == EKeyBinding.Bind_InsertBefore || 
				binding == EKeyBinding.Bind_InsertChild))
		{ 
			return null;
		}			
		
		return parent;
	}
	
	private String stringForBinding(EKeyBinding binding) { 
		switch(binding) { 
			case Bind_DeleteAll:
				return "delete";
				
			case Bind_InsertChild:
				return "insert child";
				
			case Bind_InsertAfter:
				return "insert after";
				
			case Bind_InsertBefore:
				return "insert before";
				
			case Bind_InsertReplace:
				return "replace";
			
			default:
				break;
		}
		
		return "(unknown)";
	}

	private void handleInsert(BaseController controller, EKeyBinding binding, SimpleAutoCompleteEntry entry) {
		// Determine the parent for the new construct, this is 
		// based on the initial input (IA, IA, IC)
		ClojureConstruct parent = getParentForBinding(controller.getSelectedEditor(), binding);
		Construct newConstruct = entry.create(mDocument, parent);
		
		if(parent == null) {
			Application.showErrorMessage("Cannot " + stringForBinding(binding) + " with selected construct");			
			return ;
		}
		
		// Determine location of insertion
		int selIndex = parent.getChildren().indexOf(controller.getSelectedEditor().construct);
		switch(binding) {
			case Bind_InsertAfter: {
				parent.addChild(selIndex + 1, newConstruct);
				break;
			}
			
			case Bind_InsertBefore:  {
				parent.addChild(selIndex,  newConstruct);
				break;
			}
			
			case Bind_InsertReplace:  {
				if(parent.replaceChild(controller.getSelectedEditor().construct, newConstruct)) {
					mDocument.getConstructEditorStore().unregister(controller.getSelectedEditor());
				} else { 
					return ;
				}
				break;
			}
			
			case Bind_InsertChild: {
				int lastIndex = controller.getSelectedEditor().construct.getChildren().size();
				controller.getSelectedEditor().construct.addChild(lastIndex, newConstruct);
				break;
			}
			
			default:
				break;
		}
		
		ConstructEditor added = mDocument.editorsFromConstruct(newConstruct);
		if(added != null)  {
			controller.mConstructSelector.Select(SelectionCause.SelectedAfterInsert, added);
		}
	}
}

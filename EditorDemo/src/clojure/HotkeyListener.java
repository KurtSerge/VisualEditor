package clojure;

import java.awt.event.KeyEvent;

import json.JSONController;

import clojure.constructs.*;
import clojure.constructs.meta.*;

import editor.BaseController;
import editor.BaseController.EKeyBinding;
import editor.Application;
import editor.BaseControllerListener;
import editor.Construct.SelectionCause;
import editor.ConstructEditor;
import editor.document.ConstructDocument;

public class HotkeyListener implements BaseControllerListener {
	private final ConstructDocument mDocument;
	
	public HotkeyListener(ConstructDocument document) { 
		mDocument = document;
	}
	
	
	@Override
	public boolean receivedHotkey(BaseController controller, EKeyBinding binding, int keyEventCode) {
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

		handleInsert(controller, binding, keyEventCode);
		return true;
	}
	
	private ClojureConstruct getConstructFromKey(ClojureConstruct parent, int keyEventCode) {
		ClojureConstruct newConstruct = null;

		System.out.println("Key is " + KeyEvent.VK_M);
		
		switch(keyEventCode) {
			case KeyEvent.VK_6:
				newConstruct = new FunctionConstruct(parent);
				break;
			
			case KeyEvent.VK_7:
				newConstruct = new CaseConstruct(parent);
				break;
		
			case KeyEvent.VK_8:
				newConstruct = new LetConstruct(parent);
				break;
		
			case KeyEvent.VK_9:
				newConstruct = new DefineFunctionConstruct(parent);
				break;
				
			case KeyEvent.VK_0:
				newConstruct = new IfThenElseConstruct(parent);
				break;
			
			case KeyEvent.VK_S: 
				newConstruct = new SymbolConstruct(parent, "symbol");
				break;
				
			case KeyEvent.VK_V:
				newConstruct = new VectorConstruct(parent, null);
				break;
				
			case KeyEvent.VK_L:
				newConstruct = new ListConstruct(parent, null);
				break;	
				
			case KeyEvent.VK_M:
				newConstruct = new MapConstruct(parent, null);
				break;
				
			case KeyEvent.VK_P:
				newConstruct = new KeyValuePairConstruct(parent, null);
				break;
				
			case KeyEvent.VK_B:
				newConstruct = new BooleanConstruct(parent, "true");
				break;	
				
			case KeyEvent.VK_I:
				newConstruct = new IntegerConstruct(parent, "0");
				break;		
			
			case KeyEvent.VK_K:
				newConstruct = new KeywordConstruct(parent, "keyword");
				break;
				
			case KeyEvent.VK_D:
				newConstruct = new DoubleConstruct(parent, "0.0");
				break;
				
			case KeyEvent.VK_C:
				newConstruct = new CharacterConstruct(parent, "c");
				break;
				
			case KeyEvent.VK_T:
				newConstruct = new StringConstruct(parent, "string");
				break;
				
			default:
				System.out.println("Unknown keyEventCode: " + keyEventCode);
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
				
			default:
				System.out.println(binding.toString());
				throw new RuntimeException("Unhandled hotkey");
		}
		
		if(parent == null) { 
			return null;
		}
		
		if(parent.canInsertChildren() == false && 
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
		}
		
		return "(unknown)";
	}

	private void handleInsert(BaseController controller, EKeyBinding binding, int keyEventCode) {
		// Determine the parent for the new construct, this is 
		// based on the initial input (IA, IA, IC)
		ClojureConstruct parent = getParentForBinding(controller.getSelectedEditor(), binding);
		if(parent == null) {
			Application.showError(controller.getSelectedEditor(), "Cannot " + stringForBinding(binding) + " with selected construct");			
			return ;
		}

		// Determine what the new construct will be and any default
		// children that the construct will have
		ClojureConstruct newConstruct = getConstructFromKey(parent, keyEventCode);
		if(newConstruct == null) {
			Application.showError(controller.getSelectedEditor(), "Failed to " + stringForBinding(binding) + ": unknown construct type");
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
					// Cleanup the existing editor
					mDocument.remove(controller.getSelectedEditor());
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

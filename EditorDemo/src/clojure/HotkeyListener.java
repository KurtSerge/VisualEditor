package clojure;

import java.awt.event.KeyEvent;

import json.JSONController;

import org.json.JSONArray;
import org.json.JSONObject;

import clojure.constructs.*;

import editor.BaseController;
import editor.BaseController.EKeyBinding;
import editor.BaseControllerListener;
import editor.Construct;
import editor.ConstructEditor;

public class HotkeyListener implements BaseControllerListener {
	@Override
	public void receivedHotkey(BaseController controller, EKeyBinding binding, int keyEventCode) {
		handleInsert(controller, binding, keyEventCode);
	}

	private void handleInsert(BaseController controller, EKeyBinding binding, int keyEventCode) {
		ClojureConstruct newConstruct = null;
		ClojureConstruct parent = null;

		// Determine parent of construct to be added
		switch(binding) {
			case Bind_InsertAfter:  
			case Bind_InsertBefore:
			case Bind_InsertReplace:
				parent = (ClojureConstruct) controller.getSelectedEditor().construct.parent;
				break;
				
			case Bind_InsertChild:
				// Check to see if this node can be used to insert children
				parent = (ClojureConstruct) controller.getSelectedEditor().construct;
				if(!parent.canInsertChildren()) { 
					// This node cannot be used, ignore command
					System.out.println("Ignoring Bind_InsertChild");
					return ;
				}
				break;
			default:
				throw new RuntimeException("Unhandled hotkey");
		}
		
		// Get a construct to insert
		switch(keyEventCode) {
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
		
		// Error check previous step
		if(newConstruct == null) {
			System.out.println("Fatal Error: A construct was not created");
			return ;
		}
		
		// Determine location of insertion
		int selIndex = parent.children.indexOf(controller.getSelectedEditor().construct);
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
				if(parent.replaceChild(controller.getSelectedEditor().construct, newConstruct) == false)
					return;
				break;
			}
			
			case Bind_InsertChild: {
				int lastIndex = controller.getSelectedEditor().construct.children.size();
				controller.getSelectedEditor().construct.addChild(lastIndex, newConstruct);
				break;
			}
		}
		
		ConstructEditor added = JSONController.editors_from_constructs(newConstruct);
		if(added != null)  {
			controller.selector.Select(added);
		}
	}
}

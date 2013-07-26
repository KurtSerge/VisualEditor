package json;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONObject;

import editor.BaseController;
import editor.BaseControllerListener;
import editor.Construct;
import editor.ConstructEditor;
import editor.BaseController.EKeyBinding;
import editor.document.ConstructDocument;

public class JSONHotkeyListener implements BaseControllerListener {
	private final ConstructDocument mDocument;
	private final JFrame window;	

	public JSONHotkeyListener(ConstructDocument document, JFrame window) {
		this.window = window;
		this.mDocument = document;
	}
	
	@Override
	public void receivedHotkey(BaseController controller, EKeyBinding binding, int keyEventCode) {
		handleInsert(controller, binding, keyEventCode);
	}

	private void handleInsert(BaseController controller, EKeyBinding binding, int keyEventCode) {
		Construct newConstruct = null;
		Construct parent = null;

		// Determine parent of construct to be added
		switch(binding) {
			case Bind_InsertAfter:  
			case Bind_InsertBefore:
			case Bind_InsertReplace:
			case Bind_InsertUsurp:
				parent = controller.getSelectedEditor().construct.parent;
				break;
			case Bind_InsertChild:
				parent = controller.getSelectedEditor().construct;
				break;
				
			case Bind_Undo:
				mDocument.undo();
				return;
				
			case Bind_Redo:
				mDocument.redo();
				return;
				
			case Bind_DebugPrint:
				mDocument.debugPrint();
				break;
				
			default:
				throw new RuntimeException("Unhandled hotkey");
		
		}
		
		// Get a construct to insert
		switch(keyEventCode) {
			case KeyEvent.VK_O: // Object
				if(parent.getClass() == json.KeyValueConstruct.class)  {
					// Empty Object 
					JSONObject newObj = new JSONObject();
					newConstruct = JSONController.construct_for_json(newObj, parent);
				}
				else {
					// KV-Pair with Obj value (it's common enough to warrant a hotkey methinks)
					// FIXME: needs work
					JSONObject newObj = new JSONObject();
					newObj.put("EmptyString", new JSONObject());
					newConstruct = JSONController.construct_for_json(newObj, parent);
				}
				break;
			case KeyEvent.VK_S: // String
				newConstruct = JSONController.construct_for_json("EmtpyStr", parent);
				break;
			case KeyEvent.VK_K: // KVPair
    			newConstruct = JSONController.get_empty_kvp(parent);
				break;
			case KeyEvent.VK_I: // Integer
				newConstruct = JSONController.construct_for_json(0, parent);
				break;
			case KeyEvent.VK_F: // Float
				newConstruct = JSONController.construct_for_json(0.0, parent);
				break;
			case KeyEvent.VK_B: // Bool
				newConstruct = JSONController.construct_for_json(true, parent);
				break;
			case KeyEvent.VK_N: // Null
				newConstruct = JSONController.construct_for_json(org.json.JSONObject.NULL, parent);
				break;
			case KeyEvent.VK_A: // Array
				JSONArray list = new JSONArray();
				list.put("test");
				list.put("test2");
				newConstruct = JSONController.construct_for_json(list, parent);
				break;
			default:
				return;
		}
		// Error check previous step
		if(newConstruct == null)
			return;
		
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
				ConstructEditor editorParent = controller.getSelectedEditor().getParent();
				editorParent.replaceChild(controller.getSelectedEditor().construct, newConstruct);
				break;
			}
			case Bind_InsertChild: {
				int lastIndex = controller.getSelectedEditor().construct.children.size();
				controller.getSelectedEditor().construct.addChild(lastIndex, newConstruct);
				break;
			}
			case Bind_InsertUsurp: {
				// FIXME: What to test this on?
				if(1==1)
					throw(new RuntimeException("Usurp not working"));
				
				parent.addChild(selIndex + 1, newConstruct);// TODO: delete this if usurp fails
				// Copy children
				for(Construct child : controller.getSelectedEditor().construct.children)  {
					Construct usurp = child.deepCopy(newConstruct);
					int addIndex = newConstruct.children.size();
					parent.addChild(addIndex, usurp);
				}
				
				// copy selected children
				//newConstruct = controller.getSelectedEditor().construct.deepCopy(parent);
				//parent.addChild(selIndex+1, newConstruct);
				
				// delete selected children
				//parent.deleteChild(controller.getSelectedEditor().construct);
				break;
			}
			
			default:
				break;
		}
		
		ConstructEditor added = mDocument.editorsFromConstruct(newConstruct);
		if(added != null)  {
			controller.getSelectedEditor().update();
			controller.selector.Select(added);
		}
	}
}

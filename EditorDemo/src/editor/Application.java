package editor;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.json.JSONArray;
import org.json.JSONObject;

import editor.BaseController.EKeyBinding;
import editor.document.ClojureDocument;
import editor.document.Document;
import editor.document.JSONDocument;

import clojure.ClojureController;

import json.JSONController;

public class Application extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Demo
//	private Construct  jsonDocumentConstruct2 = null;
	
	private Document mDocument = null;
	private BaseController controller = null;
	
	private class HotkeyListener implements BaseControllerListener {
		private final Document mDocument;
		
		public HotkeyListener(Document document) { 
			mDocument = document;
		}
		
		@Override
		public void receivedHotkey(BaseController controller, EKeyBinding binding, int keyEventCode) {
			handleInsert(binding, keyEventCode);
		}

		private void handleInsert(EKeyBinding binding, int keyEventCode) {
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
					if(parent.replaceChild(controller.getSelectedEditor().construct, newConstruct) == false)
						return;
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
				controller.selector.Select(added);
			}
		}
	}
    
	
	Application()
	{
		super("Editor Demo");
		
		boolean shouldLoadJson = false;	// Alt: Loads Clojure
		
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setBackground(Color.white);
		
		try {
			if(shouldLoadJson) {
				mDocument = new JSONDocument("test2.json");
			} else {
				mDocument = new ClojureDocument("testtypes.clj");
			}
		
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return ;
		} catch (Exception ex) {
			System.err.println("Uncaught exception while loading Document");
			ex.printStackTrace();
			return ;
		}

		
		Component rootDocumentComponent = mDocument.getRootComponent();
		this.add(rootDocumentComponent);
		
		if(shouldLoadJson == false) {
			controller = new BaseController(this, mDocument);
			Toolkit.getDefaultToolkit().addAWTEventListener(new BaseMouseController(controller, mDocument), AWTEvent.MOUSE_EVENT_MASK);
			rootDocumentComponent.addKeyListener(controller);
			rootDocumentComponent.requestFocus();
			
			controller.setListener(new clojure.HotkeyListener(mDocument));
			controller.registerHotkey(EKeyBinding.Bind_InsertAfter, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_A, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertBefore, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_B, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertChild, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_C, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertReplace, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_R, (char)KeyEvent.VK_UNDEFINED));
		}
		else{
			controller = new BaseController(this, mDocument);
			Toolkit.getDefaultToolkit().addAWTEventListener(new BaseMouseController(controller, mDocument), AWTEvent.MOUSE_EVENT_MASK);
			rootDocumentComponent.addKeyListener(controller);
			rootDocumentComponent.requestFocus();
			
			controller.setListener(new HotkeyListener(mDocument));
			controller.registerHotkey(EKeyBinding.Bind_InsertAfter, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_A, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertBefore, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_B, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertReplace, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_R, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertChild, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_C, (char)KeyEvent.VK_UNDEFINED));
			controller.registerHotkey(EKeyBinding.Bind_InsertUsurp, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_U, (char)KeyEvent.VK_UNDEFINED));
		}

		this.pack();
		this.setSize(800, 600);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Application();
	}

}

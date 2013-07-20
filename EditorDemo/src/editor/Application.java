package editor;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import editor.BaseController.EKeyBinding;

import clojure.ClojureController;
import clojure.constructs.IntegerConstruct;

import json.JSONController;
import json.KeyValueConstruct;
import json.ObjectConstruct;
import json.StringConstruct;
import json.StringLiteralConstruct;
import lisp.LispController;




public class Application extends JFrame
{
	// Demo
	private Construct  jsonDocumentConstruct2 = null;
	private  BaseController controller = null;
	
	private class HotkeyListener implements BaseControllerListener {

		@Override
		public void receivedHotkey(EKeyBinding binding, int keyEventCode) {
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
			}
			
			ConstructEditor added = JSONController.editors_from_constructs(newConstruct);
			if(added != null)  {
				controller.selector.Select(added);
			}
		}
	}
    
    // Using AWT because Swing "MouseListener" doesn't give coords when clicking over jtextareas
    private static class Listener implements AWTEventListener {
        public void eventDispatched(AWTEvent event) {
        	if (event instanceof MouseEvent) {
        		MouseEvent e = (MouseEvent)event;
        		if(e.getID() == MouseEvent.MOUSE_CLICKED)
	            System.out.print(MouseInfo.getPointerInfo().getLocation() + "\n");
        	} 
        }
    }
	
	Application()
	{
		super("Editor Demo");
		
		boolean shouldLoadJson = true;	// Alt: Loads Clojure
		
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setBackground(Color.white);
		
		try {
			if(shouldLoadJson) {
				jsonDocumentConstruct2 = JSONController.load_json(new FileInputStream("testNum.json"));
			} else {
				jsonDocumentConstruct2 = ClojureController.load_clojure(new FileInputStream("testtypes.clj"));
			}
			
			//jsonDocumentConstruct2 = JSONController.load_json(new FileInputStream("commaTest.json"));
			//jsonDocumentConstruct2 = JSONController.load_json(new FileInputStream("test2.json"));
			//jsonDocumentConstruct2.debugPrint();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		

		/*
		// TODO: Load from file
		Construct jsonDocumentConstruct = new ObjectConstruct(null);
		
		Construct key_literal = new StringLiteralConstruct(jsonDocumentConstruct, "key");
		JSONConstructEditorFactory.create_editor_for(key_literal);
		
		Construct key = new StringConstruct(key_literal);
		JSONConstructEditorFactory.create_editor_for(key);
		key.children.add(key_literal);
		
		Construct value_literal = new StringLiteralConstruct(jsonDocumentConstruct, "value");
		JSONConstructEditorFactory.create_editor_for(value_literal);
		
		Construct value = new StringConstruct(value_literal);
		JSONConstructEditorFactory.create_editor_for(value);
		value.children.add(value_literal);
		
		
		jsonDocumentConstruct.children.add(key);
		jsonDocumentConstruct.children.add(value);
		
		this.add(JSONConstructEditorFactory.create_editor_for(jsonDocumentConstruct).get_component());
		*/
		
		Component top;
		if(shouldLoadJson) { 
			top = JSONController.editors_from_constructs(jsonDocumentConstruct2).get_component();
		} else { 
			top = ClojureController.editors_from_constructs(jsonDocumentConstruct2).get_component();
		}
		
		this.add(top);
		

		if(shouldLoadJson == false) {
			controller = new BaseController(this, ClojureController.editors);
			top.addKeyListener(controller);
	
			Toolkit.getDefaultToolkit().addAWTEventListener(new Listener(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
		}
		else{
			controller = new BaseController(this, JSONController.editors);
			top.addKeyListener(controller); // Must add BaseController first
			top.requestFocus();
			
			controller.setListener(new HotkeyListener());
			controller.registerHotkey(EKeyBinding.Bind_InsertAfter, "IA?");
			controller.registerHotkey(EKeyBinding.Bind_InsertBefore, "IB?");
			controller.registerHotkey(EKeyBinding.Bind_InsertReplace, "IR?");
			controller.registerHotkey(EKeyBinding.Bind_InsertChild, "IC?");
			
			//top.addMouseListener(new MouseSelector());
			Toolkit.getDefaultToolkit().addAWTEventListener(new Listener(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);

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

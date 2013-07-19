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
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONObject;

import clojure.ClojureController;

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
	
    private class MyDispatcher implements KeyListener {
    	boolean insert_pressed;
    	private final JFrame frame;
    	
    	public MyDispatcher(JFrame frame) {
    		this.frame = frame;
    	}
    	
		@Override
		public void keyPressed(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	
        		// Check for combo key presses, such as "i + o"
    			if(insert_pressed==true) {
            		insert_pressed = false;
	        		switch(e.getKeyCode()) {
	        			// Insert KV pair
		        		case KeyEvent.VK_K: {
		        			JSONObject obj=new JSONObject();
		        			obj.put("?temp","?temp");// TODO: second string should actually be ? placeholder... can be object OR string
		        			Construct ret = JSONController.add_key_value_pair(obj, controller.getSelectedEditor().construct);
		        			if(ret != null)
		        				JSONController.editors_from_constructs(ret);
		        			break;
		        		}
		        		case KeyEvent.VK_O: {
		        			JSONObject obj=new JSONObject();
		        			obj.put("?temp",new JSONObject());// TODO: second string should actually be ? placeholder... can be object OR string
		        			Construct ret = JSONController.add_key_value_pair(obj, controller.getSelectedEditor().construct);
		        			if(ret != null)
		        				JSONController.editors_from_constructs(ret);
		        		}
	        		}
	        		return;
    			}
    			
        		// Reset first key press
        		insert_pressed = false;
        		
        		switch(e.getKeyCode()) {
	        		case KeyEvent.VK_I:
	        			insert_pressed = true;
	        			repaint();
	        			break;
	        		case KeyEvent.VK_Q:
	        			jsonDocumentConstruct2.debugPrint();
	        			break;
	        		default:
	        			break;
        		}
            }
		}

		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
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
		
		boolean shouldLoadJson = false;	// Alt: Loads Clojure
		
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
		top.addKeyListener(new MyDispatcher(this));
		
		controller = new BaseController(this, ClojureController.editors);
		top.addKeyListener(controller);

		Toolkit.getDefaultToolkit().addAWTEventListener(new Listener(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);

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

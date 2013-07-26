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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONArray;
import org.json.JSONObject;

import editor.BaseController.EKeyBinding;
import editor.document.ClojureConstructDocument;
import editor.document.ConstructDocument;
import editor.document.JSONConstructDocument;

import clojure.ClojureController;

import json.JSONController;
import json.JSONHotkeyListener;
public class Application extends JFrame
{
	private void setupNewConstruct(Component top, BaseControllerListener listener) {
		// Delete
		if(controller != null) {
			controller.getSelectedEditor().deleteAll();
			this.removeKeyListener(controller);
		}

		this.add(top);
		controller = new BaseController(this, mDocument);
		top.addKeyListener(controller);
		top.requestFocus();
		
		controller.setListener(listener);
		controller.registerHotkey(EKeyBinding.Bind_InsertAfter, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_A, (char)KeyEvent.VK_UNDEFINED));
		controller.registerHotkey(EKeyBinding.Bind_InsertBefore, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_B, (char)KeyEvent.VK_UNDEFINED));
		controller.registerHotkey(EKeyBinding.Bind_InsertReplace, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_R, (char)KeyEvent.VK_UNDEFINED));
		controller.registerHotkey(EKeyBinding.Bind_InsertChild, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_C, (char)KeyEvent.VK_UNDEFINED));
		controller.registerHotkey(EKeyBinding.Bind_InsertUsurp, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_U, (char)KeyEvent.VK_UNDEFINED));
		controller.registerHotkey(EKeyBinding.Bind_Undo, String.format("%s", (char)KeyEvent.VK_U));
		controller.registerHotkey(EKeyBinding.Bind_Redo, String.format("%s", (char)KeyEvent.VK_R));
		
		controller.registerHotkey(EKeyBinding.Bind_DebugPrint, String.format("%s", (char)KeyEvent.VK_P));
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConstructDocument mDocument = null;
	private BaseController controller = null;
	

    
	
	Application()
	{
		super("Editor Demo");
		
		boolean shouldLoadJson = true;	// Alt: Loads Clojure
		
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setBackground(Color.white);
		
		final JFrame window = this;
		
		try {
			if(shouldLoadJson) {
				mDocument = new JSONConstructDocument("test2.json");
				mDocument.setListener(new ConstructDocument.ConstructDocumentListener() {
					@Override
					public void onDocumentUndo(ConstructDocument document) {
						JSONHotkeyListener keyListener = new JSONHotkeyListener(controller, document, window);
						setupNewConstruct(document.getRootComponent(), keyListener);
					}
					
					@Override
					public void onDocumentRedo(ConstructDocument document) {
						JSONHotkeyListener keyListener = new JSONHotkeyListener(controller, document, window);
						setupNewConstruct(document.getRootComponent(), keyListener);
					}
				});				
			} else {
				mDocument = new ClojureConstructDocument("testtypes.clj");
				mDocument.setListener(new ConstructDocument.ConstructDocumentListener() {
					@Override
					public void onDocumentUndo(ConstructDocument document) {
						clojure.HotkeyListener keyListener = new clojure.HotkeyListener(document);
						setupNewConstruct(document.getRootComponent(), keyListener);
					}
					
					@Override
					public void onDocumentRedo(ConstructDocument document) {
						clojure.HotkeyListener keyListener = new clojure.HotkeyListener(document);
						setupNewConstruct(document.getRootComponent(), keyListener);
					}
				});				
			}
			
			// Adds a listener that will update the 
			// visible document when the document is
			// updated via redo/undo methods.

		
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return ;
		} catch (Exception ex) {
			System.err.println("Uncaught exception while loading Document");
			ex.printStackTrace();
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
			JSONHotkeyListener keyListener = new JSONHotkeyListener(controller, mDocument, this);
			setupNewConstruct(rootDocumentComponent, keyListener);
			Toolkit.getDefaultToolkit().addAWTEventListener(new BaseMouseController(controller, mDocument), AWTEvent.MOUSE_EVENT_MASK);
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

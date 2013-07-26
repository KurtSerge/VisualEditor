package editor;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
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
public class Application extends VisualEditorFrame
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
		
		controller.registerHotkey(EKeyBinding.Bind_Copy, String.format("%s", (char)KeyEvent.VK_C));
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
		
		this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		final VisualEditorFrame window = this;
		try {
			if(shouldLoadJson) {
				// Create a document for JSON parsing
				mDocument = new JSONConstructDocument("testNum.json");
				mDocument.setListener(new ConstructDocument.ConstructDocumentListener() {
					@Override
					public void onDocumentUndo(ConstructDocument document) {
						JSONHotkeyListener keyListener = new JSONHotkeyListener(document, window);
						setupNewConstruct(document.getRootComponent(), keyListener);
					}
					
					@Override
					public void onDocumentRedo(ConstructDocument document) {
						JSONHotkeyListener keyListener = new JSONHotkeyListener(document, window);
						setupNewConstruct(document.getRootComponent(), keyListener);
					}
				});				
			} else {
				// Create a document for Clojure parsing
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
			clojure.HotkeyListener keyListener = new clojure.HotkeyListener(mDocument);
			setupNewConstruct(rootDocumentComponent, keyListener);
			Toolkit.getDefaultToolkit().addAWTEventListener(new BaseMouseController(controller, mDocument, window), AWTEvent.MOUSE_EVENT_MASK);
		}
		else{
			JSONHotkeyListener keyListener = new JSONHotkeyListener(mDocument, this);
			setupNewConstruct(rootDocumentComponent, keyListener);
			Toolkit.getDefaultToolkit().addAWTEventListener(new BaseMouseController(controller, mDocument, window), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
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

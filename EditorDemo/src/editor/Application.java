package editor;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONArray;
import org.json.JSONObject;

import editor.BaseController.EKeyBinding;
import editor.BaseController.Hotkey;
import editor.ConstructPublisher.ConstructListener;
import editor.document.ClojureConstructDocument;
import editor.document.ConstructDocument;
import editor.document.JSONConstructDocument;

import json.JSONController;
import json.JSONHotkeyListener;
public class Application extends VisualEditorFrame implements ComponentListener
{
	private void setupNewConstruct(Component top, BaseControllerListener listener) {
		// Delete
		if(controller != null) {
			controller.getSelectedEditor().deleteAll();
			this.removeKeyListener(controller);
		}

		this.getDocumentPane().add(top);
		
		
		// TODO: ! Cyclic
		controller = new BaseController(this, mDocument);
		mDocument.setController(controller);
		
		layoutController = new LayoutController(mDocument, this, getSize(), 0.9f);
		top.setFocusTraversalKeysEnabled(false);
		top.addKeyListener(controller);
		top.requestFocus();

		controller.addListener(listener);
		controller.addHotkey(EKeyBinding.Bind_Undo, KeyEvent.VK_Z, true);
		controller.addHotkey(EKeyBinding.Bind_Redo, KeyEvent.VK_Y, true);
		controller.addHotkey(EKeyBinding.Bind_InsertAfter, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_A, true)).setCaptureAlphaNumeric(true);
		controller.addHotkey(EKeyBinding.Bind_InsertBefore, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_B, true)).setCaptureAlphaNumeric(true);
		controller.addHotkey(EKeyBinding.Bind_InsertReplace, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_R, true)).setCaptureAlphaNumeric(true);
		controller.addHotkey(EKeyBinding.Bind_InsertChild, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_C, true)).setCaptureAlphaNumeric(true);

		// TODO: Restore
		//controller.registerHotkey(EKeyBinding.Bind_InsertUsurp, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_U, (char)KeyEvent.VK_UNDEFINED));
		//controller.registerHotkey(EKeyBinding.Bind_Copy, String.format("%s", (char)KeyEvent.VK_C));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConstructDocument mDocument = null;
	private BaseController controller = null;
	private LayoutController layoutController = null;
	private JTextArea lastError;

	Application()
	{
		super("Editor Demo");
		
		boolean shouldLoadJson = false;	// Alt: Loads Clojure

		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setBackground(Color.white);

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
			
			ConstructPublisher.getInstance().setActiveDocument(mDocument);
			ConstructPublisher.getInstance().addListener(new ConstructListener() {
				
				@Override
				public void onConstructRemovedChild(Construct parent, Construct child, int index) {
					layoutController.relayout();
				}
				
				@Override
				public void onConstructAddedChild(Construct parent, Construct child, int index) {
					mDocument.editorsFromConstruct(child);
				}

				@Override
				public void onConstructModified(Construct construct) {
					final LayoutController lc = layoutController;
					if(lc != null) { 
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								lc.relayout();
							}
						});
					}
				}
			});
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return ;
		} catch (Exception ex) {
			System.err.println("Uncaught exception while loading Document");
			ex.printStackTrace();
		}
		
		Component rootDocumentComponent = mDocument.getRootComponent();			

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
		
		this.getContentPane().addComponentListener(this);

		this.pack();
		this.setSize(800, 600);
	}

	private static Application sApplication = null;
	
	
	public static void resetError() { 
		if(sApplication != null)
			sApplication._resetError();
	}
	
	public static void showError(Construct editor, String error) {
		System.out.println("Show modal error: " + error);
		sApplication.presentError(ConstructEditor.getEditorByConstruct(editor), error);
	}
	
	public static void showError(ConstructEditor editor, String error) { 
		sApplication.presentError(editor, error);
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
		
		sApplication = new Application();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		layoutController.setDimensions(getSize());
		layoutController.relayout();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}
}

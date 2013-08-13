package editor;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import json.JSONHotkeyListener;
import autocomplete.IAutoCompleteListener;
import construct.Construct;
import editor.ConstructPublisher.ConstructListener;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ClojureConstructDocument;
import editor.document.ConstructDocument;
import editor.document.JSONConstructDocument;
public class Application extends VisualEditorFrame implements ComponentListener
{
	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
	
	public Clipboard mClipboard = new Clipboard();
	
	public Clipboard getClipboard() { 
		return mClipboard;
	}
	
	private void setupNewConstruct(Component top, IInterfaceActionListener listener) {
		// Delete
		if(controller != null) {
			controller.getSelectedEditor().deleteAll();
			this.removeKeyListener(controller);
		}

		this.getDocumentPane().add(top);
		
		// TODO: ! Cyclic
		controller = new InterfaceController(this, mDocument);
		mDocument.setController(controller);
		
		layoutController = new LayoutController(mDocument, this, getSize(), 0.9f);
		top.setFocusTraversalKeysEnabled(false);
		top.addKeyListener(controller);
		top.requestFocus();

		controller.addInterfaceActionListener(listener);
		controller.addHotkey(EInterfaceAction.Bind_Undo, KeyEvent.VK_Z, true);
		controller.addHotkey(EInterfaceAction.Bind_Redo, KeyEvent.VK_Y, true);
		controller.addHotkey(EInterfaceAction.Bind_Copy, KeyEvent.VK_C, true);
		controller.addHotkey(EInterfaceAction.Bind_PresentAutoComplete, KeyEvent.VK_ENTER);
		controller.addHotkey(EInterfaceAction.Bind_InsertPaste, KeyEvent.VK_V, true);
		controller.addHotkey(EInterfaceAction.Bind_InsertAfter, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_A, true)).setFollowsWithAutoComplete(true);
		controller.addHotkey(EInterfaceAction.Bind_InsertBefore, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_B, true)).setFollowsWithAutoComplete(true);
		controller.addHotkey(EInterfaceAction.Bind_InsertReplace, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_R, true)).setFollowsWithAutoComplete(true);
		controller.addHotkey(EInterfaceAction.Bind_InsertChild, KeyEvent.VK_I, true).setNext(new Hotkey(KeyEvent.VK_C, true)).setFollowsWithAutoComplete(true);
		
		// TODO: Restore
		//controller.registerHotkey(EInterfaceAction.Bind_InsertUsurp, String.format("%s%s%s", (char)KeyEvent.VK_I, (char)KeyEvent.VK_U, (char)KeyEvent.VK_UNDEFINED));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConstructDocument mDocument = null;
	private InterfaceController controller = null;
	private LayoutController layoutController = null;

	Application() {
		super("Form Editor");

		this.setSize(DEFAULT_WINDOW_SIZE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.white);		
		this.setVisible(true);
	}
	
	public void initialize() { 
		boolean shouldLoadJson = false;
		final VisualEditorFrame window = this;
		
		this.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				// TODO Auto-generated method stub
				hideAutoComplete(false);
			}
			
			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				restoreAutoComplete();
			}
		});
		
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
	
		this.invalidate();
	}

	private static Application sApplication = null;
	
	
	public static void resetError() { 
		if(sApplication != null)
			sApplication._resetError();
	}
	
	public static void showErrorMessage(String error) {
		if(sApplication != null)
			sApplication.presentError(error);
	}
	
	public static void showInfoMessage(String message) { 
		if(sApplication != null)
			sApplication.presentInfoMessage(message);
	}
	
	public static void showDebugMessage(String message) { 
		if(sApplication != null)
			sApplication.presentDebugMessage(message);
	}
	
	public static void presentAutoComplete(InterfaceController controller, ConstructEditor editor, IAutoCompleteListener listener) { 
		if(sApplication != null) 
			sApplication.showAutoComplete(controller, editor, listener);
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
		sApplication.initialize();
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
	
	public static Application getApplication() { 
		return sApplication;
	}
}

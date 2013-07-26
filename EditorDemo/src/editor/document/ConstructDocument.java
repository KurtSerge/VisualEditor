package editor.document;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import editor.Construct;
import editor.ConstructEditor;
import editor.MonospaceConstructEditor;

/**
 * This is an abstract Construct oriented Document. It provides
 * wrappers for the basic Undo, Redo and Components as well as 
 * file input and output. 
 * 
 * @author chrislord
 */
public abstract class ConstructDocument {
	public interface ConstructDocumentListener { 
		public void onDocumentUndo(ConstructDocument document);
		public void onDocumentRedo(ConstructDocument document);
	}
	
	private ArrayList<ConstructEditor> mEditors;
	private Construct mRootConstruct;
	private Component mRootComponent;
	private ConstructDocumentListener mListener;

	public ConstructDocument(String filename) 
		throws FileNotFoundException
	{
		if(filename == null) {
			throw new IllegalArgumentException("filename cannot be null");
		}
		
		// Ensure we can access & read the file
		File file = new File(filename);
		if(!file.exists() || !file.canRead()) { 
			throw new FileNotFoundException();
		}
		
		// Attempt to load the file from disk into constructs
		FileInputStream fis = new FileInputStream(filename);
		mRootConstruct = loadConstruct(fis);
		if(mRootConstruct == null) { 
			throw new NullPointerException("No construct returned for Document::loadConstruct()");
		}
		
		mRootConstruct.AddToUndoBuffer();
		
		// Create editors from the root document
		ConstructEditor editorFromRoot = editorsFromConstruct(mRootConstruct);
		if(editorFromRoot == null) { 
			throw new NullPointerException("No editor returned for construct root");
		}
		
		setRootComponent(editorFromRoot.get_component());
	}
	
	public void setListener(ConstructDocumentListener newListener) { 
		mListener = newListener;
	}
	
	public ConstructEditor editorsFromConstruct(Construct root) { 
		for(Construct child : root.children)
			editorsFromConstruct(child);
		
		MonospaceConstructEditor newEditor = new MonospaceConstructEditor(root, this);
		
		if(mEditors == null)
			mEditors = new ArrayList<ConstructEditor>();
		mEditors.add(newEditor);
		
		return newEditor;
	}
	
	protected abstract Construct loadConstruct(InputStream in);
	
	public Component getRootComponent() { 
		return mRootComponent;
	}
	
	public List<ConstructEditor> getEditors() { 
		return mEditors;
	}
	
	public void remove(ConstructEditor editor) { 
		mEditors.remove(editor);
	}
	
	
	public boolean undo() {
		Construct undoneConstruct = Construct.getUndo();
		if(undoneConstruct != null) { 
			mRootConstruct = undoneConstruct;
			ConstructEditor editorFromRoot = editorsFromConstruct(mRootConstruct);
			setRootComponent(editorFromRoot.get_component());
			
			// Fire onDocumentUndo
			if(mListener != null) { 
				mListener.onDocumentUndo(this);
			}
			
			return true;
		} else { 
			System.err.println("Undo failed: Construct.getUndo() returned null");
		}
		
		return false;
	}
	
	public boolean redo() { 
		Construct redoneConstruct = Construct.getRedo();
		if(redoneConstruct != null) { 
			mRootConstruct = redoneConstruct;
			ConstructEditor editorFromRoot = editorsFromConstruct(mRootConstruct);
			setRootComponent(editorFromRoot.get_component());
			
			// Fire onDocumentRedo
			if(mListener != null) { 
				mListener.onDocumentRedo(this);
			}
			return true;
		} else { 
			System.err.println("Redo failed: Construct.getRedo() returned null");
		}
		
		return false;
	}
	
	public void debugPrint() { 
		mRootConstruct.debugPrint();
	}
	
	protected void setRootComponent(Component component) { 
		mRootComponent = component;
	}
}

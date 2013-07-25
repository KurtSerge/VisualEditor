package editor.document;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import editor.Construct;
import editor.ConstructEditor;
import editor.MonospaceConstructEditor;

public abstract class Document {
	
	private ArrayList<ConstructEditor> mEditors;
	private Construct mRootConstruct;
	private Component mRootComponent;

	public Document(String filename) 
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
		
		// Create editors from the root document
		ConstructEditor editorFromRoot = editorsFromConstruct(mRootConstruct);
		if(editorFromRoot == null) { 
			throw new NullPointerException("No editor returned for construct root");
		}
		
		mRootComponent = editorFromRoot.get_component();
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
}

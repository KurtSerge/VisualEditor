package editor.document;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import clojure.ClojureReader;
import clojure.constructs.ListConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.special.VariadicVectorConstruct;
import editor.BaseController;
import editor.Construct;

public class ClojureConstructDocument extends ConstructDocument {
	
	public ClojureConstructDocument(String filename) 
			throws FileNotFoundException
	{
		super(filename);
	}
	
	@Override
	protected Construct loadConstruct(InputStream in) { 
		try { 
 			ClojureReader reader = new ClojureReader();
			return reader.parseFromInputStream(in);
		} catch(Exception ex) { 
			System.err.println("Failed to load Clojure file " + ex.getMessage());
		}

		return null;
	}

}

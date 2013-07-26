package editor.document;

import java.io.FileNotFoundException;
import java.io.InputStream;

import clojure.ClojureReader;
import editor.Construct;

public class ClojureDocument extends Document {
	
	public ClojureDocument(String filename) 
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

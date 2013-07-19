package clojure;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;

import editor.Construct;
import editor.ConstructEditor;
import editor.MonospaceConstructEditor;

public class ClojureController {
	
	public static ArrayList<ConstructEditor> editors;

	static public ConstructEditor editors_from_constructs(Construct top)
	{
		for(Construct child : top.children)
			editors_from_constructs(child);
		
		MonospaceConstructEditor my_editor = new MonospaceConstructEditor(top);
		
		if(editors == null)
			editors = new ArrayList<ConstructEditor>();
		editors.add(my_editor);
		
		return my_editor;
	}
	
	static public Construct load_clojure(InputStream in) {
		try { 
 			ClojureReader reader = new ClojureReader();
			return reader.parseFromInputStream(in);
		} catch(Exception ex) { 
			System.err.println("Failed to load Clojure file " + ex.getMessage());
		}

		return null;
	}
}

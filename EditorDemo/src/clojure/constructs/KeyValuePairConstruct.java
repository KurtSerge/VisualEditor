package clojure.constructs;

import java.util.LinkedList;

import editor.Construct;
import clojure.*;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.special.VariadicVectorConstruct;

/**
 * An uncontained key value pair to enforce 1:1
 * matching of key/value pairs.
 * 
 * TODO: Rules for placement of KVPConstruct's
 *  
 * For instance:
 * 		In replacement of a single argument placeholder
 * 		Just plain on the root, uncontained
 * 
 * @author Christopher Lord
 */
public class KeyValuePairConstruct extends ClojureConstruct {

	public KeyValuePairConstruct(Construct parent, String literal) {
		super("keyvaluepair", parent);
		
		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("test-constant"));
		placeholders.add(Placeholder.createPlaceholder("result-expr"));		
		setPlaceholders(placeholders);
	}

	@Override
	public String screen_text() {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < this.children.size(); i++) { 
			stringBuilder.append("$(node)");
			if(i != this.children.size() - 1) 
				stringBuilder.append(BREAKING_SPACE);
		}
		
		return super.layout(stringBuilder.toString());
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		KeyValuePairConstruct newCopy = new KeyValuePairConstruct(parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

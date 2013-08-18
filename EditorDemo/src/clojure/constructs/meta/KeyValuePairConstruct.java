package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

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
public class KeyValuePairConstruct extends MetaConstruct {
	
	public KeyValuePairConstruct(KeyValuePairConstruct construct, Construct parent, String literal) {
		super(construct.getDocument(), "keyvaluepair", parent);
	}
	
	public KeyValuePairConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "keyvaluepair", parent);
		
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
		KeyValuePairConstruct newCopy = new KeyValuePairConstruct(this, parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

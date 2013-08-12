package clojure.constructs.special;

import clojure.ClojureConstruct;
import clojure.constructs.BigIntConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

/**
 * A special construct returned as the parent to all
 * Clojure documents that allows multiple root-level 
 * clojure forms.
 * 
 * @author chrislord
 */
public class EmptyConstruct extends ClojureConstruct {
	public EmptyConstruct(ConstructDocument document) {
		super(document, "clojure", null);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append("\n\n");
			}
			builder.append("$(node)");
		}
		return builder.toString();
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public boolean isConstructContainer() { 
		return true;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		EmptyConstruct newCopy = new EmptyConstruct(mDocument);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

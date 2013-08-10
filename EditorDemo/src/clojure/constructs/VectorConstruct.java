package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class VectorConstruct extends ClojureConstruct {
	public VectorConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "vector", parent);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(BREAKING_SPACE);
			}
			builder.append("$(node)");
		}

		builder.append("]");
		return super.layout(builder.toString());
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override 
	public boolean canInsertChildren() { 
		return true;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		VectorConstruct newCopy = new VectorConstruct(mDocument, parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class VectorConstruct extends ClojureConstruct {
	public VectorConstruct(Construct parent, String literal) {
		super("vector", parent);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(" ");
			}
			builder.append("$(node)");
		}

		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override 
	public boolean canInsertChildren() { 
		return true;
	}

}

package clojure.constructs.special;

import clojure.ClojureConstruct;
import clojure.constructs.BigIntConstruct;
import editor.Construct;

public class EmptyConstruct extends ClojureConstruct {
	public EmptyConstruct() {
		super("empty", null);
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
	public boolean canInsertChildren() { 
		return true;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		EmptyConstruct newCopy = new EmptyConstruct();
		super.deepCopy(newCopy);
		return newCopy;
	}
}

package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class BooleanConstruct extends ClojureConstruct {
	public BooleanConstruct(Construct parent, String literal) { 
		super("boolean", parent);
		this.literal = literal;
	}

	@Override
	public String screen_text() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
}

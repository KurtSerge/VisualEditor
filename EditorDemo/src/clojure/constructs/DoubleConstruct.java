package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class DoubleConstruct extends ClojureConstruct {

	public DoubleConstruct(Construct parent, String literal) { 
		super("double", parent);
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

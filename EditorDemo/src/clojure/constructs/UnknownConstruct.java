package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class UnknownConstruct extends ClojureConstruct {

	public UnknownConstruct(Construct parent) { 
		super("unknown", parent);
	}
	
	@Override
	public String screen_text() {
		return "__unknown__";
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		UnknownConstruct newCopy = new UnknownConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

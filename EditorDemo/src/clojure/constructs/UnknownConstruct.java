package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class UnknownConstruct extends ClojureConstruct {

	public UnknownConstruct(Construct parent) { 
		super("unknown", parent);
	}
	
	@Override
	public String screen_text() {
		// TODO Auto-generated method stub
		return "__unknown__";
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

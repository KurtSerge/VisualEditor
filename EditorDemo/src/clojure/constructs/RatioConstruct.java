package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class RatioConstruct extends ClojureConstruct {

	public RatioConstruct(Construct parent, String literal) {
		super("ratio", parent);
		this.literal = literal;
	}
	
	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

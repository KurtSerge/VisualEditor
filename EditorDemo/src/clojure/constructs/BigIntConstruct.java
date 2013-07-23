package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class BigIntConstruct extends ClojureConstruct {
	
	public BigIntConstruct(Construct parent, String literal) {
		super("bigint", parent);
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

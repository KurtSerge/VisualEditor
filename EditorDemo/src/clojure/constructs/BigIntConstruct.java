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
		return null;
	}

	@Override
	public boolean validate() {
		return false;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		BigIntConstruct newCopy = new BigIntConstruct(parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

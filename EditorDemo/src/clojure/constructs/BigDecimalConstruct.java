package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class BigDecimalConstruct extends ClojureConstruct {

	public BigDecimalConstruct(Construct parent, String literal) {
		super("bigdecimal", parent);
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

}

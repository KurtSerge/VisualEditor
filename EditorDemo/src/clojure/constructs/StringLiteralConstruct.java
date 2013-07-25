package clojure.constructs;

import clojure.ClojureConstruct;
public class StringLiteralConstruct extends ClojureConstruct {
	public StringLiteralConstruct(StringConstruct parent, String literal) { 
		super("stringliteral", parent);
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

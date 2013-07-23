package clojure.constructs;

import clojure.ClojureConstruct;
public class StringLiteralConstruct extends ClojureConstruct {
	public StringLiteralConstruct(StringConstruct parent, String literal) { 
		super("stringliteral", parent);
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

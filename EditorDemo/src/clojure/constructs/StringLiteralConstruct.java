package clojure.constructs;

import editor.Construct;

public class StringLiteralConstruct extends Construct {
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

package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class KeywordConstruct extends ClojureConstruct {
	public KeywordConstruct(Construct parent, String literal) {
		super("keyword", parent);
		
		this.literal = literal;
	}

	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		return this.literal.matches("^[a-zA-Z]([a-zA-Z0-9\\*\\?!+_-]+)?$");
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		KeywordConstruct newCopy = new KeywordConstruct(parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

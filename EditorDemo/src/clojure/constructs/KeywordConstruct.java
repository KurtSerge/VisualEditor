package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class KeywordConstruct extends ClojureConstruct {
	public KeywordConstruct(Construct parent, String literal) {
		super("keyword", parent);
		
		
		this.addChild(0, new StringLiteralConstruct(this, literal));
	}

	@Override
	public String screen_text() {
		return ":$(node)";
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		KeywordConstruct newCopy = new KeywordConstruct(parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

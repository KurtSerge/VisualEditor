package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class KeywordConstruct extends ClojureConstruct {
	public KeywordConstruct(Construct parent, String literal) {
		super("keyword", parent);

		SymbolConstruct symbolConstruct = new SymbolConstruct(this, literal);
		symbolConstruct.setIsSoleDependantConstruct(true);
		this.addChild(0, symbolConstruct);
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
	
	@Override
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) {
		return false;
	}
}

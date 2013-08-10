package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class KeywordConstruct extends ClojureConstruct {
	public KeywordConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "keyword", parent);

		SymbolConstruct symbolConstruct = new SymbolConstruct(mDocument, this, literal);
		symbolConstruct.setIsSoleDependantConstruct(true);
		this.addChild(0, symbolConstruct);
	}

	@Override
	public String screen_text() {
		return ":$(node)";
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		KeywordConstruct newCopy = new KeywordConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	@Override
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) {
		return false;
	}
}

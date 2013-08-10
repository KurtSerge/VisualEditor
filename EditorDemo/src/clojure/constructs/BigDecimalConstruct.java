package clojure.constructs;

import json.ArrayConstruct;
import clojure.ClojureConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class BigDecimalConstruct extends ClojureConstruct {

	public BigDecimalConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "bigdecimal", parent);
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
		BigDecimalConstruct newCopy = new BigDecimalConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

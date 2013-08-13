package clojure.constructs;

import clojure.ClojureConstruct;
import construct.Construct;
import editor.document.ConstructDocument;

public class BigDecimalConstruct extends ClojureConstruct {

	public BigDecimalConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "bigdecimal", parent);
		this.literal = literal;
		if(this.literal == null) 
			this.literal = "0";
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

package clojure.constructs;

import clojure.ClojureConstruct;
import construct.Construct;
import editor.document.ConstructDocument;

public class BigIntConstruct extends ClojureConstruct {
	
	public BigIntConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "bigint", parent);
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
		BigIntConstruct newCopy = new BigIntConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

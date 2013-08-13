package clojure.constructs;

import clojure.ClojureConstruct;
import construct.Construct;
import editor.document.ConstructDocument;

public class RatioConstruct extends ClojureConstruct {

	public RatioConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "ratio", parent);
		this.literal = literal;
	}
	
	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		RatioConstruct newCopy = new RatioConstruct(mDocument, parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

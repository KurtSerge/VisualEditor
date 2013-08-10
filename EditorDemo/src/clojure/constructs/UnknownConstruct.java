package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class UnknownConstruct extends ClojureConstruct {

	public UnknownConstruct(ConstructDocument document, Construct parent) { 
		super(document, "unknown", parent);
	}
	
	@Override
	public String screen_text() {
		return "__unknown__";
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		UnknownConstruct newCopy = new UnknownConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

package clojure.constructs;

import construct.Construct;
import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

public class IntegerConstruct extends ClojureConstruct {
	public IntegerConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "integer", parent);
		
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
		try {
			Integer.parseInt(this.literal);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		IntegerConstruct newCopy = new IntegerConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

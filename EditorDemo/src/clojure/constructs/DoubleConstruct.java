package clojure.constructs;

import construct.Construct;
import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

public class DoubleConstruct extends ClojureConstruct {

	public DoubleConstruct(ConstructDocument document, Construct parent, String literal) { 
		super(document, "double", parent);
		this.literal = literal;
	}
	
	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		try { 
			Double.parseDouble(this.literal);
			return true;
		} catch(NumberFormatException ex) {
		}
		
		return false;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		DoubleConstruct newCopy = new DoubleConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

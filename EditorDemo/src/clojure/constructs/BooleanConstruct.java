package clojure.constructs;

import clojure.ClojureConstruct;
import construct.Construct;
import editor.document.ConstructDocument;

public class BooleanConstruct extends ClojureConstruct {
	public BooleanConstruct(ConstructDocument document, Construct parent, String literal) { 
		super(document, "boolean", parent);
		this.literal = literal;
		if(this.literal == null) 
			this.literal = "false";		
	}

	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		try { 
			Boolean.parseBoolean(this.literal);
			return true;
		} catch(NumberFormatException ex) { 
			return false;
		}
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		BigDecimalConstruct newCopy = new BigDecimalConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

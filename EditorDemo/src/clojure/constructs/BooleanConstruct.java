package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class BooleanConstruct extends ClojureConstruct {
	public BooleanConstruct(Construct parent, String literal) { 
		super("boolean", parent);
		this.literal = literal;
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
		BigDecimalConstruct newCopy = new BigDecimalConstruct(parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

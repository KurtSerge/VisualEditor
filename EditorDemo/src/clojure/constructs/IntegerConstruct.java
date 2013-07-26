package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class IntegerConstruct extends ClojureConstruct {
	public IntegerConstruct(Construct parent, String literal) {
		super("integer", parent);
		
		this.literal = literal;
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
		IntegerConstruct newCopy = new IntegerConstruct(parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

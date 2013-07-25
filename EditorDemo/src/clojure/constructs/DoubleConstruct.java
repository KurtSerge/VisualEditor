package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class DoubleConstruct extends ClojureConstruct {

	public DoubleConstruct(Construct parent, String literal) { 
		super("double", parent);
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
}

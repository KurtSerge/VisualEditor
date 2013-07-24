package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class SymbolConstruct extends ClojureConstruct {
	public SymbolConstruct(Construct parent, String literal) { 
		super("symbol", parent);
		
		this.literal = literal.toString();
	}

	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		return false;
	}
}

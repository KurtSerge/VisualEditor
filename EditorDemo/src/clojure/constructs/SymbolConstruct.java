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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
}

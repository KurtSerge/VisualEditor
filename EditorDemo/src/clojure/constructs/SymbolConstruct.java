package clojure.constructs;

import editor.Construct;

public class SymbolConstruct extends Construct {
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

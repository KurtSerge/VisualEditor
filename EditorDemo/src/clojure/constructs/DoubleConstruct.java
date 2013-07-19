package clojure.constructs;

import editor.Construct;

public class DoubleConstruct extends Construct {

	public DoubleConstruct(Construct parent, String literal) { 
		super("double", parent);
		this.literal = literal;
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

package clojure.constructs;

import editor.Construct;

public class BigDecimalConstruct extends Construct {

	public BigDecimalConstruct(Construct parent, String literal) {
		super("bigdecimal", parent);
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

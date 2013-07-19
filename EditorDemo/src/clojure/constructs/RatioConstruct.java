package clojure.constructs;

import editor.Construct;

public class RatioConstruct extends Construct {

	public RatioConstruct(Construct parent, String literal) {
		super("ratio", parent);
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

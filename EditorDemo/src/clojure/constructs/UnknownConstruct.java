package clojure.constructs;

import editor.Construct;

public class UnknownConstruct extends Construct {

	public UnknownConstruct(Construct parent) { 
		super("unknown", parent);
	}
	
	@Override
	public String screen_text() {
		// TODO Auto-generated method stub
		return "__unknown__";
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

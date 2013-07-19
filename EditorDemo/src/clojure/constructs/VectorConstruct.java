package clojure.constructs;

import editor.Construct;

public class VectorConstruct extends Construct {
	public VectorConstruct(Construct parent, String literal) {
		super("vector", parent);
	}

	@Override
	public String screen_text() {
		return "[]";
	}

	@Override
	public boolean validate() {
		return false;
	}

}

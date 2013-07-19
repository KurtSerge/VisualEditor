package clojure.constructs;

import editor.Construct;

public class IntegerConstruct extends Construct {
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
			int test = Integer.parseInt(this.literal);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
}

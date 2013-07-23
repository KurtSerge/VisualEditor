package clojure.constructs;

import clojure.ClojureConstruct;
import clojure.constructs.StringLiteralConstruct;
import editor.Construct;

public class StringConstruct extends ClojureConstruct {
	
	public StringConstruct(Construct parent, String literal) { 
		super("string", parent);
		
		StringLiteralConstruct stringLiteralConstruct = new StringLiteralConstruct(this, literal);
		children.add(stringLiteralConstruct);		
	}

	@Override
	public String screen_text() {
		assert(1 == children.size());
		assert(StringLiteralConstruct.class.equals(children.get(0).getClass()));

		return "\"$(node)\"";
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

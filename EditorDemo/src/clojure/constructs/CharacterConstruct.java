package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class CharacterConstruct extends ClojureConstruct {

	public CharacterConstruct(Construct parent, String literal) { 
		super("character", parent);
		this.literal = literal;
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

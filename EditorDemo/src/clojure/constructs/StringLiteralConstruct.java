package clojure.constructs;

import java.awt.Color;

import clojure.ClojureConstruct;
import editor.Construct;
public class StringLiteralConstruct extends ClojureConstruct {
	public StringLiteralConstruct(ClojureConstruct parent, String literal) { 
		super("stringliteral", parent);
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
	
	@Override
	public Color debug_getForegroundColor() {
		return new Color(144, 33, 104);
	}
}

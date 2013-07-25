package clojure.constructs.special;

import java.awt.Color;

import clojure.ClojureConstruct;

public class PlaceholderConstruct extends ClojureConstruct {
	
	public PlaceholderConstruct(ClojureConstruct parent, String literal) { 
		super("placeholder", parent);
		
		mString = literal;
	}

	@Override
	public String screen_text() {
		return mString;
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	public Color debug_getForegroundColor() { 
		return Color.LIGHT_GRAY;
	}
	
	private String mString;
}

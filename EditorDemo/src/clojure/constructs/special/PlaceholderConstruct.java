package clojure.constructs.special;

import java.awt.Color;

import clojure.ClojureConstruct;
import clojure.constructs.meta.IfThenElseConstruct;
import editor.Construct;

public class PlaceholderConstruct extends ClojureConstruct {
	
	public PlaceholderConstruct(Construct parent, String literal) { 
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
	
	@Override
	public Construct deepCopy(Construct parent) {
		PlaceholderConstruct newCopy = new PlaceholderConstruct(parent, mString);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

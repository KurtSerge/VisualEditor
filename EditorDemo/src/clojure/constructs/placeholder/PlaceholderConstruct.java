package clojure.constructs.placeholder;

import java.awt.Color;

import clojure.ClojureConstruct;
import clojure.constructs.meta.IfThenElseConstruct;
import editor.Construct;

public class PlaceholderConstruct extends ClojureConstruct {
	
	public PlaceholderConstruct(Construct parent, Placeholder placeholder) { 
		super("placeholder", parent);
		
		String displayText = placeholder.getHint();
		if(placeholder.isVariadic()) { 
			displayText = displayText.concat("*");
		} else if(placeholder.isOptional()) { 
			displayText = displayText.concat("?");
		}		
		
		mString = displayText;
		mDescriptor = placeholder;
	}
	
	public Placeholder getDescriptor() { 
		return mDescriptor;
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
		PlaceholderConstruct newCopy = new PlaceholderConstruct(parent, mDescriptor);
		super.deepCopy(newCopy);
		return newCopy;
	}

	private Placeholder mDescriptor;
}

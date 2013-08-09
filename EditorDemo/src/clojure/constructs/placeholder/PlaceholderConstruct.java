package clojure.constructs.placeholder;

import java.awt.Color;
import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
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
	
	@Override
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

	@Override
	public boolean onReceivedRawKey(KeyEvent e) {
		if(getDescriptor().isVariadic()) { 
			return false;
		}
		
		int keyCode = e.getKeyCode();

		if(keyCode == KeyEvent.VK_QUOTE) { 
			return parent.replaceChild(this, new StringConstruct(parent, ""));
		}
		
		if(keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z &&
				getDescriptor().isAllowed(SymbolConstruct.class))
		{
			// This is alphabetic and should be translated as the start of a symbol
			String keyEventText = KeyEvent.getKeyText(keyCode);
			if(e.isShiftDown() == false) { 
				keyEventText = keyEventText.toLowerCase();
			}
			
			SymbolConstruct replacementConstruct = new SymbolConstruct(parent, keyEventText);
			return parent.replaceChild(this, replacementConstruct);
		}
		
		if(getDescriptor().getClassRestriction() != null) { 
			if(getDescriptor().getClassRestriction().equals(StringConstruct.class) &&
					keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
				// If we have a string restriction then take the
				// key text and throw it into a new StringConstruct 
				String keyEventText = KeyEvent.getKeyText(keyCode); 
				if(e.isShiftDown() == false) { 
					keyEventText = keyEventText.toLowerCase();
				}
				
				StringConstruct replacementConstruct = new StringConstruct(parent, keyEventText);
				return parent.replaceChild(this, replacementConstruct);
			}
		}
		
		if(keyCode == KeyEvent.VK_ENTER) { 
			if(getDescriptor().getClassRestriction() != null) {
				// We'll autobox this selection				
				Class<?> restrictedClass = getDescriptor().getClassRestriction();
				if(restrictedClass.equals(StringConstruct.class)) {
					StringConstruct replacingConstruct = new StringConstruct(parent, "");
					return parent.replaceChild(this, replacingConstruct);
				} else if(restrictedClass.equals(SymbolConstruct.class)) { 
					SymbolConstruct replacingConstruct = new SymbolConstruct(parent, null);
					return parent.replaceChild(this, replacingConstruct);
				}
			}
		}

		return false;
	}	

	private Placeholder mDescriptor;
}

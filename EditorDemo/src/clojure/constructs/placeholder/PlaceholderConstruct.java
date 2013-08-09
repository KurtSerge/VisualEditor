package clojure.constructs.placeholder;

import java.awt.Color;
import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import clojure.constructs.IntegerConstruct;
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
	
	private boolean isStartOfIntegerConstruct(KeyEvent e) { 
		return e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9;
	}
	
	private boolean isStartOfSymbolConstruct(KeyEvent e) {
		if((e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z) ||
				e.getKeyCode() == KeyEvent.VK_QUOTE) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onReceivedKeyEvent(KeyEvent e, boolean isTyping) {
		System.out.println("PlaceholderConstruct::onReceivedKeyEvent('" + e.getKeyChar() + "', " + isTyping + ")");
		
		if(getDescriptor().isVariadic() || isTyping == true) { 
			return false;
		}
		
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_QUOTE && e.isShiftDown()) { 
			return parent.replaceChild(this, new StringConstruct(parent, ""));
		}
		
		if(getDescriptor().isAllowed(IntegerConstruct.class) && isStartOfIntegerConstruct(e)) { 
			String keyEventText = KeyEvent.getKeyText(keyCode);
			IntegerConstruct replacementConstruct = new IntegerConstruct(parent, keyEventText);
			return parent.replaceChild(this, replacementConstruct);
		}
		
		if(getDescriptor().isAllowed(SymbolConstruct.class) && isStartOfSymbolConstruct(e)) {
			String keyEventText = KeyEvent.getKeyText(keyCode);
			if(e.isShiftDown() == false) { 
				keyEventText = keyEventText.toLowerCase();
			}
			
			SymbolConstruct replacementConstruct = new SymbolConstruct(parent, keyEventText);
			return parent.replaceChild(this, replacementConstruct);
		}
		
		// Auto swap this construct with a StringConstruct if
		// that is the specified class restriction
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

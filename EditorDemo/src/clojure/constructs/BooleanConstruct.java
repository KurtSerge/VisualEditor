package clojure.constructs;

import java.awt.Color;
import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import construct.Construct;
import construct.Construct.ConstructAction;
import editor.document.ConstructDocument;

public class BooleanConstruct extends ClojureConstruct {
	public BooleanConstruct(ConstructDocument document, Construct parent, String literal) { 
		super(document, "boolean", parent);
		if(literal != null) { 
			mValue = Boolean.parseBoolean(literal);	
		} else { 
			mValue = false;
		}
	}

	@Override
	public String screen_text() {
		if(mValue == true) { 
			return "true";
		} else { 
			return "false";
		}
	}

	@Override
	public boolean validate() {
		try { 
			Boolean.parseBoolean(this.literal);
			return true;
		} catch(NumberFormatException ex) { 
			return false;
		}
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		BooleanConstruct newCopy = new BooleanConstruct(mDocument, parent, mValue.toString());
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	@Override
	public Color debug_getForegroundColor() {
		return new Color(255, 20, 147);
	}
	
	/**
	 * Allow only a-z, A-Z, 0-9
	 * 
	 * Disallow starting with 0-9
	 * 
	 * @param e The KeyEvent causing the trigger
	 * @param isTyping True if editing this.literal
	 * @return True to consume the event ( can also call e.consume() )
	 */
	public ConstructAction onReceivedKeyEvent(KeyEvent keyEvent, boolean isTyping) {
		if(keyEvent.getKeyCode() == KeyEvent.VK_T) {
			mValue = true;
			return ConstructAction.Refresh;
		}
		
		if(keyEvent.getKeyCode() == KeyEvent.VK_F) { 
			mValue = false;
			return ConstructAction.Refresh;
		}
			
		return ConstructAction.None;
	}	
	
	Boolean mValue = false;
}

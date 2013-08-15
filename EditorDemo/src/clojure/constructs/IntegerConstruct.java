package clojure.constructs;

import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import construct.Construct;
import editor.document.ConstructDocument;

public class IntegerConstruct extends ClojureConstruct {
	public IntegerConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "integer", parent);
		
		this.literal = literal;
		if(this.literal == null) 
			this.literal = "0";	
	}

	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		try {
			Integer.parseInt(this.literal);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		IntegerConstruct newCopy = new IntegerConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	/**
	 * Automatic deletion of this construct 
	 * @param e
	 * @return
	 */
	private ConstructAction deleteIfAlreadyEmpty(KeyEvent e, ConstructAction defaultResponse) {
		if((int)e.getKeyChar() == 8) { // Backspace
			if(this.literal.length() == 0) { 
				return ConstructAction.DeleteThis;
			}
		}
		
		return defaultResponse;
	}
	
	/**
	 * @param e The KeyEvent causing the trigger
	 * @param isTyping True if editing this.literal
	 * @return True to consume the event ( can also call e.consume() )
	 */
	@SuppressWarnings("unused")
	public ConstructAction onReceivedKeyEvent(KeyEvent e, boolean isTyping) {
		if(isTyping) {
			// Parse this as a long
			String pendingLiteral = this.literal + e.getKeyChar();
			try { 
				// Attempt parsing this number as an integer
				Long integer = Long.parseLong(pendingLiteral);
			} catch(NumberFormatException ex) { 
				return deleteIfAlreadyEmpty(e, ConstructAction.ConsumeEvent);
			}		
		}
		
		return ConstructAction.None;
	}
}

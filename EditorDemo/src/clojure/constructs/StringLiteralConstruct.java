package clojure.constructs;

import java.awt.Color;
import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import editor.document.ConstructDocument;
public class StringLiteralConstruct extends ClojureConstruct {
	public StringLiteralConstruct(ConstructDocument document, ClojureConstruct parent, String literal) { 
		super(document, "stringliteral", parent);
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
	
	public ConstructAction onReceivedKeyEvent(KeyEvent keyEvent, boolean isTyping) {
		if(isTyping) {
			if((int)keyEvent.getKeyChar() == 34) { 
				// Quotes are not allowed
				System.out.println("TODO: Allow quotes, but automatically escape them");
				return ConstructAction.ConsumeEvent;
			}

			if((int)keyEvent.getKeyChar() == 8 && this.literal.length() == 0) {
				keyEvent.consume();
				return ConstructAction.DeleteThis;
			}
		}

		return ConstructAction.None;
	}	
}

package clojure.constructs;

import java.awt.Color;
import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class SymbolConstruct extends ClojureConstruct {
	
	public static Integer[] sSpecialCharacters = {
		38,		// &
		39,		// '
		42,		// *
		43,		// +
		45, 	// -
		46,		// .
		47,		// /
	};
	
	
	/**
	 * Mutable.
	 * 
	 * @param parent
	 * @param literal
	 */
	public SymbolConstruct(ConstructDocument document, Construct parent, String literal) { 
		this(document, parent, literal, true);
	}
	
	/**
	 * Support for immutable symbols.
	 * 
	 * @param parent
	 * @param symbol
	 * @param mutable
	 */
	public SymbolConstruct(ConstructDocument document, Construct parent, String symbol, boolean mutable) { 
		super(document, "symbol", parent);
		
		if(mutable == false) { 
			this.mMutable = false;
			this.literal = null;
			this.mImmutableSymbol = symbol;
		} else { 
			this.mMutable = true;
			this.literal = symbol;
			this.mImmutableSymbol = null;
		}
	}

	@Override
	public String screen_text() {
		return mImmutableSymbol;
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public Color debug_getForegroundColor() { 
		if(mImmutableSymbol == null) { 
			return new Color(36, 26, 196);
		} else { 
			return new Color(0, 153, 0);
		}
	}
	
	private boolean mMutable;
	private String mImmutableSymbol;
	
	
	@Override
	public Construct deepCopy(Construct parent) {
		SymbolConstruct newCopy;
		
		if(mMutable) { 
			newCopy = new SymbolConstruct(mDocument, parent, this.literal, true);
		} else { 
			newCopy = new SymbolConstruct(mDocument, parent, mImmutableSymbol, false);
		}
		
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	private boolean keyEventWillEmptyConstruct(KeyEvent e) {
		if((int)e.getKeyChar() == 8) { // Backspace
			if(this.literal.length() == 0) { 
				return true;
			}
			
//			if(this.literal.length() == 1) { 
//				ClojureConstruct parentForm = (ClojureConstruct) this.parent;
//				if(parentForm != null && 
//						parentForm.getPlaceholders() != null) { 
//					return true;
//				}
//			}
		}
		
		return false;
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
		if(isTyping) {
			if(((int) keyEvent.getKeyChar() >= 48 && (int) keyEvent.getKeyChar() <= 57)) {
				if(this.literal.length() == 0) {
					// Consume
					return ConstructAction.ConsumeEvent;
				}
				
				return ConstructAction.None;
			}

			if(((int) keyEvent.getKeyChar() >= 97 && (int) keyEvent.getKeyChar() <= 122) ||   // a-z
				((int) keyEvent.getKeyChar() >= 65 && (int) keyEvent.getKeyChar() <= 90))		// A-Z
			{
				return ConstructAction.None;
			} 
			
			// Check to see if the special character is allowed
			for(int i = 0; i < sSpecialCharacters.length; i++) { 
				if(sSpecialCharacters[i] == (int) keyEvent.getKeyChar()) {
					return ConstructAction.None;
				}
			}

			if(keyEventWillEmptyConstruct(keyEvent) == true) {
				keyEvent.consume();
				return ConstructAction.DeleteThis;
			}

			return ConstructAction.ConsumeEvent;
		}

		return ConstructAction.None;
	}	
}

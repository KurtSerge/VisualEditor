package clojure.constructs.containers;

import java.awt.Color;
import java.awt.event.KeyEvent;

import clojure.ClojureConstruct;
import construct.Construct;
import construct.Construct.ConstructAction;
import editor.document.ConstructDocument;

public class ListConstruct extends ClojureConstruct {
	
	public static String AFFIX = ")";
	public static String PREFIX = "(";

	public ListConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "list", parent);
	}
	
	public ListConstruct(ConstructDocument document, String type, Construct parent) { 
		super(document, type, parent);
		
		mCall = type;
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		if(mPrefix != null) { 
			builder.append(mPrefix);
		}
		
		builder.append(PREFIX);
		
		if(mCall != null) { 
			builder.append(mCall);
			if(getChildren().size() != 0) { 
				builder.append(" ");
			}
		}

		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(BREAKING_SPACE);
			}
			
			if(this.getIsMultilined() && i != 0) { 
				int spacerLength = PREFIX.length();
				if(mCall != null) { 
					spacerLength += mCall.length() + 1;
				}
				
				for(int j = 0; j < spacerLength; j++) { 
					builder.append(" ");
				}
			}
			
			builder.append("$(node)");
		}

		builder.append(AFFIX);
		
		return super.layout(builder.toString());
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override 
	public boolean isConstructContainer() { 
		return mCall == null;
	}
	
	@Override
	public Color debug_getForegroundColor() { 
		if(mCall == null) { 
			return new Color(0, 0, 0);
		} else { 
			return new Color(0, 153, 0);
		}
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		ListConstruct newCopy = new ListConstruct(mDocument, parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	public void deepCopyChildrenTo(Construct parent) { 
		super.deepCopy(parent);
	}
	
	/**
	 * @param e The KeyEvent causing the trigger
	 * @param isTyping True if editing this.literal
	 * @return True to consume the event ( can also call e.consume() )
	 */
	public ConstructAction onReceivedKeyEvent(KeyEvent keyEvent, boolean isTyping) {
		System.out.println((int) keyEvent.getKeyCode());
		
		if(keyEvent.getKeyCode() == KeyEvent.VK_QUOTE) {
			if(mPrefix == null) { 
				mPrefix = "'";
			} else { 
				mPrefix = mPrefix.equalsIgnoreCase("'") ? null : "'";
			}
			
			return ConstructAction.Refresh;
		}

		return super.onReceivedKeyEvent(keyEvent, isTyping);
	}	
	
	private String mPrefix;
	private String mCall;
}

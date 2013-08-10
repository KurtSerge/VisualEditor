package clojure.constructs;

import java.awt.Color;

import clojure.ClojureConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class ListConstruct extends ClojureConstruct {

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
		builder.append("(");
		
		if(mCall != null)
			builder.append(mCall + " ");

		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(BREAKING_SPACE);
			}
			
			if(mCall != null && this.getIsMultilined() && i != 0) { 
				for(int j = 0; j < mCall.length() + 2; j++) { 
					builder.append(" ");
				}
			}
			
			builder.append("$(node)");
		}

		builder.append(")");
		
		return super.layout(builder.toString());
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override 
	public boolean canInsertChildren() { 
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
	
	private String mCall;
}

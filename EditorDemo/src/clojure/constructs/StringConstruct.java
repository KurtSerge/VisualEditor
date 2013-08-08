package clojure.constructs;

import java.awt.Color;

import clojure.ClojureConstruct;
import clojure.constructs.StringLiteralConstruct;
import editor.Construct;

public class StringConstruct extends ClojureConstruct {
	
	public StringConstruct(Construct parent, String literal) { 
		super("string", parent);
		
		StringLiteralConstruct stringLiteralConstruct = new StringLiteralConstruct(this, literal);
		children.add(stringLiteralConstruct);		
	}

	@Override
	public String screen_text() {
		assert(1 == children.size());
		assert(StringLiteralConstruct.class.equals(children.get(0).getClass()));

		return "\"$(node)\"";
	}

	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		StringConstruct newCopy = new StringConstruct(parent, this.children.get(0).literal);
		return newCopy;
	}
	
	@Override
	public Color debug_getForegroundColor() {
		return new Color(144, 33, 104);
	}
	
	@Override
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) {
		return false;
	}	
	
	@Override
	public Construct getConstructForSelection(SelectionType type) { 
		if(type == SelectionType.AutoboxedReplacement) { 
			return this.children.get(0);
		}
		
		return super.getConstructForSelection(type);
	}
}

package clojure.constructs;

import java.awt.Color;

import construct.Construct;

import clojure.ClojureConstruct;
import clojure.constructs.StringLiteralConstruct;
import editor.document.ConstructDocument;

public class StringConstruct extends ClojureConstruct {
	
	public StringConstruct(ConstructDocument document, Construct parent, String literal) { 
		super(document, "string", parent);
		
		StringLiteralConstruct stringLiteralConstruct = new StringLiteralConstruct(document, this, literal);
		stringLiteralConstruct.setIsSoleDependantConstruct(true);
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
		StringConstruct newCopy = new StringConstruct(mDocument, parent, this.children.get(0).literal);
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
	public Construct getConstructForSelection(SelectionCause type) { 
		if(type == SelectionCause.SelectedReplacementDiscoveredDuringKeyEvent ||
				type == SelectionCause.SelectedAfterDuplicatingSibling || 
				type == SelectionCause.SelectedAfterInsert)
		{
			// Select the child, not this
			return this.children.get(0);
		}
		
		return super.getConstructForSelection(type);
	}
}

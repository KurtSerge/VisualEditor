package clojure.constructs.meta;

import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

public class SymbolList extends ListConstruct {

	public SymbolList(ConstructDocument document, Construct parent, String literal) {
		super(document, parent, literal);
		
		children.add(new SymbolConstruct(mDocument, this, "symbol"));
	}
	
	@Override
	public Construct getConstructForSelection(SelectionCause type) { 
		if(type == SelectionCause.SelectedAfterInsert) {
			// Select the child, not this
			return this.children.get(0);
		}
		
		return super.getConstructForSelection(type);
	}
}

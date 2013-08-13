package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

public class SymbolList extends ListConstruct {

	public SymbolList(ConstructDocument document, Construct parent, String literal) {
		super(document, parent, literal);

		LinkedList<Placeholder> paramsPlaceholders = new LinkedList<Placeholder>();
		paramsPlaceholders.add(Placeholder.createPermanentPlaceholder(new SymbolConstruct(mDocument, this, "symbol")));
		paramsPlaceholders.add(Placeholder.createVariadicPlaceholder("exprs"));
		setPlaceholders(paramsPlaceholders);
	}
	
	@Override
	public Construct getConstructForSelection(SelectionCause type) { 
		if(type == SelectionCause.SelectedAfterInsert) {
			// Select the child, not this
			return this.children.get(0);
		}
		
		return super.getConstructForSelection(type);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		SymbolList newCopy = new SymbolList(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}	
}

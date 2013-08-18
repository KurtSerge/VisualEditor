package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

public class SymbolList extends MetaConstruct {

	public SymbolList(SymbolList construct, Construct parent) {
		super(construct.getDocument(), null, parent);
		
		this.literal = construct.literal;
	}		

	public SymbolList(ConstructDocument document, Construct parent) {
		super(document, null, parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new SymbolConstruct(mDocument, this, "function-name")));
		placeholders.add(Placeholder.createVariadicPlaceholder("args", KeyValuePairConstruct.class));
		setPlaceholders(placeholders);
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
		SymbolList newCopy = new SymbolList(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}	
}

package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.ClojureConstruct.Placeholder;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.special.PlaceholderConstruct;
import editor.Construct;

public class IfThenElseConstruct extends ListConstruct {

	public IfThenElseConstruct(Construct parent) {
		super(parent, null);

		LinkedList<ClojureConstruct.Placeholder> placeholders = new LinkedList<ClojureConstruct.Placeholder>();
		
		placeholders.add(Placeholder.createPermanentPlaceholder(new SymbolConstruct(this, "if", false)));
		placeholders.add(Placeholder.createPlaceholder("test"));
		placeholders.add(Placeholder.createPlaceholder("then"));
		placeholders.add(Placeholder.createOptionalPlaceholder("else"));
		
		setPlaceholders(placeholders);
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	public boolean canDeleteChild(int index, Construct child) {  
		if(index == 0) { 
			return false;
		}
		
		return super.canDeleteChild(index, child);
	}
	
	public boolean canInsertChildren() { 
		return false;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		IfThenElseConstruct newCopy = new IfThenElseConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

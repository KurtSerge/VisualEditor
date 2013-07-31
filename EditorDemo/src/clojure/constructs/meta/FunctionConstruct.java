package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.special.VariadicVectorConstruct;
import editor.Construct;

public class FunctionConstruct extends ListConstruct {

	public FunctionConstruct(Construct parent) {
		super("fn", parent);
		
		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createOptionalPlaceholder("name", SymbolConstruct.class));
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(this, "param")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));
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
		FunctionConstruct newCopy = new FunctionConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

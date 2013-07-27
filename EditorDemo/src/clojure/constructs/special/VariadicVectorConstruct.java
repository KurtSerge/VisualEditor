package clojure.constructs.special;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.VectorConstruct;
import editor.Construct;

public class VariadicVectorConstruct extends VectorConstruct {
	
	public VariadicVectorConstruct(Construct parent, String hint) { 
		super(parent, null);
		
		LinkedList<ClojureConstruct.Placeholder> paramsPlaceholders = new LinkedList<ClojureConstruct.Placeholder>();
		paramsPlaceholders.add(ClojureConstruct.Placeholder.createVariadicPlaceholder(hint));
		setPlaceholders(paramsPlaceholders);
	}
	
	/**
	 * Variadic Vector only has variadic values,
	 * so cannot support arbitrary child addition.
	 */
	@Override 
	public boolean canInsertChildren() { 
		return false;
	}
}

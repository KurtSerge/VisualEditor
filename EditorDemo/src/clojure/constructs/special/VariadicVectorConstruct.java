package clojure.constructs.special;

import java.util.LinkedList;

import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import editor.Construct;
import editor.document.ConstructDocument;

/**
 * The VariadicVectorConstruct is an empty vector which contains
 * a single variadic placeholder. It disables the addition of 
 * arbitrary children.
 * 
 * TODO: Add class-type restriction support?
 * 
 * @author chrislord
 */
public class VariadicVectorConstruct extends VectorConstruct {
	
	public VariadicVectorConstruct(ConstructDocument document, Construct parent, String hint) { 
		super(document, parent, null);
		
		LinkedList<Placeholder> paramsPlaceholders = new LinkedList<Placeholder>();
		paramsPlaceholders.add(Placeholder.createVariadicPlaceholder(hint));
		setPlaceholders(paramsPlaceholders);
	}

	@Override 
	public boolean isConstructContainer() { 
		return false;
	}
}

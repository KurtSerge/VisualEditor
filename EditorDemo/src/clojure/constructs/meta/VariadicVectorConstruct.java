package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct.ConstructType;
import clojure.constructs.containers.VectorConstruct;
import construct.Construct;
import construct.Placeholder;
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

	/**
	 * This construct is a 'UserCollection' construct;
	 * freely allow all actions that manpulate children.
	 */
	@Override
	public ConstructType getConstructType() { 
		return ConstructType.StrictCollection;
	}
}

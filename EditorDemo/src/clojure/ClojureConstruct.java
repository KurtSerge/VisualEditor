package clojure;

import construct.Construct;
import construct.PlaceholdingConstruct;
import editor.document.ConstructDocument;

/**
 * TODO: validate() should ensure all required placeholders are replaced
 * 
 * @author Christopher Lord
 */
public abstract class ClojureConstruct extends PlaceholdingConstruct
{	
	public static enum ConstructType {
		Primitive,			// No children
		UserCollection,		// Multiple user-addable children
		StrictCollection,	// Strict formatting		
	}

	public ClojureConstruct(ConstructDocument document, String type, Construct parent) {
		super(document, type, parent);
	}
	
	public void onBranchHighlighted() {
		insertPlaceholders();
	}
	
	public void onBranchUnhighlighted() {
		removePlaceholders(false);
	}
	
	/**
	 * By default, no ClojureConstruct can have insert children.
	 * This needs to be overridden by container constructs such
	 * as Vector, Map and List.
	 * 
	 * @return If a child can be inserted or not.
	 */
	public ConstructType getConstructType() { 
		return ConstructType.Primitive;
	}	
	
	@Override
	public boolean validate() {
		return false;
	}	
}

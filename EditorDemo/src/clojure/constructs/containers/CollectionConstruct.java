package clojure.constructs.containers;

import construct.Construct;
import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

public abstract class CollectionConstruct extends ClojureConstruct {
	
	/**
	 * Do nothing..
	 * 
	 * @param document
	 * @param type
	 * @param parent
	 */
	public CollectionConstruct(ConstructDocument document, String type, Construct parent) {
		super(document, type, parent);
	}
	
}

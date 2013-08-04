package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.KeyValuePairConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import editor.Construct;

/**
 * Syntax: (case test conditions*)
 * Where: 
 * 		- `conditions` is clojure.constructs.KeuValuePairConstruct
 * 
 * @author Christopher Lord
 */
public class CaseConstruct extends ListConstruct {

	public CaseConstruct(Construct parent) {
		super("case", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("test"));
		placeholders.add(Placeholder.createVariadicPlaceholder("conditions", KeyValuePairConstruct.class));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		CaseConstruct newCopy = new CaseConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}	
	
}

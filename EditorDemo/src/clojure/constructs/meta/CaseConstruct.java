package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.KeyValuePairConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import editor.Construct;
import editor.document.ConstructDocument;

/**
 * Syntax: (case test conditions*)
 * Where: 
 * 		- `conditions` is clojure.constructs.KeuValuePairConstruct
 * 
 * @author Christopher Lord
 */
public class CaseConstruct extends ListConstruct {

	public CaseConstruct(ConstructDocument document, Construct parent) {
		super(document, "case", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("test"));
		placeholders.add(Placeholder.createVariadicPlaceholder("conditions", KeyValuePairConstruct.class));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		CaseConstruct newCopy = new CaseConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}	
	
}

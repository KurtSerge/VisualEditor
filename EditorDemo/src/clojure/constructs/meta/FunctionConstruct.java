package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.special.VariadicVectorConstruct;
import editor.Construct;
import editor.document.ConstructDocument;

/**
 * Syntax: (fn name? [param*] exprs*)
 * Where:
 * 		- `name` is clojure.constructs.SymbolConstruct
 * 
 * @author Christopher Lord
 */
public class FunctionConstruct extends ListConstruct {

	public FunctionConstruct(ConstructDocument document, Construct parent) {
		super(document, "fn", parent);
		
		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createOptionalPlaceholder("name", SymbolConstruct.class));
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(mDocument, this, "param")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		FunctionConstruct newCopy = new FunctionConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
}

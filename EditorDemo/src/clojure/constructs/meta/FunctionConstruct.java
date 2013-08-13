package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
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

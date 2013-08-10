package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.special.VariadicVectorConstruct;
import editor.Construct;
import editor.document.ConstructDocument;
import clojure.ClojureConstruct.*;

/**
 * Syntax: (defn name doc-string? attr-map? [params*] exprs*)
 * Where:
 * 		- `doc-string` is clojure.constructs.StringConstruct
 * 
 * @author Christopher Lord
 */
public class DefineFunctionConstruct extends ListConstruct {

	public DefineFunctionConstruct(ConstructDocument document, Construct parent) {
		super(document, "defn", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("name"));
		placeholders.add(Placeholder.createOptionalPlaceholder("doc-string", StringConstruct.class));
		placeholders.add(Placeholder.createOptionalPlaceholder("attr-map"));
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(mDocument, this, "params")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		DefineFunctionConstruct newCopy = new DefineFunctionConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
}

package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.StringConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

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

package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.StringConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * Syntax: (def symbol doc-string? init?)
 * 
 * @author Christopher Lord
 */
public class DefConstruct extends ListConstruct {

	public DefConstruct(ConstructDocument document, Construct parent) {
		super(document, "def", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("symbol", clojure.constructs.SymbolConstruct.class));
		placeholders.add(Placeholder.createOptionalPlaceholder("doc-string", StringConstruct.class));
		placeholders.add(Placeholder.createOptionalPlaceholder("init"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		DefConstruct newCopy = new DefConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

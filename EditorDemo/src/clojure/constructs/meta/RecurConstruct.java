package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.StringConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * Syntax: (do exprs*)
 * 
 * @author Christopher Lord
 */
public class RecurConstruct extends ListConstruct {

	public RecurConstruct(ConstructDocument document, Construct parent) {
		super(document, "recur", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		RecurConstruct newCopy = new RecurConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

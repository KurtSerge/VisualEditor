package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * Syntax: (if test then else?)
 * 
 * @author Christopher Lord
 */
public class IfThenElseConstruct extends ListConstruct {

	public IfThenElseConstruct(ConstructDocument document, Construct parent) {
		super(document, "if", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("test"));
		placeholders.add(Placeholder.createPlaceholder("then"));
		placeholders.add(Placeholder.createOptionalPlaceholder("else"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		IfThenElseConstruct newCopy = new IfThenElseConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
}

package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.placeholder.PlaceholderConstruct;
import editor.Construct;

/**
 * Syntax: (if test then else?)
 * 
 * @author Christopher Lord
 */
public class IfThenElseConstruct extends ListConstruct {

	public IfThenElseConstruct(Construct parent) {
		super("if", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("test"));
		placeholders.add(Placeholder.createPlaceholder("then"));
		placeholders.add(Placeholder.createOptionalPlaceholder("else"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		IfThenElseConstruct newCopy = new IfThenElseConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
}

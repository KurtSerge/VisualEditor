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
 * Syntax: (let [bindings*] exprs*)
 * 
 * @author Christopher Lord
 */
public class LetConstruct extends ListConstruct {

	public LetConstruct(ConstructDocument document, Construct parent) {
		super(document, "let", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(mDocument, this, "bindings")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));		
		setPlaceholders(placeholders);
	}

	@Override
	public Construct deepCopy(Construct parent) {
		LetConstruct newCopy = new LetConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
}

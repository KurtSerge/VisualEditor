package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * Syntax: (let [bindings*] exprs*)
 * 
 * @author Christopher Lord
 */
public class LetConstruct extends ListConstruct {
	
	public LetConstruct(LetConstruct construct, Construct parent) {
		super(construct.getDocument(), "let", parent);
	}

	public LetConstruct(ConstructDocument document, Construct parent) {
		super(document, "let", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(mDocument, this, "bindings")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));		
		setPlaceholders(placeholders);
	}

	@Override
	public Construct deepCopy(Construct parent) {
		LetConstruct newCopy = new LetConstruct(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}
}

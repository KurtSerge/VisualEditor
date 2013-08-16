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
public class LoopConstruct extends ListConstruct {
	
	public LoopConstruct(LoopConstruct construct, Construct parent) {
		super(construct.getDocument(), "loop", parent);
	}

	public LoopConstruct(ConstructDocument document, Construct parent) {
		super(document, "loop", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(mDocument, this, "bindings")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		LoopConstruct newCopy = new LoopConstruct(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}
}

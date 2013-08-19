package clojure.constructs.meta;

import java.util.LinkedList;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * (rand n?)
 * @author Christopher Lord
 */
public class randConstruct extends MetaConstruct {
	
	public randConstruct(randConstruct construct, Construct parent) {
		super(construct.getDocument(), "rand", parent);
	}

	public randConstruct(ConstructDocument document, Construct parent) {
		super(document, "rand", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createOptionalPlaceholder("n", clojure.constructs.IntegerConstruct.class));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		randConstruct newCopy = new randConstruct(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}
}

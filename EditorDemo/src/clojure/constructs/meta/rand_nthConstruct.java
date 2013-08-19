package clojure.constructs.meta;

import java.util.LinkedList;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * (rand-nth collection)
 * @author Christopher Lord
 */
public class rand_nthConstruct extends MetaConstruct {
	
	public rand_nthConstruct(rand_nthConstruct construct, Construct parent) {
		super(construct.getDocument(), "rand-nth", parent);
	}

	public rand_nthConstruct(ConstructDocument document, Construct parent) {
		super(document, "rand-nth", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("collection", clojure.constructs.containers.CollectionConstruct.class));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		rand_nthConstruct newCopy = new rand_nthConstruct(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}
}

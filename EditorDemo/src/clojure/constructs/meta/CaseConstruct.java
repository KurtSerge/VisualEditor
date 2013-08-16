package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

/**
 * Syntax: (case test conditions*)
 * Where: 
 * 		- `conditions` is clojure.constructs.KeuValuePairConstruct
 * 
 * @author Christopher Lord
 */
public class CaseConstruct extends ListConstruct {

	public CaseConstruct(CaseConstruct construct, Construct parent) {
		super(construct.getDocument(), "case", parent);
	}	

	public CaseConstruct(ConstructDocument document, Construct parent) {
		super(document, "case", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("test"));
		placeholders.add(Placeholder.createVariadicPlaceholder("conditions", KeyValuePairConstruct.class));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		CaseConstruct newCopy = new CaseConstruct(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}	
}

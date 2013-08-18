package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.StringConstruct;
import clojure.constructs.containers.ListConstruct;
import construct.Construct;
import construct.Placeholder;
import construct.Construct.SelectionCause;
import editor.document.ConstructDocument;

/**
 * Syntax: (def symbol doc-string? init?)
 * 
 * @author Christopher Lord
 */
public class DefConstruct extends MetaConstruct {
	
	public DefConstruct(DefConstruct construct, Construct parent) {
		super(construct.getDocument(), construct.type, parent);
	}

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
		DefConstruct newCopy = new DefConstruct(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}
	
	@Override
	public Construct getConstructForSelection(SelectionCause type) { 
		if(type == SelectionCause.SelectedAfterInsert && 
				this.children.size() != 0) {
			// Select the child, not this
			return this.children.get(0);
		}
		
		return super.getConstructForSelection(type);
	}	
}

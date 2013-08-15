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
public class DefConstruct extends ListConstruct {

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
		DefConstruct newCopy = new DefConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	@Override
	public Construct getConstructForSelection(SelectionCause type) { 
		if(type == SelectionCause.SelectedAfterInsert) {
			// Select the child, not this
			return this.children.get(0);
		}
		
		return super.getConstructForSelection(type);
	}	
}

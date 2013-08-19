package clojure.constructs.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.ListConstruct;
import clojure.constructs.containers.MapConstruct;
import construct.Construct;
import construct.Placeholder;
import construct.Construct.SelectionCause;
import editor.document.ConstructDocument;

/**
 * Syntax: (defn name doc-string? attr-map? [params*] exprs*)
 * Where:
 * 		- `doc-string` is clojure.constructs.StringConstruct
 * 
 * @author Christopher Lord
 */
public class defmacro extends MetaConstruct {

	public defmacro(defmacro defn, Construct parent) {
		super(defn.getDocument(), "defmacro", parent);
	}	
	
	public defmacro(ConstructDocument document, Construct parent) {
		super(document, "defmacro", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("name", SymbolConstruct.class));
		placeholders.add(Placeholder.createOptionalPlaceholder("doc-string", StringConstruct.class));
		placeholders.add(Placeholder.createOptionalPlaceholder("attr-map", MapConstruct.class));
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(mDocument, this, "params")));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		defmacro newCopy = new defmacro(this, parent);
		super.deepCopyChildrenTo(newCopy);
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

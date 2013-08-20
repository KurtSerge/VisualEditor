package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.containers.MapConstruct;
import clojure.constructs.special.KeywordExpressionPairConstruct;
import construct.Construct;
import construct.Placeholder;
import construct.Construct.SelectionCause;
import editor.document.ConstructDocument;

/**
 * (defproject project-name version args*)
 * 
 * @author Christopher Lord
 */
public class defproject extends MetaConstruct {

	public defproject(defproject defn, Construct parent) {
		super(defn.getDocument(), "defproject", parent);
	}	
	
	public defproject(ConstructDocument document, Construct parent) {
		super(document, "defproject", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPlaceholder("project-name", SymbolConstruct.class));
		placeholders.add(Placeholder.createPlaceholder("version", StringConstruct.class));
		placeholders.add(Placeholder.createVariadicPlaceholder("args", KeywordExpressionPairConstruct.class));
		setPlaceholders(placeholders);
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		defproject newCopy = new defproject(this, parent);
		super.deepCopyChildrenTo(newCopy);
		return newCopy;
	}
}

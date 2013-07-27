package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.ClojureConstruct.Placeholder;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import editor.Construct;

public class LetConstruct extends ClojureConstruct {

	public LetConstruct(Construct parent) {
		super("let", parent);
		

		
		LinkedList<ClojureConstruct.Placeholder> placeholders = new LinkedList<ClojureConstruct.Placeholder>();
		
		placeholders.add(Placeholder.createPermanentPlaceholder(new SymbolConstruct(this, "let", false)));

		
		// Setup the non-optional "parameters" placeholder
		LinkedList<ClojureConstruct.Placeholder> bindingsPlaceholders = new LinkedList<ClojureConstruct.Placeholder>();
		bindingsPlaceholders.add(ClojureConstruct.Placeholder.createVariadicPlaceholder("bindings"));
		VectorConstruct bindingsConstruct = new VectorConstruct(this, null);
		bindingsConstruct.setPlaceholders(bindingsPlaceholders);

		placeholders.add(Placeholder.createPermanentPlaceholder(bindingsConstruct));
		placeholders.add(Placeholder.createVariadicPlaceholder("exprs"));		
		
		setPlaceholders(placeholders);
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	public boolean canDeleteChild(int index, Construct child) {  
		if(index == 0) { 
			return false;
		}
		
		return super.canDeleteChild(index, child);
	}

	@Override
	public String screen_text() {
		return "($(node) $(node) $(node))";
	}
	
	public boolean canInsertChildren() { 
		return false;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		LetConstruct newCopy = new LetConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

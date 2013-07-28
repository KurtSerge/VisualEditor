package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.special.VariadicVectorConstruct;
import editor.Construct;

public class LetConstruct extends ClojureConstruct {

	public LetConstruct(Construct parent) {
		super("let", parent);

		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new SymbolConstruct(this, "let", false)));
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(this, "bindings")));
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

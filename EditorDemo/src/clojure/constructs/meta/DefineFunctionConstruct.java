package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.ClojureConstruct.Placeholder;
import clojure.constructs.ListConstruct;
import clojure.constructs.StringConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.VectorConstruct;
import clojure.constructs.special.VariadicVectorConstruct;
import editor.Construct;
import clojure.ClojureConstruct.*;

public class DefineFunctionConstruct extends ClojureConstruct {

	public DefineFunctionConstruct(Construct parent) {
		super("DefineFunction", parent);

		
		LinkedList<ClojureConstruct.Placeholder> placeholders = new LinkedList<ClojureConstruct.Placeholder>();
		
		placeholders.add(Placeholder.createPermanentPlaceholder(new SymbolConstruct(this, "defn", false)));
		placeholders.add(Placeholder.createPlaceholder("name"));
		placeholders.add(Placeholder.createOptionalPlaceholder("doc-string", StringConstruct.class));
		placeholders.add(Placeholder.createOptionalPlaceholder("attr-map"));
		placeholders.add(Placeholder.createPermanentPlaceholder(new VariadicVectorConstruct(this, "params")));
		placeholders.add(ClojureConstruct.Placeholder.createVariadicPlaceholder("exprs"));
		
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
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for(int i = 0; i < this.children.size(); i++) { 
			builder.append("$(node)");
			if(i != this.children.size() - 1) 
				builder.append(" ");
		}
		
		builder.append(")");
		return builder.toString();
	}
	
	public boolean canInsertChildren() { 
		return false;
	}	
	
	@Override
	public Construct deepCopy(Construct parent) {
		DefineFunctionConstruct newCopy = new DefineFunctionConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

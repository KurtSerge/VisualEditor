package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import clojure.constructs.special.PlaceholderConstruct;
import editor.Construct;

public class IfThenElseConstruct extends ListConstruct {

	public IfThenElseConstruct(Construct parent) {
		super(parent, null);
		
		this.children.add(new SymbolConstruct(this, "if", false));
		
		LinkedList<String> placeholders = new LinkedList<String>();
		placeholders.add("condition");
		placeholders.add("then");
		placeholders.add("else");
		
		setPlaceholders(placeholders, 1);
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	public boolean canDeleteChild(Construct child) {  
		int index = this.children.indexOf(child);
		if(index == 0) { 
			return false;
		}
		
		return super.canDeleteChild(child);
	}
	
	public boolean canInsertChildren() { 
		return false;
	}
}

package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.SymbolConstruct;
import editor.Construct;

public class FunctionConstruct extends ClojureConstruct {

	public FunctionConstruct(Construct parent) {
		super("fn", parent);
		
		this.children.add(new SymbolConstruct(this, "fn", false));
		
//		LinkedList<String> placeholders = new LinkedList<String>();
//		placeholders.add("arguments");
//		placeholders.add("context");
//		
//		setPlaceholders(placeholders, 1);
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
		FunctionConstruct newCopy = new FunctionConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

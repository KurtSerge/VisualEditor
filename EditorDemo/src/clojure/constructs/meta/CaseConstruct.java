package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.SymbolConstruct;
import editor.Construct;

public class CaseConstruct extends ClojureConstruct {
	

	public CaseConstruct(Construct parent) {
		super("case", parent);
		
		this.children.add(new SymbolConstruct(this, "case", false));
		
//		LinkedList<String> placeholders = new LinkedList<String>();
//		placeholders.add("e");
//		placeholders.add("clause");
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
		CaseConstruct newCopy = new CaseConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}	

}

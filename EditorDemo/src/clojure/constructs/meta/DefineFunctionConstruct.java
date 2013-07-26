package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.ListConstruct;
import clojure.constructs.SymbolConstruct;
import editor.Construct;

public class DefineFunctionConstruct extends ClojureConstruct {

	public DefineFunctionConstruct(Construct parent) {
		super("DefineFunction", parent);
		
		this.children.add(new SymbolConstruct(this, "defn", false));
		
		LinkedList<String> placeholders = new LinkedList<String>();
		placeholders.add("name");
		placeholders.add("doc-string?");
		placeholders.add("attr-map?");
		placeholders.add("params");
		placeholders.add("body");
		
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

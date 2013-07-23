package clojure.constructs;

import clojure.ClojureConstruct;
import editor.Construct;

public class MapConstruct extends ClojureConstruct {
	
	public MapConstruct(Construct parent, String literal) { 
		super("map", parent);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(" ");
			}
			
			builder.append("$(node)");
			
			if(i % 2 == 1 && i != children.size() - 1)
				builder.append(",");
		}

		builder.append("}");
		return builder.toString();
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

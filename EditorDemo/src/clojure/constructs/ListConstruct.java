package clojure.constructs;

import editor.Construct;

public class ListConstruct extends Construct {

	public ListConstruct(Construct parent, String literal) {
		super("list", parent);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(" ");
			}
			builder.append("$(node)");
		}

		builder.append(")");
		return builder.toString();
	}

	@Override
	public boolean validate() {
		return false;
	}

}

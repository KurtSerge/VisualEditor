package clojure.constructs;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.placeholder.Placeholder;
import editor.Construct;

public class KeywordExpressionPairConstruct extends ClojureConstruct {

	public KeywordExpressionPairConstruct(Construct parent, String literal) {
		super("keywordexpressionpair", parent);
		
		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new KeywordConstruct(this, "key")));
		placeholders.add(Placeholder.createPlaceholder("expr"));		
		setPlaceholders(placeholders);
	}

	@Override
	public String screen_text() {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < this.children.size(); i++) { 
			stringBuilder.append("$(node)");
			if(i != this.children.size() - 1) 
				stringBuilder.append(" ");
		}
		
		return super.layout(stringBuilder.toString());
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		KeywordExpressionPairConstruct newCopy = new KeywordExpressionPairConstruct(parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

package clojure.constructs.meta;

import java.util.LinkedList;

import clojure.ClojureConstruct;
import clojure.constructs.KeywordConstruct;
import construct.Construct;
import construct.Placeholder;
import editor.document.ConstructDocument;

public class KeywordExpressionPairConstruct extends ClojureConstruct {

	private KeywordExpressionPairConstruct(KeywordExpressionPairConstruct construct, Construct parent) { 
		super(construct.getDocument(), "keywordexpressionpair", parent);
	}
	
	public KeywordExpressionPairConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "keywordexpressionpair", parent);
		
		LinkedList<Placeholder> placeholders = new LinkedList<Placeholder>();
		placeholders.add(Placeholder.createPermanentPlaceholder(new KeywordConstruct(document, this, "key")));
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
		KeywordExpressionPairConstruct newCopy = new KeywordExpressionPairConstruct(this, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

package clojure.constructs;

import construct.Construct;
import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

public class CharacterConstruct extends ClojureConstruct {

	public CharacterConstruct(ConstructDocument document, Construct parent, String literal) { 
		super(document, "character", parent);
		this.literal = literal;
	}
	
	@Override
	public String screen_text() {
		return null;
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		CharacterConstruct newCopy = new CharacterConstruct(mDocument, parent, this.literal);
		super.deepCopy(newCopy);
		return newCopy;
	}

}

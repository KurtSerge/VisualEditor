package json;

import editor.Construct;
import editor.document.ConstructDocument;

public class BooleanConstruct extends Construct{

	protected BooleanConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "boolean", parent);
		this.literal = literal;
	}

	@Override
	public String screen_text()
	{
		return null;
	}

	@Override
	public boolean validate() {
		if(literal == "true" || literal == "false")
			return true;
		
		return false;
	}
	
	@Override
	protected boolean canAddChild(int index, Construct child) {
		return false;
	}
	
	public Construct deepCopy(Construct parent) {
		BooleanConstruct newCopy = new BooleanConstruct(mDocument, parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
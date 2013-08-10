package json;

import editor.Construct;
import editor.document.ConstructDocument;

public class NullConstruct extends Construct{

	protected NullConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "null", parent);
		this.literal = literal;
	}

	@Override
	public String screen_text()
	{
		return "null";
	}

	@Override
	public boolean validate() {
		if(literal == "null")
			return true;
		
		return false;
	}
	
	@Override
	protected boolean canAddChild(int index, Construct child) {
		return false;
	}
	
	
	public Construct deepCopy(Construct parent) {
		NullConstruct newCopy = new NullConstruct(mDocument, parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
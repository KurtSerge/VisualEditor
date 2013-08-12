package json;

import construct.Construct;
import editor.document.ConstructDocument;

public class IntegerConstruct extends Construct{
	public IntegerConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "integer", parent);
		this.literal = literal;
	}

	@Override
	public String screen_text()
	{
		return null;
	}

	@Override
	public boolean validate() {
		try {
			// FIXME: int test = Integer.parseInt(this.literal);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	protected boolean canAddChild(int index, Construct child) {
		return false;
	}
	
	
	public Construct deepCopy(Construct parent) {
		IntegerConstruct newCopy = new IntegerConstruct(mDocument, parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
package json;

import editor.Construct;
import editor.document.ConstructDocument;

public class FloatConstruct extends Construct{

	protected FloatConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "float", parent);
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
			// FIXME: double test = Double.parseDouble(this.literal);
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
		FloatConstruct newCopy = new FloatConstruct(mDocument, parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
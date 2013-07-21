package json;

import editor.Construct;

public class FloatConstruct extends Construct{

	protected FloatConstruct(Construct parent, String literal) {
		super("float", parent);
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
	public boolean validateAddChild(int index, Construct child) {
		return false;
	}
	
	
	public Construct deepCopy(Construct parent) {
		FloatConstruct newCopy = new FloatConstruct(parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
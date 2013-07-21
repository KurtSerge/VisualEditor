package json;

import editor.Construct;

public class IntegerConstruct extends Construct{
	public IntegerConstruct(Construct parent, String literal) {
		super("integer", parent);
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
	public boolean validateAddChild(int index, Construct child) {
		return false;
	}
	
	
	public Construct deepCopy(Construct parent) {
		IntegerConstruct newCopy = new IntegerConstruct(parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
package json;

import editor.Construct;

public class BooleanConstruct extends Construct{

	protected BooleanConstruct(Construct parent, String literal) {
		super("boolean", parent);
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
		BooleanConstruct newCopy = new BooleanConstruct(parent, literal);
		super.deepCopy(newCopy);
		return newCopy;
	}
}
package json;

import editor.Construct;

public class NullConstruct extends Construct{

	protected NullConstruct(Construct parent, String literal) {
		super("null", parent);
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
	public boolean validateAddChild(int index, Construct child) {
		return false;
	}
}
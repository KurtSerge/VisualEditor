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
			double test = Double.parseDouble(this.literal);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
}
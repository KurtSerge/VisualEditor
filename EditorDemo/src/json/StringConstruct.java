package json;

import editor.Construct;

public class StringConstruct extends Construct {

	public StringConstruct(Construct parent)
	{
		super("string", parent);
	}

	@Override
	public String screen_text()
	{
		assert(1 == children.size());
		assert(StringLiteralConstruct.class.equals(children.get(0).getClass()));
		
		return "\"$(node)\"";
	}

}

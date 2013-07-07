package lisp;

import editor.Construct;

public class StringLiteralConstruct extends Construct
{
	public StringLiteralConstruct(Construct parent, String literal)
	{
		super("string_literal", parent);
		this.literal = literal;
	}

	@Override
	public String screen_text()
	{
		return null;
	}

}

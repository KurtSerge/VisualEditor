package lisp;

import editor.Construct;
import editor.document.ConstructDocument;

public class StringLiteralConstruct extends Construct
{
	public StringLiteralConstruct(ConstructDocument document, Construct parent, String literal)
	{
		super(document, "string_literal", parent);
		this.literal = literal;
	}

	@Override
	public String screen_text()
	{
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

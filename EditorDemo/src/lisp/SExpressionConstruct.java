package lisp;

import construct.Construct;
import editor.document.ConstructDocument;

public class SExpressionConstruct extends Construct {

	public SExpressionConstruct(ConstructDocument document, Construct parent)
	{
		super(document, "sexpression", parent);
	}
	
	@Override
	public String screen_text()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("$(node)");

		for(int index = 1;index<children.size();++index)
		{
			builder.append("\n  $(node)");
		}
		
		
		return builder.toString();
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

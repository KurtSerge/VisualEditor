package lisp;

import editor.Construct;

public class SExpressionConstruct extends Construct {

	public SExpressionConstruct(Construct parent)
	{
		super("sexpression", parent);
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

}

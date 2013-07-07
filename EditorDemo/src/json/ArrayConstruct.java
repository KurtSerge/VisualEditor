package json;

import editor.Construct;

public class ArrayConstruct extends Construct {

	public ArrayConstruct(Construct parent)
	{
		super("array", parent);
	}

	@Override
	public String screen_text()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("[");
		
		for(int index = 0;index<children.size();++index)
		{
			builder.append("$(node)");

			if(index != (children.size() - 1))
				builder.append(", ");
		}
		
		builder.append("]");
		
		return builder.toString();
	}

}

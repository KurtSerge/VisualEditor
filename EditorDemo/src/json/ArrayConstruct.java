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

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean validateAddChild(int index, Construct child) {
		// TODO: can I add an array to an array? Look into all these values
		if(child.getClass() == StringConstruct.class ||
		   child.getClass() == BooleanConstruct.class ||
		   child.getClass() == FloatConstruct.class ||
		   child.getClass() == IntegerConstruct.class ||
		   child.getClass() == NullConstruct.class) {
			return true;
		}
		
		return false;
	}


}

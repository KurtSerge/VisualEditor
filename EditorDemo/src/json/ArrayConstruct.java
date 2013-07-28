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
		
		// Change style of array contains objects
		String nodeStyle = "$(node)";
		String endStyle = "]";
		for(Construct child : children) {
			if(child.type == "object") {
				nodeStyle = "\n $(node)";
				endStyle = "\n]";
				break;
			}
		}
		
		for(int index = 0;index<children.size();++index)
		{
			builder.append(nodeStyle);

			if(index != (children.size() - 1))
				builder.append(", ");
		}
		
		builder.append(endStyle);
		
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
		   child.getClass() == NullConstruct.class ||
		   child.getClass() == ObjectConstruct.class) {
			return true;
		}
		
		return false;
	}

	public Construct deepCopy(Construct parent) {
		ArrayConstruct newCopy = new ArrayConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

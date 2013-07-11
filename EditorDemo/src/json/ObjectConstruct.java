package json;

import editor.Construct;

public class ObjectConstruct extends Construct {

	public ObjectConstruct(Construct parent)
	{
		super("object", parent);
	}

	private static String repeat_string(String s, int times)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i=0;i<times;++i)
			builder.append(s);
		
		return builder.toString();
	}
	
	@Override
	public String screen_text()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("{\n");
		
		for(int index = 0;index<children.size();index++)
		{	
			
			Construct c = children.get(index);
			// "string?"
			if(c.type.equals("object") || (c.type.equals("key_value_pair")))
				builder.append(" $(node)");
			else
				assert(1==0);
			
			if(index < children.size() - 1)
				builder.append(", ");
			
			builder.append("\n");
		}
		
		builder.append("}");
		
		return builder.toString();
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}

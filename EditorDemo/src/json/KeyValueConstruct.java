package json;

import editor.Construct;
	

public class KeyValueConstruct extends Construct {



	public KeyValueConstruct(Construct parent)
	{
		super("key_value_pair", parent);
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
		assert(children.size() == 2);
		
		StringBuilder builder = new StringBuilder();
		// key
		builder.append("$(node):");
		// new line for object values
		Construct c = children.get(1);
		if(c.type.toString() == "object")
			builder.append("\n");
		// value
		builder.append(" $(node)");

		return builder.toString();
	}

	
}

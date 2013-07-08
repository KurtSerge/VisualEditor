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
		return " $(node): $(node)";
	}

	
}

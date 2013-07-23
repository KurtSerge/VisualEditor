package json;

import editor.Construct;
import editor.EmptyConstruct;
	

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

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean validateAddChild(int index, Construct child) {
		if(index == 0) {
			// Key
			if(child.getClass() == StringConstruct.class)
				return true;
			else
				return false;
		}
		else if(index == 1) {
			// Value
			if(child.getClass() == ArrayConstruct.class ||
			   child.getClass() == BooleanConstruct.class ||
			   child.getClass() == FloatConstruct.class ||
			   child.getClass() == IntegerConstruct.class ||
			   child.getClass() == NullConstruct.class ||
			   child.getClass() == StringConstruct.class ||
			   child.getClass() == ObjectConstruct.class ||
			   child.getClass() == EmptyConstruct.class) {
				return true;
			}
			else
				return false;
		}
		else {
			// not valid
			return false;
		}
	}

	@Override
	public boolean canDeleteChild(int index, Construct child) {
		if(index != 0)  {
			return true; // FIXME: necessary? some more general way to force this behaviour?
		}
		
		return false;
	}

	
	
	// Override this for special rules, example: KV-Pair must have at least 2 children
	public void handleDeleteChild() {
		if(children.size() == 1)  {
			Construct newCon = new EmptyConstruct(this);
			
			this.addChild(1, newCon);
			JSONController.editors_from_constructs(newCon);
		}
	}


	
	
	public Construct deepCopy(Construct parent) {
		KeyValueConstruct newCopy = new KeyValueConstruct(parent);
		super.deepCopy(newCopy);

		return newCopy;
	}
}

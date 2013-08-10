package json;

import editor.Construct;
import editor.document.ConstructDocument;

public class ObjectConstruct extends Construct {

	public ObjectConstruct(ConstructDocument document, Construct parent)
	{
		super(document, "object", parent);
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
		// object can be root or key's value
		if(parent.getClass() != KeyValueConstruct.class && parent != null)
			return false;
		
		return true;
	}
	
	@Override
	protected boolean canAddChild(int index, Construct child) {
		if(	child.getClass() == KeyValueConstruct.class/* ||
		    child.getClass() == ObjectConstruct.class*/) {// TODO: temporary, should NOT be able to insert object! Remove this || clause
			return true;
		}
		
		return false;
	}
	


	public Construct deepCopy(Construct parent) {
		ObjectConstruct newCopy = new ObjectConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

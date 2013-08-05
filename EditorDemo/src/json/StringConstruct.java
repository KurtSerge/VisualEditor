package json;

import editor.Construct;

public class StringConstruct extends Construct {

	public StringConstruct(Construct parent)
	{
		super("string", parent);
	}

	@Override
	public String screen_text()
	{
		assert(1 == children.size());
		assert(StringLiteralConstruct.class.equals(children.get(0).getClass()));
		
		return "\"$(node)\"";
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean canAddChild(int index, Construct child) {
		if(child.getClass() == StringLiteralConstruct.class)
			return true;
		
		return false;
	}
	
	
	public Construct deepCopy(Construct parent) {
		StringConstruct newCopy = new StringConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}

}

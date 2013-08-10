package json;

import editor.Construct;
import editor.document.ConstructDocument;

public class StringConstruct extends Construct {

	public StringConstruct(ConstructDocument document, Construct parent)
	{
		super(document, "string", parent);
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
		StringConstruct newCopy = new StringConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}

}

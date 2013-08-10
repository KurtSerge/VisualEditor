package editor;

import editor.Construct;
import editor.document.ConstructDocument;

public class EmptyConstruct extends Construct {
	
	public EmptyConstruct(ConstructDocument document, Construct parent)
	{
		super(document, "empty", parent);
	}

	@Override
	public String screen_text()
	{
		return "\u0325 \u0325 \u0325 ";
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Construct deepCopy(Construct parent) {
		EmptyConstruct newCopy = new EmptyConstruct(mDocument, parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}




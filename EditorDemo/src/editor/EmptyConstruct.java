package editor;

import editor.Construct;

public class EmptyConstruct extends Construct {
	
	public EmptyConstruct(Construct parent)
	{
		super("empty", parent);
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
		EmptyConstruct newCopy = new EmptyConstruct(parent);
		super.deepCopy(newCopy);
		return newCopy;
	}
}




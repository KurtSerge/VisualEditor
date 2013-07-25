package clojure;

import editor.Construct;

public abstract class ClojureConstruct extends Construct
{
	protected ClojureConstruct(String type, Construct parent) {
		super(type, parent);
	}

	public boolean canInsertChildren() { 
		return false;
	}
}

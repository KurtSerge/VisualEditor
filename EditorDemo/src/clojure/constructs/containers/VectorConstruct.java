package clojure.constructs.containers;

import clojure.ClojureConstruct;
import clojure.ClojureConstruct.ConstructType;
import construct.Construct;
import editor.document.ConstructDocument;

public class VectorConstruct extends CollectionConstruct
{
	public VectorConstruct(ConstructDocument document, Construct parent, String literal) {
		super(document, "vector", parent);
	}
	
	public static String PREFIX = "[";
	public static String AFFIX = "]";

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append(PREFIX);
		
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append(BREAKING_SPACE);
			}
			
			if(this.getIsMultilined() && i != 0) { 
				for(int j = 0; j < PREFIX.length(); j++) { 
					builder.append(" ");
				}
			}			
			
			builder.append("$(node)");
		}

		builder.append(AFFIX);
		return super.layout(builder.toString());
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	/**
	 * This construct is a 'UserCollection' construct;
	 * freely allow all actions that manpulate children.
	 */
	@Override
	public ConstructType getConstructType() { 
		return ConstructType.UserCollection;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		VectorConstruct newCopy = new VectorConstruct(mDocument, parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
}

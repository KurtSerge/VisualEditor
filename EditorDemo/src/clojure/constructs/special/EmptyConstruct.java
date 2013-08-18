package clojure.constructs.special;

import java.util.ArrayList;
import java.util.Collection;

import clojure.ClojureConstruct;
import construct.Construct;
import editor.document.ConstructDocument;

/**
 * A special construct returned as the parent to all
 * Clojure documents that allows multiple root-level 
 * clojure forms.
 * 
 * @author chrislord
 */
public class EmptyConstruct extends ClojureConstruct {
	public EmptyConstruct(ConstructDocument document) {
		super(document, "clojure", null);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < children.size(); ++i) {
			if(i != 0 && i < children.size()) { 
				builder.append("\n\n");
			}
			builder.append("$(node)");
		}
		return builder.toString();
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	@Override
	public boolean isConstructContainer() { 
		return true;
	}

	@Override
	public Construct deepCopy(Construct parent) {
		EmptyConstruct newCopy = new EmptyConstruct(mDocument);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	@Override
	public Collection<Class<?>> getAutoCompleteClasses() { 
		ArrayList<Class<?>> classRestriction = new ArrayList<Class<?>>();
		classRestriction.add(clojure.constructs.containers.ListConstruct.class);
		return classRestriction;
	}
}

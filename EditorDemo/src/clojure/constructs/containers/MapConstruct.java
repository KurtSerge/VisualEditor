package clojure.constructs.containers;

import construct.Construct;
import construct.Placeholder;
import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

public class MapConstruct extends ClojureConstruct {
	
	private static final String PREFIX = "{";
	private static final String AFFIX = "}";
	
	public MapConstruct(ConstructDocument document, Construct parent, String literal) { 
		super(document, "map", parent);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append(PREFIX);
		
		for(int i = 0; i < children.size(); ++i) {
			
			// Add spacing for opening prefix
			if(i != 0 && this.getIsMultilined()) { 
				for(int j = 0; j < PREFIX.length(); j++) 
					builder.append(" ");
			}
			
			// Append the next node
			builder.append("$(node)");
			boolean isFinalNode = i == children.size() - 1;
			if(!isFinalNode) {
				// There is another node, and this node
				// ended a pair of key-value nodes
				builder.append("," + BREAKING_SPACE);
			}
		}

		builder.append(AFFIX);
		return super.layout(builder.toString());
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
		MapConstruct newCopy = new MapConstruct(mDocument, parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	@Override
	public void onChildAdded(int index, Construct child) {
		if(child.getClass().equals(clojure.constructs.KeywordConstruct.class)) { 
			addChild(index+1, Placeholder.createPlaceholder("value").createConstruct(mDocument, this));
		}
	}
	
	@Override
	protected void onChildDeleted(int index, Construct deleted) {
		super.onChildDeleted(index, deleted);
		
		// Delete the value next to the keyword construct
		if(deleted.getClass().equals(clojure.constructs.KeywordConstruct.class)) {
			this.children.get(index).delete(false, false);
		}
		
		// Restore placeholder for deleted children next to KeywordConstruct's
		if(index != 0 && this.children.size() >= 1) { 
			if(this.children.get(index-1).getClass().equals(clojure.constructs.KeywordConstruct.class)) { 
				addChild(index, Placeholder.createPlaceholder("value").createConstruct(mDocument, this));
			}
		}
	}	
	
	// Override this to set special conditions for when a child can be deleted
	@Override
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) {
		if(child.getClass().equals(construct.PlaceholderConstruct.class)) { 
			return false;
		}
		
		return super.canDeleteChild(index, child, isUser);
	}
	
	@Override
	public boolean canAddChild(int index, Construct child) {
		return child.getClass().equals(clojure.constructs.meta.KeywordExpressionPairConstruct.class);
	}
	
	protected boolean canReplaceChild(int index, Construct oldConstruct, Construct newConstruct) {
		if(index % 2 == 0)
			return false;
			
		return true;
	}
}

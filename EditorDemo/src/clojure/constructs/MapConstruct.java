package clojure.constructs;

import clojure.ClojureConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.placeholder.PlaceholderConstruct;
import editor.Construct;

public class MapConstruct extends ClojureConstruct {
	
	private static final String PREFIX = "{";
	private static final String AFFIX = "}";
	
	public MapConstruct(Construct parent, String literal) { 
		super("map", parent);
	}

	@Override
	public String screen_text() {
		StringBuilder builder = new StringBuilder();
		builder.append(PREFIX);
		
		for(int i = 0; i < children.size(); ++i) {
			
			// Add spacing for opening prefix
			if(i % 2 == 0 && i != 0 && this.getIsMultilined()) { 
				for(int j = 0; j < PREFIX.length(); j++) 
					builder.append(" ");
			}
			
			// Append the next node
			builder.append("$(node)");
			boolean isFinalNode = i == children.size() - 1;
			if(i % 2 == 1 && !isFinalNode) {
				// There is another node, and this node
				// ended a pair of key-value nodes
				builder.append("," + BREAKING_SPACE);
			} else if(!isFinalNode) {
				// This node starts a key-value pair
				builder.append(" ");
				
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
	public boolean canInsertChildren() { 
		return true;
	}	
	
	@Override
	public Construct deepCopy(Construct parent) {
		MapConstruct newCopy = new MapConstruct(parent, null);
		super.deepCopy(newCopy);
		return newCopy;
	}
	
	@Override
	public void onChildAdded(int index, Construct child) {
		if(child.getClass().equals(clojure.constructs.KeywordConstruct.class)) { 
			addChild(index+1, Placeholder.createPlaceholder("value").createConstruct(this));
		}
	}
	
	@Override
	protected void onChildDeleted(int index, Construct deleted) {
		super.onChildDeleted(index, deleted);
		
		// Delete the value next to the keyword construct
		if(deleted.getClass().equals(clojure.constructs.KeywordConstruct.class)) {
			deleteChild(this.children.get(index));
		}
		
		// Restore placeholder for deleted children next to KeywordConstruct's
		if(index != 0 && this.children.size() >= 1) { 
			if(this.children.get(index-1).getClass().equals(clojure.constructs.KeywordConstruct.class)) { 
				addChild(index, Placeholder.createPlaceholder("value").createConstruct(this));
			}
		}
	}	
	
	// Override this to set special conditions for when a child can be deleted
	public boolean canDeleteChild(int index, Construct child) {
		if(child.getClass().equals(clojure.constructs.placeholder.PlaceholderConstruct.class)) { 
			return false;
		}
		
		return super.canDeleteChild(index, child);
	}
	
	public boolean validateAddChild(int index, Construct child) {
		if(index % 2 == 0)
			return child.getClass().equals(clojure.constructs.KeywordConstruct.class);

		return true;
	}
}

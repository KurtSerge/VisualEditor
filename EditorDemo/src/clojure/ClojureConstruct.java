package clojure;

import java.util.List;

import clojure.constructs.meta.IfThenElseConstruct;
import clojure.constructs.special.PlaceholderConstruct;

import editor.Construct;

public abstract class ClojureConstruct extends Construct
{
	public ClojureConstruct(String type, Construct parent) {
		super(type, parent);
		
		mPlaceholders = null;
	}
	
	protected void setPlaceholders(List<String> placeholders, int offset) {
		assert(mPlaceholders == null);	// TODO: Updating placeholders
		
		mPlaceholders = placeholders;
		mPlaceholdersOffset = offset;
		
		// Add placeholders as children to this node
		for(int i = 0; i < mPlaceholders.size(); i++) { 
			String placeholderText = mPlaceholders.get(i);
			addChild(i + mPlaceholdersOffset, new PlaceholderConstruct(this, placeholderText));
		}
	}

	public boolean canInsertChildren() { 
		return false;
	}
	
	public boolean canDeleteChild(int index, Construct child) { 
		if(child.getClass().equals(clojure.constructs.special.PlaceholderConstruct.class)) { 
			// Placeholder constructs can only be replaced, not deleted
			return false;
		}
		
		return true;
	}
	

//	public boolean deleteChild(Construct child) {
//		if(canDeleteChild(child)) {
//			// Check to see if we are managing placeholders at
//			// this location, if so then do not delete: replace.
//			int childIndex = this.children.indexOf(child);
//			if(mPlaceholders != null && 
//					childIndex >= mPlaceholdersOffset && 
//					childIndex <  mPlaceholdersOffset + mPlaceholders.size())
//			{
//				// Swap the child
//				this.replaceChild(child, new PlaceholderConstruct(this, mPlaceholders.get(childIndex-mPlaceholdersOffset)));
//				return true;
//			}
//	
//			this.children.remove(childIndex);
//			return true;
//		}
//		
//		return false;
//	}
	
	private int mPlaceholdersOffset;
	private List<String> mPlaceholders;
	
	
	@Override
	public Construct deepCopy(Construct parent) {
		super.deepCopy(parent);
		
		if(parent.getClass().isInstance(ClojureConstruct.class)) {  
			((ClojureConstruct) parent).setPlaceholders(mPlaceholders, mPlaceholdersOffset);
		}
		
		return null;
	}
}

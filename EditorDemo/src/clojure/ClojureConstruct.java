package clojure;

import java.util.LinkedList;
import java.util.List;

import clojure.constructs.meta.IfThenElseConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.placeholder.PlaceholderConstruct;

import editor.Application;
import editor.Construct;

/**
 * TODO: setPlaceholders() replacing previous setPlaceholders()
 * TODO: validate() should ensure all required placeholders are replaced
 * 
 * @author Christopher Lord
 */
public abstract class ClojureConstruct extends Construct
{
	public ClojureConstruct(String type, Construct parent) {
		super(type, parent);
		
		mPlaceholders = null;
	}
	
	@Override
	public Construct deepCopy(Construct parent) {
		super.deepCopy(parent);
		
		return null;
	}
	
	/**
	 * If placeholders are set, prevent deletion of permanent & 
	 * placeholder constructs.
	 */
	public boolean canDeleteChild(int index, Construct child) { 
		if(mPlaceholders == null) {
			return super.canDeleteChild(index, child);
		}
		
		Placeholder descriptor = getPlaceholderForConstruct(child);
		if(descriptor == null) {
			// No descriptor with mPlaceholders set is an error,
			// no deletion when it comes down to the error.
			return false; 
		}
		
		if(descriptor.isPermanent()) { 
			Application.showError(child, "Cannot delete this construct");
			return false;
		}
		
		
		if(child.getClass().equals(PlaceholderConstruct.class))
		{ 
			Application.showError(child, "Cannot delete this construct");
			return false;
		}

		return true;
	}

	/**
	 * By default, no ClojureConstruct can have insert children.
	 * This needs to be overriden by container constructs such
	 * as Vector, Map and List.
	 * 
	 * @return If a child can be inserted or not.
	 */
	public boolean canInsertChildren() { 
		return false;
	}

	/**
	 * If placeholders are set, post-deletion of a child should
	 * restore the placeholder object.
	 */
	@Override
	public void handleDeleteChild(int index, Construct deleted) {
		super.handleDeleteChild(index, deleted);
		
		if(getPlaceholders() != null) {
			Placeholder descriptor = getPlaceholderForIndex(index);
			if(!descriptor.isVariadic()) { 
				PlaceholderConstruct construct = new PlaceholderConstruct(this, descriptor);
				this.addChild(index, construct);
			}
		}		
	}
	
	/**
	 * ClojureConstruct's can have Placeholders, in which case we need
	 * to manage what can replace a Placeholder according to the rules 
	 * laid out by it. This includes variadic arguments, class type
	 * restrictions and permanent constructs.
	 */
	@Override
	public boolean replaceChild(Construct replaceMe, Construct newCon)
	{
		if(mPlaceholders != null) { 
			int indexOfOldConstruct = this.children.indexOf(replaceMe);
			Placeholder descriptor = null;
			
			// If the index of the old construct is bigger than our placeholders
			// size, then we are likely dealing with a variadic placeholder at the
			// end of the placeholders list. Verify this, and assume this case.
			if(indexOfOldConstruct >= this.mPlaceholders.size()) { 
				// Fetch the last descriptor and verify that it is variadic
				descriptor = mPlaceholders.get(mPlaceholders.size() - 1);
				if(!descriptor.isVariadic()) {
					System.err.println("<ClojureConstruct> Index of replaceChild exceeds mPlaceholders.size() but last placeholder is NOT variadic.");
					return false;
				}
			} else { 
				descriptor = this.mPlaceholders.get(indexOfOldConstruct);
			}
			
			if(!descriptor.isAllowed(newCon.getClass())) { 
				Application.showError(replaceMe, descriptor.getHint() + " requires instance of " + descriptor.getClassRestriction().getSimpleName());
				return false;
			}

			if(descriptor.isPermanent()) {
				Application.showError(replaceMe, "Cannot delete this construct");
				return false;
			}
			
			if(descriptor.isVariadic() && 
					replaceMe.getClass().equals(PlaceholderConstruct.class))
			{ 
				this.addChild(indexOfOldConstruct, newCon);
				return true;
			}

		}
				
		return super.replaceChild(replaceMe, newCon);
	}
	
	public void setPlaceholders(List<Placeholder> placeholders) {
		assert(mPlaceholders == null);
		 
		mPlaceholders = placeholders;
		
		// Add placeholders as children to this node
		for(int i = 0; i < mPlaceholders.size(); i++) { 
			Placeholder placeholder = mPlaceholders.get(i);
			
//			if(placeholder.isOptional()) 
//				continue;
//			
			if(placeholder.isPermanent()) {
				if(mPlaceholdersAddedOnce == false)
					addChild(children.size(), placeholder.getPermanentConstruct());
			} else { 
				PlaceholderConstruct construct = new PlaceholderConstruct(this, placeholder);
				addChild(children.size(), construct);	
			}
		}
		
		mPlaceholdersAdded = true;
		mPlaceholdersAddedOnce = true;
	}
	
	protected Placeholder getPlaceholderForIndex(int indexOfObject) { 
		Placeholder descriptor = null;
		if(indexOfObject >= mPlaceholders.size()) { 
			descriptor = mPlaceholders.get(mPlaceholders.size() - 1);
			if(!descriptor.isVariadic()) {
				return null;
			}
		} else { 
			descriptor = mPlaceholders.get(indexOfObject);
		}
		
		return descriptor;
	}
	
	protected Placeholder getPlaceholderForConstruct(Construct object) {
		int indexOfObject = this.children.indexOf(object);
		return getPlaceholderForIndex(indexOfObject);
	}
	
	protected List<Placeholder> getPlaceholders() { 
		return mPlaceholders;
	}
	
	@Override
	public boolean validate() {
		return false;
	}

	private List<Placeholder> mPlaceholders;
	private boolean mPlaceholdersAdded;
	private boolean mPlaceholdersAddedOnce;
	private boolean mIsSelected;
	
	
	public boolean isSelected() { 
		return mIsSelected;
	}
}

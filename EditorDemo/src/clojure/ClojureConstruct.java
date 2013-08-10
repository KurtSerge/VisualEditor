package clojure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import clojure.constructs.meta.IfThenElseConstruct;
import clojure.constructs.placeholder.Placeholder;
import clojure.constructs.placeholder.PlaceholderConstruct;

import editor.Application;
import editor.Construct;
import editor.document.ConstructDocument;

/**
 * TODO: setPlaceholders() replacing previous setPlaceholders()
 * TODO: validate() should ensure all required placeholders are replaced
 * 
 * @author Christopher Lord
 */
public abstract class ClojureConstruct extends Construct
{
	public ClojureConstruct(ConstructDocument document, String type, Construct parent) {
		super(document, type, parent);
		
		mPlaceholders = null;
		mConstructsToPlaceholders = new HashMap<Construct, Placeholder>();
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
	@Override
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) { 
		if(mPlaceholders == null) {
			return super.canDeleteChild(index, child, isUser);
		}

		Placeholder descriptor = getPlaceholderForConstruct(child);		
		if(descriptor == null || descriptor.isPermanent()) { 
			Application.showErrorMessage("Cannot delete this construct");
			return false;
		}

		if(isUser == true && child.getClass().equals(PlaceholderConstruct.class)) { 
			Application.showErrorMessage("Cannot delete this construct");
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
	protected void onChildDeleted(int index, Construct deleted) {
		super.onChildDeleted(index, deleted);
		
		if(getPlaceholders() != null && 
				deleted.getClass().equals(PlaceholderConstruct.class) == false)	// Forced, internal deletion
		{
			Placeholder descriptor = getPlaceholderForIndex(index);
			if(!descriptor.isVariadic()) { 
				PlaceholderConstruct construct = new PlaceholderConstruct(mDocument, this, descriptor);
				descriptor.setPopulated(false);

			    Iterator<Entry<Construct, Placeholder>> it = mConstructsToPlaceholders.entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<Construct, Placeholder> pairs = (Map.Entry<Construct, Placeholder>)it.next();
			        if(pairs.getValue().equals(descriptor)) { 
			        	it.remove();
			        }
			    }
				
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
				Application.showErrorMessage(descriptor.getHint() + " requires instance of " + descriptor.getClassRestriction().getSimpleName());
				return false;
			}

			if(descriptor.isPermanent()) {
				Application.showErrorMessage("Cannot delete this construct");
				return false;
			}
			
			if(descriptor.isVariadic() && 
					replaceMe.getClass().equals(PlaceholderConstruct.class))
			{ 
				this.addChild(indexOfOldConstruct, newCon);
				return true;
			}

			descriptor.setPopulated(true);
			mConstructsToPlaceholders.put(newCon, descriptor);			
		}
				
		return super.replaceChild(replaceMe, newCon);
	}
	
	public void setPlaceholders(List<Placeholder> placeholders) {
		mPlaceholders = placeholders;
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
		Placeholder descriptor = mConstructsToPlaceholders.get(object);
		if(descriptor == null && mPlaceholders.get(mPlaceholders.size() - 1).isVariadic()) {
			return mPlaceholders.get(mPlaceholders.size() - 1);
		}
		
		return descriptor;
	}
	

	
	@Override
	public boolean validate() {
		return false;
	}

	private List<Placeholder> mPlaceholders;
	private boolean mPlaceholdersAdded;
	private boolean mPlaceholdersAddedOnce;
	private boolean mPlaceholdersAddedOptionals;
	private HashMap<Construct, Placeholder> mConstructsToPlaceholders;
	
	public List<Placeholder> getPlaceholders() { 
		return mPlaceholders;
	}
	
	/**
	 * Removes the specified placeholders. Set removeNonOptional to true
	 * to remove all of the placeholders on display.
	 */
	protected void removePlaceholders(boolean removeNonOptional) { 
		// Iterate over all the children, removing any placeholders
		LinkedList<Construct> deletingConstructs = new LinkedList<Construct>();
		for(Construct construct : this.children) { 
			if(construct.getClass().equals(PlaceholderConstruct.class)) {
				PlaceholderConstruct placeholderConstruct = (PlaceholderConstruct) construct;
				if(placeholderConstruct.getDescriptor().isOptional() || removeNonOptional) {
					deletingConstructs.add(construct);
				}
			}
		}

		// Now remove the identified children
		for(Construct deleted : deletingConstructs) { 
			deleted.delete();
		}
		
		mPlaceholdersAdded = false;
		if(mPlaceholdersAddedOptionals && removeNonOptional) 
			mPlaceholdersAddedOptionals = false;
	}
	
	/**
	 * Inserts all placeholders where required. This includes
	 * permanent, optional and regular placeholders.
	 */
	protected void insertPlaceholders() { 
		if(mPlaceholders != null && !mPlaceholdersAdded) { 
			// Add placeholders as children to this node
			for(int i = 0; i < mPlaceholders.size(); i++) { 
				Placeholder placeholder = mPlaceholders.get(i);
				if(placeholder.getIsPopulated() == true) {
					// The slot where this placeholder was going to sit
					// is currently occupied by a construct, do not refill
					continue;
				}
				
				if(mPlaceholdersAddedOptionals == true && 
						placeholder.isOptional() == false) {
					// Skip adding any optional placeholders if the
					// optional placeholders already exist
					continue;
				}
					
				if(placeholder.isPermanent()) {
					// Only add permanent placeholders once, ignore
					// them every other time we pass through
					if(mPlaceholdersAddedOnce == false) { 
						mConstructsToPlaceholders.put(placeholder.getPermanentConstruct(), placeholder);
						addChild(children.size(), placeholder.getPermanentConstruct());
						mPlaceholdersAddedOnce = true;
					}
				} else { 
					// This placeholder needs to be filled, add it in now
					PlaceholderConstruct construct = new PlaceholderConstruct(mDocument, this, placeholder);
					if(placeholder.isVariadic()) { 
						// Variadic placeholders always reside at the end
						// of the specified form, treat them like so..
						addChild(children.size(), construct);
					} else { 
						mConstructsToPlaceholders.put(construct, placeholder);						
						addChild(i, construct);
					}
				}
			}
			
			mPlaceholdersAdded = true;
			mPlaceholdersAddedOptionals = true;
		}
	}
	
	public void onBranchHighlighted() {
		insertPlaceholders();
	}
	
	public void onBranchUnhighlighted() {
		removePlaceholders(false);
	}
}

package construct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import editor.Application;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ConstructDocument;

public abstract class PlaceholdingConstruct extends Construct {
	
	public static class PlaceholdingConstructState { 
		private HashMap<Construct, Placeholder> mConstructsToPlaceholders;	
		private List<Placeholder> mPlaceholders;	
		private boolean mPlaceholdersAdded;				// Indicates all placeholders are added
		private boolean mPlaceholdersAddedOnce;			// Indicates perment placeholders are added
		private boolean mPlaceholdersAddedOptionals;
		private HashMap<Placeholder, Boolean> mPopulationStates;
		
		public PlaceholdingConstructState() {
			mConstructsToPlaceholders = new HashMap<Construct, Placeholder>();			
			mPopulationStates = new HashMap<Placeholder, Boolean>();
			
			mPlaceholders = null;
			mPlaceholdersAdded = false;
			mPlaceholdersAddedOnce = false;
			mPlaceholdersAddedOptionals = false;
		}
		
		@SuppressWarnings("unchecked")
		public PlaceholdingConstructState(PlaceholdingConstructState state) { 
			mPlaceholders = new ArrayList<Placeholder>();
			if(state.mPlaceholders != null) { 
				mPlaceholders.addAll(state.mPlaceholders);
			}
			
			mPopulationStates = (HashMap<Placeholder, Boolean>) state.mPopulationStates.clone();
			mPlaceholdersAdded = state.mPlaceholdersAdded;
			mPlaceholdersAddedOnce = state.mPlaceholdersAddedOnce;
			mPlaceholdersAddedOptionals = state.mPlaceholdersAddedOptionals;
			mConstructsToPlaceholders = state.mConstructsToPlaceholders;
		}
		
		public PlaceholdingConstructState deepCopy() { 
			return new PlaceholdingConstructState(this);
		}
		
		@Override
		public String toString() { 
			return "Added: " + mPlaceholdersAdded + "\n" +
					"Added Once: " + mPlaceholdersAddedOnce + "\n" + 
					"Added Optionals: " + mPlaceholdersAddedOptionals;
		}
		
		public boolean getIsPopulated(Placeholder placeholder) { 
			Boolean booleanObj = mPopulationStates.get(placeholder);
			if(booleanObj == null) { 
				booleanObj = false;
			}
			
			return booleanObj;
		}
		
		public void setIsPopulated(Placeholder placeholder, boolean value) { 
			mPopulationStates.put(placeholder, value);
		}
	}
	
	private PlaceholdingConstructState mState;

	public PlaceholdingConstruct(ConstructDocument document, String type, Construct parent) {
		super(document, type, parent);
		
//		setState(new PlaceholdingConstructState());
	}
	
	private void setState(PlaceholdingConstructState state) { 
		mState = state;
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
		if(mState != null && mState.mPlaceholders != null) { 
			int indexOfOldConstruct = this.children.indexOf(replaceMe);
			Placeholder descriptor = null;
			
			// If the index of the old construct is bigger than our placeholders
			// size, then we are likely dealing with a variadic placeholder at the
			// end of the placeholders list. Verify this, and assume this case.
			if(indexOfOldConstruct >= this.mState.mPlaceholders.size()) { 
				// Fetch the last descriptor and verify that it is variadic
				descriptor = mState.mPlaceholders.get(mState.mPlaceholders.size() - 1);
				if(!descriptor.isVariadic()) {
					System.err.println("<ClojureConstruct> Index of replaceChild exceeds mPlaceholders.size() but last placeholder is NOT variadic.");
					return false;
				}
			} else { 
				descriptor = this.mState.mPlaceholders.get(indexOfOldConstruct);
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
				this.addChild(indexOfOldConstruct, newCon, false);
				return true;
			}

			mState.setIsPopulated(descriptor, true);
			mState.mConstructsToPlaceholders.put(newCon, descriptor);			
		}
				
		return super.replaceChild(replaceMe, newCon);
	}
	
	/**
	 * If placeholders are set, prevent deletion of permanent & placeholder constructs.
	 */
	@Override
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) { 
		if(mState == null || mState.mPlaceholders == null) {
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
				mState.setIsPopulated(descriptor, false);

			    Iterator<Entry<Construct, Placeholder>> it = mState.mConstructsToPlaceholders.entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<Construct, Placeholder> pairs = (Map.Entry<Construct, Placeholder>)it.next();
			        if(pairs.getValue().equals(descriptor)) { 
			        	it.remove();
			        }
			    }
			    
			    mState.mConstructsToPlaceholders.put(construct, descriptor);
				
				this.addChild(index, construct, false);
			}
		}		
	}
	
	protected List<Placeholder> getPlaceholders() { 		
		return (mState == null) ? null : mState.mPlaceholders;
	}	
	
	protected void setPlaceholders(List<Placeholder> placeholders) {
		if(mState == null) { 
			setState(new PlaceholdingConstructState());
			mState.mPlaceholders = placeholders;
			
			insertPlaceholders();
			removePlaceholders(false);
		}
	}
	
	protected Placeholder getPlaceholderForIndex(int indexOfObject) {
		Placeholder descriptor = null;
		if(indexOfObject >= mState.mPlaceholders.size()) { 
			descriptor = mState.mPlaceholders.get(mState.mPlaceholders.size() - 1);
			if(!descriptor.isVariadic()) {
				return null;
			}
		} else { 
			descriptor = mState.mPlaceholders.get(indexOfObject);
		}
		
		return descriptor;
	}
	
	protected Placeholder getPlaceholderForConstruct(Construct object) {
		Placeholder descriptor = mState.mConstructsToPlaceholders.get(object);
		if(descriptor == null && mState.mPlaceholders.get(mState.mPlaceholders.size() - 1).isVariadic()) {
			return mState.mPlaceholders.get(mState.mPlaceholders.size() - 1);
		}
		
		return descriptor;
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
		
		if(mState != null) { 
			mState.mPlaceholdersAdded = false;
		
			if(mState.mPlaceholdersAddedOptionals && removeNonOptional) 
				mState.mPlaceholdersAddedOptionals = false;
		}
	}
	
	/**
	 * Inserts all placeholders where required. This includes
	 * permanent, optional and regular placeholders.
	 */
	protected void insertPlaceholders() { 
		if(mState != null && mState.mPlaceholders != null && !mState.mPlaceholdersAdded) { 
			// Add placeholders as children to this node
			for(int i = 0; i < mState.mPlaceholders.size(); i++) { 
				Placeholder placeholder = mState.mPlaceholders.get(i);
				if(mState.getIsPopulated(placeholder) == true) {
					// The slot where this placeholder was going to sit
					// is currently occupied by a construct, do not refill
					continue;
				}
				
				if(mState.mPlaceholdersAddedOptionals == true && 
						placeholder.isOptional() == false) {
					// Skip adding any optional placeholders if the
					// optional placeholders already exist
					continue;
				}
					
				if(placeholder.isPermanent()) {
					// Only add permanent placeholders once, ignore
					// them every other time we pass through
					if(mState.mPlaceholdersAddedOnce == false) { 
						mState.mConstructsToPlaceholders.put(placeholder.getPermanentConstruct(), placeholder);
						addChild(children.size(), placeholder.getPermanentConstruct());
						mState.mPlaceholdersAddedOnce = true;
					}
				} else { 
					// This placeholder needs to be filled, add it in now
					PlaceholderConstruct construct = new PlaceholderConstruct(mDocument, this, placeholder);
					if(placeholder.isVariadic()) { 
						// Variadic placeholders always reside at the end
						// of the specified form, treat them like so..
						addChild(children.size(), construct, false);
					} else { 
						mState.mConstructsToPlaceholders.put(construct, placeholder);						
						addChild(i, construct, false);
					}
				}
			}
			
			mState.mPlaceholdersAdded = true;
			mState.mPlaceholdersAddedOptionals = true;
		}
	}	
  
	public Construct deepCopy(Construct parent) {
		// Copy the placeholding state
		PlaceholdingConstruct placeholdingConstruct = (PlaceholdingConstruct) parent;
		if(mState != null) { 
			placeholdingConstruct.setState(mState.deepCopy());
		}

		for(Construct child : this.children) {
			if(mState != null) {
				// We need to copy the state of this placeholder
				Placeholder originalPlaceholder = mState.mConstructsToPlaceholders.get(child);
				int indexOfOriginalPlaceholder = mState.mPlaceholders.indexOf(originalPlaceholder);	
				if(indexOfOriginalPlaceholder >= 0)
				{
					// Copy the child like normal
					Construct newConstruct = child.deepCopy(parent);
					parent.children.add(newConstruct);
					
					// Now that we've established the reference, save it
					Placeholder newPlaceholder = placeholdingConstruct.mState.mPlaceholders.get(indexOfOriginalPlaceholder);
					placeholdingConstruct.mState.mConstructsToPlaceholders.put(newConstruct, newPlaceholder);
				} else { 
					// Copy the child like normal
					Construct newConstruct = child.deepCopy(parent);
					parent.children.add(newConstruct);
				}
			}
			else {
				// Copy the child like normal
				Construct newConstruct = child.deepCopy(parent);
				parent.children.add(newConstruct);
			}
		}
		
		return placeholdingConstruct;
	}
	
	
	/**
	 * In the following form:
	 * 		(symbol variadicObject1 variadicObject2 variadicPlaceholder*)
	 *
	 * Allows the following operations:
	 * 		variadicObject1: InsertBefore, InsertAfter, InsertReplace
	 * 		variadicObject2: InsertBefore, InsertAfter, InsertReplace
	 * 		variadicPlaceholder: InsertBefore, InsertReplace
	 */
	@Override
	public boolean canPerformAction(EInterfaceAction binding, Construct selected) {	
		if(getPlaceholders() != null) {
			// Determine the placeholder for the selected construct
			Placeholder placeholder = getPlaceholderForConstruct(selected);
			if(placeholder != null) { 
				// If we have selected a placeholder construct, if it is variadic,
				// then we can insert *before* it, but not after it..
				if(selected.getClass().equals(construct.PlaceholderConstruct.class)) {
					PlaceholderConstruct construct = (PlaceholderConstruct) selected;
					if(construct.getDescriptor().isVariadic()) { 
						switch(binding) { 
						case Bind_InsertBefore:
						case Bind_InsertReplace:
							return true;
							
						default:
							return false;
						}
					}
				}

				// If the selected construct is part of a variadic
				// placeholder, we can insert before and after freely
				if(placeholder.isVariadic()) {
					switch(binding) { 
					case Bind_InsertAfter:
					case Bind_InsertBefore:
					case Bind_InsertChild:
					case Bind_InsertReplace:
					case Bind_DuplicateToAdjacent:
						return true;
						
					default:
						return false;
					}
				}
			}
			
			return false;
		}

		// With no placeholders, allow all bindings
		return true;
	}	
}

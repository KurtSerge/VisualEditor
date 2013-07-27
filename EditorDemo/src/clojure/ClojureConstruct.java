package clojure;

import java.util.List;

import clojure.constructs.meta.IfThenElseConstruct;
import clojure.constructs.special.PlaceholderConstruct;

import editor.Construct;

public abstract class ClojureConstruct extends Construct
{
	public static class Placeholder {
		public static Placeholder createVariadicPlaceholder(String hint) {
			return new Placeholder(hint, true, true);
		}
		
		public static Placeholder createOptionalPlaceholder(String hint) { 
			return new Placeholder(hint, true, false);
		}
		
		public static Placeholder createOptionalPlaceholder(String hint, Class<ClojureConstruct> restriction) {
			// TODO: Class type restrictions
			return new Placeholder(hint, true, false);
		}
		
		public static Placeholder createPermanentPlaceholder(ClojureConstruct permanentInstance) {
			return new Placeholder(permanentInstance);
		}
		
		public static Placeholder createPlaceholder(String hint) { 
			return new Placeholder(hint, false, false);
		}
		
		protected Placeholder(ClojureConstruct permanentInstance) { 
			this.mPermanentConstruct = permanentInstance;
			this.mIsPermanent = true;
			this.mIsOptional = false;	
			this.mIsVariadic = false;
			this.mHint = null;			
		}
		
		protected Placeholder(String hint, boolean optional) {
			this(hint, optional, false);
		}
		
		protected Placeholder(String hint, boolean optional, boolean variadic) {
			this.mIsPermanent = false;
			this.mIsOptional = optional;
			this.mIsVariadic = variadic;
			this.mHint = hint;			
		}
		
		public boolean isPermanent() { 
			return mIsPermanent;
		}
		
		public ClojureConstruct getPermanentConstruct() { 
			return mPermanentConstruct;
		}
		
		public String getHint() { 
			return mHint;
		}
		
		public boolean isOptional() { 
			return mIsOptional;
		}
		
		public boolean isVariadic() { 
			return mIsVariadic;
		}
		
		private ClojureConstruct mPermanentConstruct;
		private boolean mIsPermanent;
		private boolean mIsOptional;
		private boolean mIsVariadic;
		private String mHint;
	}
	
	public ClojureConstruct(String type, Construct parent) {
		super(type, parent);
		
		mPlaceholders = null;
	}
	
	public void setPlaceholders(List<Placeholder> placeholders) {
		assert(mPlaceholders == null);	// TODO: Updating placeholders
		 
		mPlaceholders = placeholders;

//		
//		// Add placeholders as children to this node
		for(int i = 0; i < mPlaceholders.size(); i++) { 
			Placeholder placeholder = mPlaceholders.get(i);
			
			if(placeholder.isPermanent()) {
				addChild(i, placeholder.getPermanentConstruct());
			} else { 
				String displayText = placeholder.getHint();
				if(placeholder.isVariadic()) { 
					displayText = displayText.concat("*");
				} else if(placeholder.isOptional()) { 
					displayText = displayText.concat("?");
				}
				
				PlaceholderConstruct construct = new PlaceholderConstruct(this, displayText);
				construct.setDescriptor(placeholder);
				addChild(i, construct);	
			}
			
		}
	}

	public boolean canInsertChildren() { 
		return false;
	}
	
	public boolean canDeleteChild(int index, Construct child) { 
		
		if(mPlaceholders == null) {
			return true;
		}
		
		Placeholder descriptor = descriptorForConstruct(child);
		if(descriptor == null) {
			// No descriptor with mPlaceholders set is an error,
			// no deletion when it comes down to the error.
			return false; 
		}
		
		if(descriptor.isPermanent() == false && 
			child.getClass().equals(PlaceholderConstruct.class) == false)
		{ 
			return true;
		}
		
		
		return false;
	}

	private List<Placeholder> mPlaceholders;
	
	@Override
	public Construct deepCopy(Construct parent) {
		super.deepCopy(parent);
		
		if(parent.getClass().isInstance(ClojureConstruct.class)) {  
//			((ClojureConstruct) parent).setPlaceholders(mPlaceholders, mPlaceholdersOffset);
		}
		
		return null;
	}
	
	private Placeholder descriptorForConstruct(Construct object) {
		Placeholder descriptor = null;
	
		int indexOfObject = this.children.indexOf(object);
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
					System.err.println("Variadic check failure 11342707");
					return false;
				}
			} else { 
				descriptor = this.mPlaceholders.get(indexOfOldConstruct);
			}

			if(descriptor.isPermanent()) {
				System.err.println("Cannot replace permanent placeholder.");
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
}

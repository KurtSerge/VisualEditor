package construct;

import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

public class Placeholder {
	public static Placeholder createVariadicPlaceholder(String hint) {
		return new Placeholder(hint, true, true, null);
	}
	
	public static Placeholder createVariadicPlaceholder(String hint, Class<?> restriction) { 
		return new Placeholder(hint, true, true, restriction);
	}
	
	public static Placeholder createOptionalPlaceholder(String hint) { 
		return new Placeholder(hint, true, false, null);
	}
	
	public static Placeholder createOptionalPlaceholder(String hint, Class<?> restriction) {
		return new Placeholder(hint, true, false, restriction);
	}
	
	public static Placeholder createPermanentPlaceholder(ClojureConstruct permanentInstance) {
		return new Placeholder(permanentInstance);
	}
	
	public static Placeholder createPlaceholder(String hint) { 
		return new Placeholder(hint, false, false, null);
	}
	
	public static Placeholder createPlaceholder(String hint, Class<?> restriction) { 
		return new Placeholder(hint, false, false, restriction);
	}
	
	protected Placeholder(ClojureConstruct permanentInstance) { 
		this.mPermanentConstruct = permanentInstance;
		this.mIsPermanent = true;
		this.mIsOptional = false;	
		this.mIsVariadic = false;
		this.mHint = null;			
	}
	
	protected Placeholder(String hint, boolean optional) {
		this(hint, optional, false, null);
	}
	
	protected Placeholder(String hint, boolean optional, boolean variadic, Class<?> restriction) {
		this.mIsPermanent = false;
		this.mClassRestriction = restriction;
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
	
	public boolean isAllowed(Class<?> obj) { 
		if(mClassRestriction == null) 
			return true;
		
		return obj.equals(mClassRestriction);
	}
	
	public void setPopulated(boolean isPopulated) { 
		mIsPopulated = isPopulated;
	}
	
	public boolean getIsPopulated() { 
		return mIsPopulated;
	}
	
	public Class<?> getClassRestriction() { 
		return mClassRestriction;
	}
	
	public PlaceholderConstruct createConstruct(ConstructDocument document, Construct parent) { 
		return new PlaceholderConstruct(document, parent, this);
	}
	
	private Class<?> mClassRestriction;
	private ClojureConstruct mPermanentConstruct;
	private boolean mIsPermanent;
	private boolean mIsOptional;
	private boolean mIsVariadic;
	private boolean mIsPopulated;
	private String mHint;
}
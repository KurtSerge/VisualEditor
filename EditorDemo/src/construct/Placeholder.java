package construct;

import clojure.ClojureConstruct;
import editor.document.ConstructDocument;

/**
 * This descriptor may or may not be shared! Do not use the Placeholder
 * instance to store state information for a construct.
 * 
 * @author Christopher Lord
 */
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
		this.mClassRestriction = null;
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
		this.mPermanentConstruct = null;
		this.mIsPermanent = false;
		this.mClassRestriction = restriction;
		this.mIsOptional = optional;
		this.mIsVariadic = variadic;
		this.mHint = hint;			
	}
	
	public final boolean isPermanent() { 
		return mIsPermanent;
	}
	
	public final ClojureConstruct getPermanentConstruct() { 
		return mPermanentConstruct;
	}
	
	public final String getHint() { 
		return mHint;
	}
	
	public final boolean isOptional() { 
		return mIsOptional;
	}
	
	public final boolean isVariadic() { 
		return mIsVariadic;
	}
	
	public boolean isAllowed(Class<?> obj) { 
		if(mClassRestriction == null) 
			return true;
		
		return mClassRestriction.isAssignableFrom(obj);
	}
	
	public final Class<?> getClassRestriction() { 
		return mClassRestriction;
	}
	
	public PlaceholderConstruct createConstruct(ConstructDocument document, Construct parent) { 
		return new PlaceholderConstruct(document, parent, this);
	}
	
	private final Class<?> mClassRestriction;
	private final ClojureConstruct mPermanentConstruct;
	private final boolean mIsPermanent;
	private final boolean mIsOptional;
	private final boolean mIsVariadic;
	private final String mHint;
}
package editor;

public class Hotkey { 
	public Hotkey(int key) { 
		mIsControlPressed = false;
		mIsAltPressed = false;
		mIsShiftPressed = false;
		mKey = key;
	}
	
	public Hotkey(int key, boolean isControlPressed) {
		mIsControlPressed = isControlPressed;
		mIsAltPressed = false;
		mIsShiftPressed = false;
		mKey = key;
	}
	
	public Hotkey(int key, boolean isControlPressed, boolean isAltPressed) { 
		mIsControlPressed = isControlPressed;
		mIsAltPressed = isAltPressed;
		mIsShiftPressed = false;
		mKey = key;
	}
	
	public Hotkey(int key, boolean isControlPressed, boolean isAltPressed, boolean isShiftPressed) { 
		mIsControlPressed = isControlPressed;
		mIsAltPressed = isAltPressed;
		mIsShiftPressed = isShiftPressed;
		mKey = key;
	}		
	
	public void setFollowsWithAutoComplete(boolean value) { 
		mFollowsWithAutoComplete = value;
	}
	
	public boolean followsWithAutoComplete() { 
		return mFollowsWithAutoComplete;
	}
	
	public int getKey() { 
		return mKey;
	}
	
	public boolean isControlPressed() { 
		return mIsControlPressed;
	}
	
	public Hotkey setNext(Hotkey hotkey) { 
		mNext = hotkey;
		return hotkey;
	}
	
	public Hotkey getNext() { 
		return mNext;
	}
	
	public String serialize() { 
		String allMembers = "" + mKey + mIsControlPressed + mIsAltPressed + mIsShiftPressed;			
		return allMembers;
	}
	
	@Override 
	public int hashCode() { 
		String serialized = serialize();
		return serialized.hashCode();
	}
	
	@Override
	public boolean equals(Object hotkey) { 
		return hotkey.hashCode() == this.hashCode();
	}
	
	private final boolean mIsControlPressed;
	private final boolean mIsAltPressed;
	private final boolean mIsShiftPressed;
	private boolean mFollowsWithAutoComplete;
	private final int mKey;
	
	private Hotkey mNext;		
}
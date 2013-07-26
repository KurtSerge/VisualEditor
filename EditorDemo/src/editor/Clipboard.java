package editor;

import editor.document.ConstructDocument;

public class Clipboard {
	private Construct copy_ref = null;
	private Construct mBuffer = null;
	
	public void copy(Construct copy) {
		copy_ref = copy;
		mBuffer = null;
	}	
	
	// Be sure to use addChild, replace, etc on returned construct
	public Construct getCopyToPaste(Construct parent) {
		mBuffer = copy_ref.deepCopy(parent);
		return mBuffer;
	}
	
	// Always pastes just like "insert child"
	public Construct paste(Construct parent, ConstructDocument doc) {
		Construct copy = getCopyToPaste(parent);
		if(copy != null) {
			if(parent.addChild(parent.children.size(), copy) == true) {
				doc.editorsFromConstruct(copy);
			}
			else
				copy = null;
		}
		return copy;
	}
}

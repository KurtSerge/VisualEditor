package editor;

import editor.document.ConstructDocument;

public class ConstructPublisher {
	
	public static interface ConstructListener { 
		public void onConstructModified(Construct construct);
		public void onConstructAddedChild(Construct parent, Construct child, int index);
		public void onConstructRemovedChild(Construct parent, Construct child, int index);
	}

	private static ConstructPublisher sInstance = null;
	private ConstructDocument mDocument = null;
	private ConstructListener mListener = null;
	
	public static ConstructPublisher getInstance() { 
		if(sInstance == null) {
			sInstance = new ConstructPublisher();
		}
		
		return sInstance;
	}
	
	public void addListener(ConstructListener listener) { 
		mListener = listener;
	}

	public void setActiveDocument(ConstructDocument document) {
		mDocument = document;
	}
	
	protected boolean isConstructInDocument(Construct construct) { 
		if(mDocument == null)
			return false;
		
		if(construct == null)
			return false;
		
		if(construct.parent != null) {
			return isConstructInDocument(construct.parent);
		}
		
		return construct.equals(mDocument.getRootConstruct());
	}
	
	public void onConstructAddedChild(Construct parent, Construct child, int index) { 
		if(this.isConstructInDocument(parent)) { 
			mListener.onConstructAddedChild(parent, child, index);
		}
	}
	
	public void onConstructRemovedChild(Construct parent, Construct child, int index) { 
		if(this.isConstructInDocument(parent)) { 
			mListener.onConstructRemovedChild(parent, child, index);
		}
	}

	public void onConstructModified(Construct construct) {
		// TODO Auto-generated method stub
		if(this.isConstructInDocument(construct) &&
				mListener != null) { 
			mListener.onConstructModified(construct);
		}
	}
}

package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import autocomplete.IAutoCompleteListener;

import construct.Construct;

public abstract class ConstructEditor implements IAutoCompleteListener
{
	public final Construct construct;
	protected final ConstructEditorStore mBoundEditorStore;
	
	ConstructEditor(Construct construct, ConstructEditorStore store)
	{
		this.construct = construct;
		this.mBoundEditorStore = store;
		
		mBoundEditorStore.register(this);
	}
	
	@Override
	protected void finalize()
	{
		mBoundEditorStore.unregister(this);
	}
	
	public abstract Component get_component();
	public abstract Dimension get_size();
	
	public void update()
	{
		if(construct.parent != null) {
			WeakReference<ConstructEditor> ret = mBoundEditorStore.get(construct.parent);
			if(ret == null) { 
				return;
			}
			
			ConstructEditor parent_editor = ret.get();
			if(parent_editor != null) { 
				parent_editor.update();
			}
		}
	}
	
	final public ConstructEditor getParent() {
		if(this.construct.parent == null)
			return null;
		
		 return mBoundEditorStore.get(construct.parent).get();
	}
	
	public abstract void setSelected(Construct.SelectionCause cause, ConstructEditor lastSelected, boolean bSelect);
	
	// Editor-specific cleanup - per editor instance
	public abstract void onEditorDeleted();



	private void getAllChildConstructs(List<Construct> referenceList, Construct delete) {
		referenceList.add(delete);

		for(Construct child : delete.children)  {
			getAllChildConstructs(referenceList, child);
		}
	}

	// calls deleteMe on topmost editor
	public void deleteAll() {
		Construct top = construct;
		while(top.parent != null)
			top = top.parent;
		
		ConstructEditor topEditor = mBoundEditorStore.get(top).get();
		topEditor.delete();
	}
	
	final public boolean replaceChild(Construct child, Construct newCon) {
		List<Construct> childConstructs = new LinkedList<Construct>();
		getAllChildConstructs(childConstructs, child);
		
		// Do the construct replacement here, if the replacement
		// is successful, then we need to cleanup all editors related
		// to construct we have just replaced
		if(construct.replaceChild(child, newCon)) {
			for(Construct constructForEditorDeletion : childConstructs) {
				WeakReference<ConstructEditor> editor = mBoundEditorStore.get(constructForEditorDeletion);
				if(editor != null && editor.get() != null) { 
					editor.get().delete();
				}
			}
		}
	
		return true;
	}
	
	public final boolean delete() {
		List<Construct> childConstructs = new LinkedList<Construct>();
		getAllChildConstructs(childConstructs, construct);		

		if(construct.delete(true, true) == true) {
			for(Construct del : childConstructs) { 
				mBoundEditorStore.get(del).get().onEditorDeleted();
			}
		}
		
		return true;
	}
}


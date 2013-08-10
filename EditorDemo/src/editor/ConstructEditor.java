package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ConstructEditor
{
	public final Construct construct;
	private final ConstructEditorStore mBoundEditorStore;
	
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
	public abstract void delete();

	public final boolean deleteMe() {
		List<Construct> childConstructs = new LinkedList<Construct>();
		getAllChildConstructs(childConstructs, construct);		

		if(construct.delete(true, true) == true) {
			for(Construct del : childConstructs) { 
				mBoundEditorStore.get(del).get().delete();
			}
		}
		return true;
	}

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
		topEditor.deleteMe();
	}
	
	// Replace my construct with newCon.  Cleanup editors (prefer calling this over Construct.replaceChild())
	final public boolean replaceChild(Construct child, Construct newCon) {
		List<Construct> childConstructs = new LinkedList<Construct>();
		getAllChildConstructs(childConstructs, child);
		
		// replace
		if(construct.replaceChild(child, newCon)) {
			for(Construct del : childConstructs) {
				mBoundEditorStore.get(del).get().delete();
			}
		}
	
		return true;
	}
}


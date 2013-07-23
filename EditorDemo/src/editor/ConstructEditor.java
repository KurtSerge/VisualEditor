package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConstructEditor
{
	public final Construct construct;
	
	ConstructEditor(Construct construct)
	{
		this.construct = construct;
		editorsByConstructs.put(construct, new WeakReference(this));
	}
	
	@Override
	protected void finalize()
	{
		// What happens if someone calls get() for a strong pointer while we are in the finalizer??
		editorsByConstructs.remove(construct);
	}
	
	public abstract Component get_component();
	public abstract Dimension get_size();
	
	public void update()
	{
		if(construct.parent != null)
		{
			WeakReference<ConstructEditor> ret =  editorsByConstructs.get(construct.parent);
			// FIXME: Why is this ever null??
			if(ret == null)
				return;
			ConstructEditor parent_editor = ret.get();
			
			if(parent_editor != null)
				parent_editor.update();
		}
	}
	
	final public ConstructEditor getParent() {
		if(this.construct.parent == null)
			return null;
		
		 return ConstructEditor.editorsByConstructs.get(construct.parent).get();
	}
	
	protected static Map<Construct, WeakReference<ConstructEditor> > editorsByConstructs =
			Collections.synchronizedMap(new HashMap<Construct, WeakReference<ConstructEditor> >());

	public abstract void setSelected(boolean bSelect);
	
	// Editor-specific cleanup - per editor instance
	public abstract void delete();
	
	// Remove this constructeditor's UI
	public final boolean deleteMe() {
		if(construct.parent != null) {
			// Get constructs
			deleteList.clear();
			getAllConstructs(construct);
			// Delete Constructs
			construct.debugPrint();
			System.out.println();
			if(construct.delete() == true) {
				for(Construct del : deleteList) {
					//del.parent.children.remove(del);// FIXME: When I comment this, crashes go away.  wtf? 
					editorsByConstructs.get(del).get().delete();
				}
			}
		}
		construct.debugPrint();
		return true;
	}
	// FIXME: Ugly, how to do this better?
	static List<Construct> deleteList = new ArrayList<Construct>();
	private void getAllConstructs(Construct delete) {
		deleteList.add(delete);
		for(Construct child : delete.children)  {
			getAllConstructs(child);
		}
	}
	
	
	
	// calls deleteMe on topmost editor
	public void deleteAll() {
		Construct top = construct;
		while(top.parent != null)
			top = top.parent;
		ConstructEditor topEditor = editorsByConstructs.get(top).get();
		topEditor.deleteMe();
	}
}


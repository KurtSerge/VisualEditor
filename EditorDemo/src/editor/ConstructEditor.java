package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
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
	
	protected static Map<Construct, WeakReference<ConstructEditor> > editorsByConstructs =
			Collections.synchronizedMap(new HashMap<Construct, WeakReference<ConstructEditor> >());
}
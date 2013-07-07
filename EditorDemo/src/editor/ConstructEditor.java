package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
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
		editorsByConstructs.remove(construct);
	}
	
	public abstract Component get_component();
	public abstract Dimension get_size();
	
	public void update()
	{
		if(construct.parent != null)
			editorsByConstructs.get(construct.parent).get().update();
	}
	
	protected static Map<Construct, WeakReference<ConstructEditor> > editorsByConstructs =
			new HashMap<Construct, WeakReference<ConstructEditor> >();
}

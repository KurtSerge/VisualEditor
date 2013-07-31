package EditorFramework;


import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public abstract class ConstructEditor  {
	// Top-level construct for this editor
	protected Construct construct;
	
	// Map construct editors to their constructs
	protected static Map<Construct, WeakReference<ConstructEditor> > editorsByConstructs =
			new HashMap<Construct, WeakReference<ConstructEditor> >();
	
	public static Map<Construct, ConstructEditor> constructManager;
	
	public abstract Component getComponent();
	public abstract Dimension getSize();
	public abstract String toScreenText();
	
	public static ConstructEditor getConstructEditor(Construct construct) { 
		return editorsByConstructs.get(construct).get();
	}
	
	public void update() {
		Construct parent = construct.node.getParent().getData().construct;
		if(parent != null)
			editorsByConstructs.get(parent).get().update();
	}
	
	ConstructEditor(Construct construct) {
		this.construct = construct;
		editorsByConstructs.put(construct, new WeakReference(this));
	}
	
	protected void finalize()
	{
		editorsByConstructs.remove(construct);
	}
}

package EditorFramework;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

public abstract class ConstructEditor  {
	// Top-level construct for this editor
	protected Construct construct;
	
	public static Map<Construct, ConstructEditor> constructManager;
	
	public abstract Component getComponent();
	public abstract Dimension getSize();
	
	ConstructEditor(Construct construct) {
		this.construct = construct;
	}
}

package EditorFramework;

import java.awt.Component;
import java.awt.Dimension;

public abstract class ConstructEditor  {
	final Construct construct;
	public abstract Component getComponent();
	public abstract Dimension getSize();
	
	ConstructEditor(Construct construct) {
		this.construct = construct;
	}
}

package EditorFramework;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class ConstructEditor  {
	final Construct construct;
	public abstract Component getComponent();
	public abstract Dimension getSize();
	
	ConstructEditor(Construct construct) {
		this.construct = construct;
	}
}

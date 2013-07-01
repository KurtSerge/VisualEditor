package EditorFramework;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class ConstructEditor  {
	Construct construct;
	LayoutManager layout;
	public abstract Component getComponent();
	public abstract Dimension getSize();
}

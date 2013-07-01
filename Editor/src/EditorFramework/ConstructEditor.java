package EditorFramework;

import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class ConstructEditor implements LayoutManager  {
	Construct construct;
	LayoutManager layout;
	public abstract JPanel getPanel();
	public abstract Dimension getSize();
}

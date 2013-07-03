package EditorFramework;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.JPanel;


public class MonospaceConstructEditor extends ConstructEditor implements LayoutManager {	
	// Maps UI info to each syntax node
	private Map<SyntaxTreeElement, JPanel> panelMap;
	private String screenText;

	public MonospaceConstructEditor(Construct construct) {
		super(construct);
	}
	
	// Return the empty string.  i.e. ... in if(...){...}
	public final String getEmptyString() {
		return null;
	}
	
	public boolean ValidateScreenText(String screenText) {
		// Possible checks:
		// toScreenText should print N occurrances of emptyString (...), where N = getNumLeaves
		return false;
	}
	
	@Override
	public Component getComponent() {
		// this panel will be a function of sub-panels
		// J.setLayoutManager(this);
		return null;
	}
	
	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String toScreenText() {
		return null;
	}
	
	public final SyntaxTreeElement getElementAt(int row, int col) {
		// Return the lowest level SyntaxTreeElement at a row/col position.  
		// Done by searching SyntaxTree, checking Jpanel BoundingBoxes in panelMap
		// If row/col falls in boundingbox, check lower level bound boxes until the lowest is found
		 return null;
	}
	

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutContainer(Container parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub
		
	}
}
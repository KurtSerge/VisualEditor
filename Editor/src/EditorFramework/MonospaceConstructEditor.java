package EditorFramework;

import java.util.Map;

import javax.swing.JPanel;

import org.w3c.dom.css.Rect;


public abstract class MonospaceConstructEditor extends ConstructEditor {	
	// Maps UI info to each syntax node
	private Map<SyntaxTreeElement, JPanel> panelMap;
	private String screenText;
	
	
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
	public JPanel getPanel() {
		return null;
	}
	
	protected abstract String toScreenText();
	
	public final SyntaxTreeElement getElementAt(int row, int col) {
		// Return the lowest level SyntaxTreeElement at a row/col position.  
		// Done by searching SyntaxTree, checking Jpanel BoundingBoxes in panelMap
		// If row/col falls in boundingbox, check lower level bound boxes until the lowest is found
		 return null;
	}
}
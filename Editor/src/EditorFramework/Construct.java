package EditorFramework;

import javax.swing.JPanel;


public abstract class Construct {
	// Every construct is the top level of some syntax tree
	private SyntaxTree root;
	
	// Set root node.  Could be loaded from XML, or selected from some larger pre-loaded tree
	public final void SetSyntaxRoot(SyntaxTreeElement node) {
		root.setRoot(node);
	}
	
	public boolean ValidateSyntaxTree(SyntaxTreeElement top) {
		return false;
	}
	
	// Defined @ URI
	public abstract int getNumLeaves();
}

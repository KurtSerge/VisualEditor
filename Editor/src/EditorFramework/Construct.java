package EditorFramework;

import GenericTree.GenericTreeNode;

public class Construct {
	// Every construct is the top level of some syntax tree
	private SyntaxTree root;
	
	public Construct(String URI) {
		//FIXME: 
	}
	
	// Set root node.  Could be loaded from XML, or selected from some larger pre-loaded tree
	public final void setSyntaxRoot(GenericTreeNode<SyntaxTreeElement> node) {
		root.setRoot(node);
	}
	
	public boolean validateSyntaxTree(GenericTreeNode<SyntaxTreeElement> top) {
		return false;
	}
	
	// Defined @ URI
	public int getNumLeaves() {
		return 0;
	}
}

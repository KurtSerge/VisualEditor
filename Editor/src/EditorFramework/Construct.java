package EditorFramework;

import GenericTree.GenericTreeNode;

public abstract class Construct {
	// Every construct is the top level of some syntax tree
	protected final GenericTreeNode<SyntaxTreeElement> node;
	
	public Construct(GenericTreeNode<SyntaxTreeElement> node) {
		this.node = node;
		//FIXME: 
	}
	
	// Set root node.  Could be loaded from XML, or selected from some larger pre-loaded tree
	public final void setSyntaxRoot() {
		// FIXME
		//root.setRoot(node);
	}
	
	public abstract boolean validateSyntaxTree();

	
	public abstract String toPlainText();
}

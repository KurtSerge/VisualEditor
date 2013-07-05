package EditorFramework;

import GenericTree.GenericTreeNode;

public abstract class Construct {
	// Every construct is the top level of some syntax tree
	// Note, can get parent/child via "node"
	protected final GenericTreeNode<SyntaxTreeElement> node;
	
	protected Construct(GenericTreeNode<SyntaxTreeElement> node) {
		this.node = node;
		//FIXME: 
	}
	
	// Set root node.  
	//public final void setSyntaxRoot(GenericTreeNode<SyntaxTreeElement> node) {
	//	this.node = node;
	//}
	
	public abstract boolean validateSyntaxTree();

	
	public abstract String toPlainText();
}
package ConstructPlugins;

import EditorFramework.Construct;
import EditorFramework.MonospaceConstructEditor;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;

public class IfStatementConstruct extends Construct {

	public IfStatementConstruct(GenericTreeNode<SyntaxTreeElement> node) {
		super(node);
	}
	
	@Override
	public String toPlainText() {
		return "if(#node#)\n{#node#}";
	}

	@Override
	public boolean validateSyntaxTree() {
		// TODO Auto-generated method stub
		return false;
	}
}

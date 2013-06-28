import java.util.List;

import EditorFramework.SyntaxTree;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;


public class main {

    public static void main(String[] args) {
    	// SyntaxTreeTest, works - Add functions to simplify syntax, or how do I do typedef?? Possible?
    	SyntaxTree test = new SyntaxTree();
    
    	test.setRoot(new GenericTreeNode<SyntaxTreeElement>());
    	GenericTreeNode<SyntaxTreeElement> root = test.getRoot();
    	
		root = new GenericTreeNode<SyntaxTreeElement>();
		
		GenericTreeNode<SyntaxTreeElement> child = new GenericTreeNode<SyntaxTreeElement>();
		SyntaxTreeElement data = new SyntaxTreeElement();
		data.URI = "something";
		child.setData(data);
		root.addChild(child);
		
		int nNodes = root.getNumberOfChildren();

		List<GenericTreeNode<SyntaxTreeElement>> elements;
		elements = root.getChildren();
    }
}

import XML.SyntaxTreeLoader;
import EditorFramework.SyntaxTree;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;

public class XMLTest {
	
	
	public static void main(String[] args)  {
    	// Load XML
    	SyntaxTreeLoader loader = new SyntaxTreeLoader("sample.xml");
    	loader.printXML();
    	System.out.println();
    	loader.printTree();
    	GenericTreeNode<SyntaxTreeElement> root = loader.getSyntaxTree();
    	SyntaxTree treeTest = new SyntaxTree();
    	treeTest.setRoot(root);
    	
   
    	GenericTreeNode<SyntaxTreeElement> found = treeTest.findURI("dfosm");
	}
}

import EditorFramework.SyntaxTree;
import EditorFramework.SyntaxTreeElement;
import EditorFramework.SyntaxTreeLoader;
import GenericTree.GenericTreeNode;

public class XMLTest {
	
	
	public static void main(String[] args)  {
    	// Load XML
    	SyntaxTreeLoader loader = new SyntaxTreeLoader("C:/Development/VisualEditor/Editor/sample.xml");
    	loader.printXML();
    	System.out.println();
    	loader.printTree();
    	GenericTreeNode<SyntaxTreeElement> root = loader.getSyntaxTree();
    	SyntaxTree treeTest = new SyntaxTree();
    	treeTest.setRoot(root);
    	
   
    	GenericTreeNode<SyntaxTreeElement> found = treeTest.findURI("dfosm");
	}
}

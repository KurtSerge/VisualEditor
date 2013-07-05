import org.w3c.dom.Document;

import XML.Loader;
import XML.SyntaxTreeLoader;
import EditorFramework.ConstructFactory;
import EditorFramework.SyntaxTree;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;

public class XMLTest {
	
	
	public static void main(String[] args)  {
    	
		//GenericTreeNode<SyntaxTreeElement> root = SyntaxTreeLoader.loadSyntaxTree("sample.xml", "URILookup.xml", true);
    	//SyntaxTreeLoader.printTree(root);
    	
		
    	// Load XML
    	Document doc = Loader.getXMLDocument("sample.xml");
    	System.out.println("XML Check:"	);
    	SyntaxTreeLoader.printXML(doc);
    	
    	// Now check tree
    	System.out.println("\n\nTree Check:");
    	ConstructFactory factory = new ConstructFactory("URILookup.xml");
    	GenericTreeNode<SyntaxTreeElement> root = SyntaxTreeLoader.loadSyntaxTree("sample.xml", false, factory);
    	SyntaxTreeLoader.printTree(root);
	}
}

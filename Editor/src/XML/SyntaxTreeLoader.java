package XML;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import EditorFramework.ConstructFactory;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;

public class SyntaxTreeLoader {
	// Attributes
	private static String skURI = "URI";
	private static String skLiteral = "literal";
	



	
	public static GenericTreeNode<SyntaxTreeElement> loadSyntaxTree(String filename, boolean printXML, ConstructFactory factory) {
		GenericTreeNode<SyntaxTreeElement> syntaxTree = null;
		Document xmlDoc = null;
		xmlDoc = Loader.getXMLDocument(filename);
		if(printXML == true) {
			printXML(xmlDoc);
		}
		if(xmlDoc != null) {
			// Add root to tree
			Element root = xmlDoc.getDocumentElement();
			syntaxTree = new GenericTreeNode<SyntaxTreeElement>();
			SyntaxTreeElement data = new SyntaxTreeElement();
			data.URI = root.getAttribute(skURI);
			data.literal = root.getAttribute(skLiteral);
			data.construct = factory.createConstruct(data.URI, syntaxTree);
			syntaxTree.setData(data);
	
			addChildrenToTree(root, syntaxTree);
		}
		else {
			syntaxTree = null;
		}
		
		return syntaxTree;
	}
	
	
	// Recursively add children to the syntaxTree
	private static void addChildrenToTree(Element root, GenericTreeNode<SyntaxTreeElement> topnode) {
		NodeList children =	root.getChildNodes();
		
		// Load nodes into tree
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				// setup new node
				Element current = (Element)node;
				SyntaxTreeElement data = new SyntaxTreeElement();
				data.URI = current.getAttribute(skURI);
				data.literal = current.getAttribute(skLiteral);
				// Add new node
				GenericTreeNode<SyntaxTreeElement> newNode = new GenericTreeNode<SyntaxTreeElement>();
				newNode.setData(data);
				topnode.addChild(newNode);
				// recursive
				addChildrenToTree(current, newNode);
			}
		}
	}
	
	// Print SyntaxTreeElement, debug
	public static void printTree(GenericTreeNode<SyntaxTreeElement> root) {
    	List<GenericTreeNode<SyntaxTreeElement>> children = root.getChildren();
    	for(int i = 0; i < children.size(); i++) {
    		GenericTreeNode<SyntaxTreeElement> child = children.get(i);
    		System.out.print("\tURI=" + child.getData().URI);
    		if(child.getData().literal.length() != 0) {
    			System.out.print(", " + child.getData().literal);
    		}
    		System.out.println("\t\tParent=" + child.getParent().getData().URI);
    		printTree(child);
    	}
	}
	
	// Print XML file, for debug
	public static void printXML(Document doc) {
		Element root = doc.getDocumentElement();
		printChildren(root, 0);
	}
	private static void printChildren(Element root, int depth) {
		NodeList children =	root.getChildNodes();
		// Print all direct children
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element current = (Element)node;
				System.out.print("\tNode Name: " + current.getNodeName());
				System.out.print(", Level: " + depth);
				System.out.print(", URI:" + current.getAttribute(skURI));
				
				String lit = current.getAttribute(skLiteral);
				if(lit.length() != 0) {
					System.out.print("\tliteral: " + lit);
				}
				System.out.println("");
				
				printChildren(current, depth+1);
			}
		}
	}
	

	
	//public SyntaxTree getSyntaxTree() {
	//	return null;
	//}
	
}

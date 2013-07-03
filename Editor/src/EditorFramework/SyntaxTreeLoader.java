package EditorFramework;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import GenericTree.GenericTreeNode;

public class SyntaxTreeLoader {
	private final GenericTreeNode<SyntaxTreeElement> syntaxTree;
	private final Document xmlDoc;
	
	public SyntaxTreeLoader(String filename) {
			xmlDoc = getXMLDocument(filename);
			if(xmlDoc != null) {
				// Add root to tree
				Element root = xmlDoc.getDocumentElement();
				syntaxTree = new GenericTreeNode<SyntaxTreeElement>();
				SyntaxTreeElement data = new SyntaxTreeElement();
				data.URI = root.getAttribute("URI");
				data.literal = root.getAttribute("literal");
				syntaxTree.setData(data);
		    	
				addChildrenToTree(root, syntaxTree);
			}
			else {
				syntaxTree = null;
			}
	}
	
	// Recursively add children to the syntaxTree
	private void addChildrenToTree(Element root, GenericTreeNode<SyntaxTreeElement> topnode) {
		NodeList children =	root.getChildNodes();
		
		// Load nodes into tree
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				// setup new node
				Element current = (Element)node;
				SyntaxTreeElement data = new SyntaxTreeElement();
				data.URI = current.getAttribute("URI");
				data.literal = current.getAttribute("literal");
				// Add new node
				GenericTreeNode<SyntaxTreeElement> newNode = new GenericTreeNode<SyntaxTreeElement>();
				newNode.setData(data);
				topnode.addChild(newNode);
				// recursive
				addChildrenToTree(current, newNode);
			}
		}
	}
	
	private Document getXMLDocument(String filename) {
		File fXmlFile = new File(filename);
		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	// Print SyntaxTreeElement, debug
	public void printTree() {
		printTree(syntaxTree);
	}
	private void printTree(GenericTreeNode<SyntaxTreeElement> root) {
    	List<GenericTreeNode<SyntaxTreeElement>> children = root.getChildren();
    	for(int i = 0; i < children.size(); i++) {
    		GenericTreeNode<SyntaxTreeElement> child = children.get(i);
    		System.out.print("URI=" + child.getData().URI);
    		if(child.getData().literal.length() != 0) {
    			System.out.print(", " + child.getData().literal);
    		}
    		System.out.println("\t\tParent=" + child.getParent().getData().URI);
    		printTree(child);
    	}
	}
	
	
	public GenericTreeNode<SyntaxTreeElement> getSyntaxTree() {
		return syntaxTree;
	}
	
	// Print XML file, for debug
	public void printXML() {
		Element root = xmlDoc.getDocumentElement();
		printChildren(root, 0);
	}
	private void printChildren(Element root) {
		printChildren(root, 0);
	}
	private void printChildren(Element root, int depth) {
		NodeList children =	root.getChildNodes();
		// Print all direct children
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element current = (Element)node;
				System.out.print("\tNode Name: " + current.getNodeName());
				System.out.print(", Level: " + depth);
				System.out.print(", URI:" + current.getAttribute("URI"));
				
				String lit = current.getAttribute("literal");
				if(lit.length() != 0) {
					System.out.print("\tliteral: " + current.getAttribute("literal"));
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

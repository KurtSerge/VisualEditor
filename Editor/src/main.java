import java.awt.Component;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ConstructPlugins.IfStatement;
import EditorFramework.Construct;
import EditorFramework.ConstructEditor;
import EditorFramework.ConstructEditorFactory;
import EditorFramework.MonospaceConstructEditor;
import EditorFramework.SyntaxTree;
import EditorFramework.SyntaxTreeElement;
import EditorFramework.XMLLoader;
import GenericTree.GenericTreeNode;


public class main {
	
	private static void treeToString(GenericTreeNode<SyntaxTreeElement> root) {
    	List<GenericTreeNode<SyntaxTreeElement>> children = root.getChildren();
    	for(int i = 0; i < children.size(); i++) {
    		GenericTreeNode<SyntaxTreeElement> child = children.get(i);
    		System.out.print("URI=" + child.getData().URI);
    		if(child.getData().literal.length() != 0) {
    			System.out.print(", " + child.getData().literal);
    		}
    		System.out.println("\t\tParent=" + child.getParent().getData().URI);
    		treeToString(child);
    	}
	}
	
	private static GenericTreeNode<SyntaxTreeElement> loadXML(String filename) {
		Document doc = XMLTest.getXMLDocument(filename);
		GenericTreeNode<SyntaxTreeElement> treeroot = null;
		if(doc != null) {
			// Add root to tree
			Element root = doc.getDocumentElement();
	    	treeroot = new GenericTreeNode<SyntaxTreeElement>();
			SyntaxTreeElement data = new SyntaxTreeElement();
			data.URI = root.getAttribute("URI");
			data.literal = root.getAttribute("literal");
			treeroot.setData(data);
	    	
			addChildrenToTree(root, treeroot);
		}
		return treeroot;
	}
	
	
	public static void addChildrenToTree(Element root, GenericTreeNode<SyntaxTreeElement> topnode) {
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

	
    public static void main(String[] args) {
    	{
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
		

    	// Load XML
    	//GenericTreeNode<SyntaxTreeElement> root = loadXML("C:/Development/VisualEditor/Editor/sample.xml");
    	//treeToString(root);
    	
    	// Load XML
    	XMLLoader conXML = new XMLLoader();
    	conXML.load("C:/Development/VisualEditor/Editor/sample.xml");
    	conXML.toStringXML();
    	conXML.toStringTree();
    	GenericTreeNode<SyntaxTreeElement> root = conXML.getSyntaxTree();
    	

    	// How to use construct editor
    	ConstructEditorFactory cef = new ConstructEditorFactory();
    	cef.registerConstructEditor("if", IfStatement.class);
    	Construct construct = new Construct(IfStatement.class.getName());
    	construct.setSyntaxRoot(root);
    	ConstructEditor editor = cef.createConstructEditor("if", construct);
    	Component comp = editor.getComponent();
    }
}

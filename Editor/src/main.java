
import XML.SyntaxTreeLoader;


import EditorFramework.ConstructEditor;
import EditorFramework.ConstructEditorFactory;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;


public class main {

    public static void main(String[] args) {
    	// Load XML

    	GenericTreeNode<SyntaxTreeElement> root = SyntaxTreeLoader.loadSyntaxTree("sample.xml", "URILookup.xml", true);
    	SyntaxTreeLoader.printTree(root);
    	
    	// How to use construct editor
    	//ConstructEditorFactory cef = new ConstructEditorFactory("URILookup.xml");
    	//ConstructEditor editor = cef.createConstructEditor("builtin://if");
  
    }
}

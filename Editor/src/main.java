
import javax.swing.JFrame;

import XML.SyntaxTreeLoader;


import EditorFramework.ConstructEditor;
import EditorFramework.ConstructFactory;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;


public class main extends JFrame {

	public main() {
    	super("Editor Demo");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
    	
    	// Load XML - store as SyntaxTree
    	ConstructFactory factory = new ConstructFactory("URILookup.xml");
    	GenericTreeNode<SyntaxTreeElement> root = SyntaxTreeLoader.loadSyntaxTree("sample.xml", false, factory);
    	
    	//SyntaxTreeLoader.printTree(root);
    	ConstructEditor edit = factory.createConstructEditor("builtin://if", root.getData().construct);

    	
		this.add(edit.getComponent());

		this.pack();
		this.setSize(800, 600);
	}
	
    public static void main(String[] args) {
    	// How to use construct editor
    	//ConstructEditorFactory cef = new ConstructEditorFactory("URILookup.xml");
    	//ConstructEditor editor = cef.createConstructEditor("builtin://if");
  
    	new main();
    }
}

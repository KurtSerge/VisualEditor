import java.awt.Component;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import XML.SyntaxTreeLoader;
import XML.URIConfig;

import ConstructPlugins.IfStatement;
import EditorFramework.Construct;
import EditorFramework.ConstructEditor;
import EditorFramework.ConstructEditorFactory;
import EditorFramework.MonospaceConstructEditor;
import EditorFramework.SyntaxTree;
import EditorFramework.SyntaxTreeElement;
import GenericTree.GenericTreeNode;


public class main {

    public static void main(String[] args) {
    	// Load XML
    	SyntaxTreeLoader loader = new SyntaxTreeLoader("sample.xml");
    	GenericTreeNode<SyntaxTreeElement> root = loader.getSyntaxTree();


    	
    	
    	// How to use construct editor
    	ConstructEditorFactory cef = new ConstructEditorFactory();
    	cef.registerConstructEditor("builtin://if");
    	ConstructEditor editor = cef.createConstructEditor("sample.xml");
    	//Component comp = editor.getComponent();
    }
}

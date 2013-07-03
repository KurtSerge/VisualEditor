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
import EditorFramework.SyntaxTreeLoader;
import GenericTree.GenericTreeNode;


public class main {

    public static void main(String[] args) {
    	// Load XML
    	SyntaxTreeLoader loader = new SyntaxTreeLoader("C:/Development/VisualEditor/Editor/sample.xml");
    	GenericTreeNode<SyntaxTreeElement> root = loader.getSyntaxTree();

    	// How to use construct editor
    	ConstructEditorFactory cef = new ConstructEditorFactory();
    	cef.registerConstructEditor("if", IfStatement.class);
    	Construct construct = new Construct(IfStatement.class.getName());
    	construct.setSyntaxRoot(root);
    	ConstructEditor editor = cef.createConstructEditor("if", construct);
    	Component comp = editor.getComponent();
    }
}

package editor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import json.JSONController;
import json.ObjectConstruct;
import json.StringConstruct;
import json.StringLiteralConstruct;
import lisp.LispController;



public class Application extends JFrame
{
	Application()
	{
		super("Editor Demo");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		// Demo
		Construct jsonDocumentConstruct2 = null;
		
		try {
			jsonDocumentConstruct2 = JSONController.load_json(new FileInputStream("test2.json"));
		//	jsonDocumentConstruct2 = LispController.load_json(new FileInputStream("/Users/shmeebegek/temp/test.json"));
			
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
	
		/*
		// TODO: Load from file
		Construct jsonDocumentConstruct = new ObjectConstruct(null);
		
		Construct key_literal = new StringLiteralConstruct(jsonDocumentConstruct, "key");
		JSONConstructEditorFactory.create_editor_for(key_literal);
		
		Construct key = new StringConstruct(key_literal);
		JSONConstructEditorFactory.create_editor_for(key);
		key.children.add(key_literal);
		
		Construct value_literal = new StringLiteralConstruct(jsonDocumentConstruct, "value");
		JSONConstructEditorFactory.create_editor_for(value_literal);
		
		Construct value = new StringConstruct(value_literal);
		JSONConstructEditorFactory.create_editor_for(value);
		value.children.add(value_literal);
		
		
		jsonDocumentConstruct.children.add(key);
		jsonDocumentConstruct.children.add(value);
		
		this.add(JSONConstructEditorFactory.create_editor_for(jsonDocumentConstruct).get_component());
		*/
		
		this.add(JSONController.editors_from_constructs(jsonDocumentConstruct2).get_component());
			
		this.pack();
		this.setSize(800, 600);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Application();
	}

}

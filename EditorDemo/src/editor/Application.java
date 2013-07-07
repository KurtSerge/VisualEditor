package editor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import json.JSONController;
import json.ObjectConstruct;
import json.StringConstruct;
import json.StringLiteralConstruct;
import lisp.LispController;



public class Application extends JFrame implements KeyListener
{
	// Demo
	private Construct  jsonDocumentConstruct2 = null;
	private EditSelection selector = null;
	
	Application()
	{
		super("Editor Demo");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setBackground(Color.white);
		
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
		
		selector = new EditSelection(this, JSONController.editors);
		
		this.pack();
		this.setSize(800, 600);
		
		this.addKeyListener(this);
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

	private class EditSelection {
		private final JFrame frame;
		private final List<ConstructEditor> editors;
		private ConstructEditor selected = null;
		
		public EditSelection(JFrame frame, List<ConstructEditor> editors) {
			this.frame = frame;
			this.editors = editors;
		}
		
		public void SelectRandom() {
			int select = Math.abs((new Random()).nextInt()) % editors.size();
			ConstructEditor toSelect = editors.get(select);
			Select(toSelect);
		}
		
		public void SelectParentConstruct() {
			if(selected == null)
				return;
			Construct parent = selected.construct.parent;
			if(parent == null)
				return;
			
			 Select(ConstructEditor.editorsByConstructs.get(parent).get());
		}
		
		public void SelectFirstChildConstruct() {
			if(selected == null)
				return;
			if(selected.construct.children.size() == 0)
				return;
			
			Construct child = selected.construct.children.get(0);
			if(child == null)
				return;
			
			 Select(ConstructEditor.editorsByConstructs.get(child).get());
		}
		
		public void SelectAdjacentConstruct(boolean next) {
			if(selected == null)
				return;
			Construct parent = selected.construct.parent;
			if(parent == null)
				return;
			
			int myIndex = parent.children.indexOf(selected.construct);
			int selectIndex = (next) ? ++myIndex : --myIndex;
			if(selectIndex >= parent.children.size()) {
				selectIndex = 0;
			}	
			else if(selectIndex < 0) {
				selectIndex = parent.children.size()-1;
			}
			Construct newSelect = parent.children.get(selectIndex);
			if(newSelect == null)
				return;
		
			ConstructEditor edit = ConstructEditor.editorsByConstructs.get(newSelect).get();
			Select(edit);
		}
		
		public void Select(ConstructEditor sel) {
			if(selected != null)   {
				selected.get_component().setBackground(new Color(0,0,0,0));
			}
			sel.get_component().setBackground(Color.red);
			selected = sel;
			frame.repaint();
			
			
			// FIXME: I just want to draw a rect with top-most zorder!
			//Component draw = new JTextArea();
			//draw.setBackground(Color.red);
			//draw.setBounds(0, 0, 40, 40);
			//this.add(draw);
			//this.setComponentZOrder(draw, 0);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(selector.selected == null) {
			selector.SelectRandom();
			return;
		}
		
		switch(arg0.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			selector.SelectAdjacentConstruct(true);
			break;
		case KeyEvent.VK_LEFT:
			selector.SelectAdjacentConstruct(false);
			break;			
		case KeyEvent.VK_UP:
			selector.SelectParentConstruct();
			break;
		case KeyEvent.VK_DOWN:
			selector.SelectFirstChildConstruct();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

}

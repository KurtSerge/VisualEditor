package EditorFramework;

import GenericTree.GenericTreeNode;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextArea;


public class MonospaceConstructEditor extends ConstructEditor implements LayoutManager {	
	// Maps UI info to each syntax node
	private Map<SyntaxTreeElement, JPanel> panelMap;
	private String screenText;
	private JTextArea text_area = new JTextArea();
	private static Font font = new Font("Monospaced",Font.PLAIN, 12);
	
	public MonospaceConstructEditor(Construct construct) {
		super(construct);
	}
	
	// Return the empty string.  i.e. ... in if(...){...}
	public final String getEmptyString() {
		return null;
	}
	
	public boolean ValidateScreenText(String screenText) {
		// Possible checks:
		// toScreenText should print N occurrances of emptyString (...), where N = getNumLeaves
		return false;
	}
	
	@Override
	public Component getComponent() {
		return text_area;
	}
	
	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String toScreenText() {
		return null;
	}
	
	public final SyntaxTreeElement getElementAt(int row, int col) {
		// Return the lowest level SyntaxTreeElement at a row/col position.  
		// Done by searching SyntaxTree, checking Jpanel BoundingBoxes in panelMap
		// If row/col falls in boundingbox, check lower level bound boxes until the lowest is found
		 return null;
	}
	

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutContainer(Container parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub
	}
	
	
	
	
	
	
	
	public void update()
	{
		// This section ensures that we have the right children, without constantly removing/adding them, 
		//  which causes user-unfriendly behaviors (unselection etc)
		{
			Set<Component> text_area_components = new HashSet<Component>();
			
			for(Component c : text_area.getComponents())
				text_area_components.add(c);
			
			List<GenericTreeNode<SyntaxTreeElement>> children = construct.node.getChildren();
			
			for(int i = 0; i < children.size(); i++) {
				Construct child_construct = children.get(i).getData().construct;
				Component child_component = editorsByConstructs.get(child_construct).get().getComponent();
				
				if(!text_area_components.contains(child_component))
					text_area.add(child_component);
				
				text_area_components.remove(child_component);
			}
			
			for(Component c : text_area_components)
				text_area.remove(c);
		}
		
		//String current_text = my_text();
		String current_text = "test";
		
		String area_text = text_area.getText();
		
		System.out.println("\"" + current_text + "\" vs \"" + area_text + "\"");
		
		//if(!current_text.equals(area_text))
		//	text_area.setText(my_text());
		
		//super.update();
	}
	
}
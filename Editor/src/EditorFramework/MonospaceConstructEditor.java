package EditorFramework;

import GenericTree.GenericTreeNode;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class MonospaceConstructEditor extends ConstructEditor implements LayoutManager, DocumentListener {	
	// Maps UI info to each syntax node
	private Map<SyntaxTreeElement, JPanel> panelMap;
	private String screenText;
	private JTextArea text_area = new JTextArea();
	private static Font font = new Font("Monospaced",Font.PLAIN, 12);
	
	public MonospaceConstructEditor(Construct construct) {
		super(construct);
		
		text_area.setFont(font);
		text_area.setLayout(this);
		
		//text_area.setEditable(construct.screen_text() == null); set editable if accepts literal
		
		text_area.getDocument().addDocumentListener(this);
		SwingUtilities.invokeLater(new Runnable(){// run after all constructors, because parent doesn't exist yet
			@Override
			public void run() {
				MonospaceConstructEditor.this.update();
			}
		});
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
	
	public String toScreenText() {
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
				if(child_construct != null) {
					Component child_component = editorsByConstructs.get(child_construct).get().getComponent();
					
					if(!text_area_components.contains(child_component))
						text_area.add(child_component);
					
					text_area_components.remove(child_component);
				}
			}
			
			for(Component c : text_area_components)
				text_area.remove(c);
		}
		
		String current_text = my_text();
		
		String area_text = text_area.getText();
		
		System.out.println("\"" + current_text + "\" vs \"" + area_text + "\"");
		
		if(!current_text.equals(area_text))
			text_area.setText(my_text());
		
		super.update();
	}
	
	private String my_text()
	{
		String screen_text = editorsByConstructs.get(construct).get().toScreenText();
		
		if(screen_text != null)
		{
			final String child_string = "$(node)";
			
			StringBuilder builder = new StringBuilder();
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
			
			int lastChildEnd = 0;
			
			int nextChildBegins = -1;
			int childIndex = 0;
			while((nextChildBegins = screen_text.indexOf(child_string, lastChildEnd)) >= 0)
			{
				List<GenericTreeNode<SyntaxTreeElement>> children = construct.node.getChildren();
				ConstructEditor child = editorsByConstructs.get(children.get(childIndex)).get();
				Dimension child_size = child.getSize(); // this would be our getDimensions()
				
				// computing spaces between something[...]
				int n = (int)Math.ceil(((double)child_size.width) / ((double)metrics.stringWidth(" ")));
				
				builder.append(screen_text.substring(lastChildEnd, nextChildBegins));
				
				for(int i=0;i<n;++i)
					builder.append(' ');
				
				lastChildEnd = nextChildBegins + child_string.length();
				++childIndex;
			}
			
			// End
			builder.append(screen_text.substring(lastChildEnd));
			
			return builder.toString();
		}
		
		return construct.node.getData().literal;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
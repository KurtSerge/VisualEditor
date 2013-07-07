package editor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class MonospaceConstructEditor extends ConstructEditor implements LayoutManager, DocumentListener
{
	Color color_for_int(int x)
	{
		return ((x%2)==0) ? Color.WHITE : Color.RED;
	}
	
	public MonospaceConstructEditor(Construct construct)
	{
		super(construct);
		text_area.setFont(font);
		text_area.setLayout(this);
		
		text_area.setEnabled(construct.screen_text() == null);
		text_area.setDisabledTextColor(Color.DARK_GRAY);
		
		// Easy way to check out the layout
		//text_area.setBackground(color_for_int(construct.nesting_level()));
		
		text_area.getDocument().addDocumentListener(this);
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				MonospaceConstructEditor.this.update();
			}
			
		});
	}

	private JTextArea text_area = new JTextArea();
	private static Font font = new Font("Monospaced",Font.PLAIN, 12);
	
	public void update()
	{
		// This section ensures that we have the right children, without constantly removing/adding them, 
		//  which causes user-unfriendly behaviors (unselection etc)
		{
			Set<Component> text_area_components = new HashSet<Component>();
			
			for(Component c : text_area.getComponents())
				text_area_components.add(c);
			
			for(Construct child : construct.children)
			{
				ConstructEditor parent_editor = editorsByConstructs.get(child).get();
				
				if(parent_editor == null)
					continue;
				
				Component child_component = parent_editor.get_component();
				
				if(!text_area_components.contains(child_component))
					text_area.add(child_component);
				
				text_area_components.remove(child_component);
			}
			
			for(Component c : text_area_components)
				text_area.remove(c);
		}
		
		String current_text = my_text();
		String area_text = text_area.getText();
		
		//System.out.println("\"" + current_text + "\" vs \"" + area_text + "\"");
		
		//System.out.println("current_text: " + current_text);
		
		if(!current_text.equals(area_text))
			text_area.setText(my_text());
		
		super.update();
	}
	
	private String my_text()
	{
		String screen_text = construct.screen_text();
		
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
				ConstructEditor child = editorsByConstructs.get(construct.children.get(childIndex)).get();
				
				if(child == null)
					continue;
				
				Dimension child_size = child.get_size();
				
				int nWidth = (int)Math.ceil(((double)child_size.width) / ((double)metrics.stringWidth(" ")));
				int nHeight = (int)Math.ceil(((double)child_size.height) / ((double)metrics.getHeight()));
				
				builder.append(screen_text.substring(lastChildEnd, nextChildBegins));
				
				for(int row=0;row<nHeight;++row)
				{
					for(int col=0;col<nWidth;++col)
						builder.append(' ');
				
					if(row != (nHeight-1))
						builder.append('\n');
				}
				
				lastChildEnd = nextChildBegins + child_string.length();
				++childIndex;
			}
			
			// End
			builder.append(screen_text.substring(lastChildEnd));
			
			return builder.toString();
		}
		
		return construct.literal;
	}
	
	@Override
	public Component get_component() {
		return text_area;
	}

	private static int NewlineCount(String text)
	{
		int ret = 0;
		
		for(int i=0;i<text.length();++i)
		{
			if(text.charAt(i) == '\n')
				++ret;
		}
		
		return ret;
	}
	
	@Override
	public Dimension get_size() {
		FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
		
		String my_text = my_text();
		
		int max_line_width = 0;
		
		int next_newline = -1;
		int last_line_end = 0;
		
		for(;(next_newline = my_text.indexOf('\n', last_line_end)) != -1;last_line_end = (next_newline + 1))
		{
			String line = my_text.substring(last_line_end, next_newline);
			
			max_line_width = Math.max(max_line_width, metrics.stringWidth(line));
		}
		
		max_line_width = Math.max(max_line_width, metrics.stringWidth(my_text.substring(last_line_end)));
		
		return new Dimension(max_line_width, 
							 metrics.getHeight() * (NewlineCount(my_text)+1));
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return this.get_size();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return this.get_size();
	}
	
	private static Point StringPoint(String s, FontMetrics metrics)
	{
		Point ret = new Point(0,0);
		
		ret.y = metrics.getHeight() * NewlineCount(s);
		
		int lastNewline = s.lastIndexOf('\n')+1;
		
		if(lastNewline != -1)
			s = s.substring(lastNewline);
		
		ret.x = metrics.stringWidth(s);
		
		return ret;
	}

	@Override
	public void layoutContainer(Container parent) {
		
		// TODO: This repeats logic
		String screen_text = construct.screen_text();
		
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
				ConstructEditor child = editorsByConstructs.get(construct.children.get(childIndex)).get();
				
				if(child == null)
					continue;
				
				Dimension child_size = child.get_size();
				
				int nWidth = (int)Math.ceil(((double)child_size.width) / ((double)metrics.stringWidth(" ")));
				int nHeight = (int)Math.ceil(((double)child_size.height) / ((double)metrics.getHeight()));
				
				builder.append(screen_text.substring(lastChildEnd, nextChildBegins));
				
				Point pt = StringPoint(builder.toString(), metrics);
				
				child.get_component().reshape(pt.x, 
											  pt.y, 
											  child_size.width, child_size.height);
				
				for(int row=0;row<nHeight;++row)
				{
					for(int col=0;col<nWidth;++col)
						builder.append(' ');
				
					if(row != (nHeight-1))
						builder.append('\n');
				}
				
				lastChildEnd = nextChildBegins + child_string.length();
				++childIndex;
			}
		}
	}

	void on_change()
	{
		// Avoid Exception in thread "main" java.lang.IllegalStateException: Attempt to mutate in notification
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				if(construct.screen_text() == null)
					construct.literal = text_area.getText();
				MonospaceConstructEditor.this.update();
			}
		});
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		on_change();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		on_change();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		on_change();
	}

}
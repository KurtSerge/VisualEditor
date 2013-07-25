package editor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import clojure.ClojureController;
import editor.document.Document;

import json.JSONController;


public class MonospaceConstructEditor extends ConstructEditor implements LayoutManager, DocumentListener
{
	private static boolean skDebug_ShowBorders = false;
	
	Color color_for_int(int x)
	{
		return ((x%2)==0) ? Color.WHITE : Color.RED;
	}
	
	private JTextArea text_area;
	private static Font font = new Font("Monospaced",Font.PLAIN, 14);
	public class TransparentTextArea extends JTextArea {
		public TransparentTextArea() {
			super();
			setBackground(new Color(0,0,0,0));
		}
        @Override
        protected void paintComponent( Graphics g ) {
			super.paintComponent(g);
			
			// Repaint top component if any component changes
			Component iter = this;
			Component top = this;
			while(iter != null) {
				top = iter;
				iter = iter.getParent();
			}
			top.repaint();
		}
	}
	
	private class TextListener implements KeyListener {
		public TextListener() {

		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Find base
				requestTopFocus();
			}
			
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
	private Document mDocument = null;

	public MonospaceConstructEditor(Construct construct, Document document)
	{
		super(construct);
		
		mDocument = document;
		
		if(construct.parent == null)  {
			text_area = new JTextArea();
			text_area.setHighlighter(null);
			text_area.setBackground(new Color(255,255,255,255));
			text_area.setName("mono_base");
		}
		else  {
			text_area = new TransparentTextArea();
			// If not editable
			if(construct.screen_text() != null) 
				text_area.setFocusable(false);
		}
		
		text_area.setFont(font);
		text_area.setLayout(this);
		text_area.setEditable(construct.screen_text() == null);
		text_area.setDisabledTextColor(Color.DARK_GRAY);
		text_area.setForeground(construct.debug_getForegroundColor());
	
		// Easy way to check out the layout
//		text_area.setBackground(color_for_int(construct.nesting_level()));
		
		text_area.getDocument().addDocumentListener(this);
		SwingUtilities.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				MonospaceConstructEditor.this.update();
			}
			
		});
	}
	
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
				if(editorsByConstructs.get(child) == null) {
					System.out.println("ClojureController.. editors_from_constructs(child) .. " + child);
					mDocument.editorsFromConstruct(child); 
				}
				
				WeakReference<ConstructEditor> editor = editorsByConstructs.get(child);
				ConstructEditor parent_editor = editor.get();
				
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
		if(skDebug_ShowBorders) {
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			text_area.setBorder(border);
		}
		
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
	
	public void RemoveComponents() {
		text_area.getDocument().removeDocumentListener(this);
		text_area.getParent().remove(text_area);
	}


	private TextListener textListener;
	@Override
	public void setSelected(boolean bSelect) {
		if(bSelect == true) {
			text_area.setBackground(Color.red);
			textListener = new TextListener();
			text_area.addKeyListener(textListener);
			text_area.setForeground(Color.white);
			if(construct != null && construct.screen_text() == null) { // editable
				get_component().requestFocus();
				//text_area.selectAll();
			}
		} 
		else {
			text_area.select(0, 0);
			text_area.setForeground(construct.debug_getForegroundColor());
			text_area.setBackground(new Color(0,0,0,0));
			text_area.removeKeyListener(textListener);
			requestTopFocus();
			textListener = null;
		}
	}
	
	// Delete editor and cleanup
	final public boolean deleteMe() {
		int index = this.construct.parent.children.indexOf(this.construct);
		if(this.construct.parent.deleteChild(construct) == true)
		{
			if(textListener != null)
				text_area.removeKeyListener(textListener);
			
			this.RemoveComponents();

			removeEditors(this);

			//editorsByConstructs.get(child).get().deleteMe();
			//editorsByConstructs.remove(this);
			
			this.update();
			return true;
		} else { 
			this.setSelected(true);
			this.update();
		}
		return false;
	}
	
	private void removeEditors(ConstructEditor editor) {
		mDocument.remove(editor);
		
		for(Construct child : editor.construct.children) {		
			ConstructEditor remove = editorsByConstructs.get(child).get();
			removeEditors(remove);
			mDocument.remove(remove);
		}		
	}

	// Set focus to topmost monospace editor
	private void requestTopFocus() {
		Component iter = text_area;
		while(iter.getName() != "mono_base") {
			iter = iter.getParent();
			if(iter == null)
				return;
		}
		iter.requestFocus();
		
	}
}
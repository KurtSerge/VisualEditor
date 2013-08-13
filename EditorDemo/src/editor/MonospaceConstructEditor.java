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
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import autocomplete.IAutoCompleteListener;
import autocomplete.AutoCompleteDialog.SimpleAutoCompleteEntry;

import construct.Construct;
import construct.Construct.ConstructAction;
import construct.Construct.SelectionCause;
import editor.BaseController.EKeyBinding;
import editor.document.ConstructDocument;


public class MonospaceConstructEditor extends ConstructEditor implements LayoutManager, DocumentListener, IAutoCompleteListener
{
	private static boolean skDebug_ShowBorders = false;
	
	Color color_for_int(int x)
	{
		return ((x%2)==0) ? Color.WHITE : Color.RED;
	}
	
	private JTextArea text_area;
	private static Font font = new Font("Monospaced",Font.PLAIN, 14);
	public class TransparentTextArea extends JTextArea {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1482967738035295842L;
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
			if(e.getKeyCode() == KeyEvent.VK_TAB) {
				
				if(construct.isSoleDependantConstruct()) { 
					System.out.println("Currently in a sole dependency, going to parent");
					if(mController == null || 
							mController.mConstructSelector == null)
					{ 
						System.err.println("Dependency construct cannot move selection to parent");
					}
					
					mController.mConstructSelector.SelectParentConstruct();
				}
				
				if(e.isAltDown()) { 
					for(BaseControllerListener listener : mController.getActionListeners()) {
						listener.onReceievedAction(mController, EKeyBinding.Bind_DuplicateToAdjacent, null);
					}
				} else {
					// Select the next adjacent construct (cancel editing)
					if(mController != null && mController.mConstructSelector != null) { 
						mController.mConstructSelector.SelectAdjacentConstruct(!e.isShiftDown());
					}
				}

				e.consume();
			} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Find base
				requestTopFocus();
			} else if(e.getKeyChar() == '\n') { 
				e.consume();
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			if(construct != null) { 
				
				ConstructAction action = construct.onReceivedKeyEvent(e,  true);
				switch(action) {
					case DeleteThis:
						mController.DeleteAllSelected();
						break;
				
					case ConsumeEvent:
						e.consume();
						break;
				
					default:
						break;
				}
			}			
		}
	}
	
	private ConstructDocument mDocument = null;
	private final BaseController mController;
	
	public MonospaceConstructEditor(ConstructEditorStore bindingEditorStore, BaseController controller, Construct construct, ConstructDocument document)
	{
		super(construct, bindingEditorStore);
		
		mController = controller;
		mDocument = document;
		
		assert(mController != null);
		assert(mDocument != null);
		
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
			
			if(text_area == null)// FIXME: Why does this happen? InvokeLater?
				return;
			
			for(Component c : text_area.getComponents())
				text_area_components.add(c);
			
			for(Construct child : construct.children)
			{
				if(mDocument.editorsFromConstruct(child) == null) {
					mDocument.editorsFromConstruct(child); 
				}
				
				ConstructEditor parent_editor = mDocument.editorsFromConstruct(child);
				if(parent_editor == null) { 
					System.err.println("WeakReference<ConstructEditor> for Child (" + child.type + ") not found");
					continue;
				}

				
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
				if(construct.children.size() == 0) // FIXME: Temp, I think this check might be necessary because I am not deleting something properly
					return "";

				Construct constructChild = construct.children.get(childIndex);
				ConstructEditor child = mDocument.editorsFromConstruct(constructChild);
				if(child == null)
					throw(new RuntimeException("Probably forgot to add construct to editorsByConstructs with 'JSONController.editors_from_constructs'"));

				
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
				ConstructEditor child = mDocument.editorsFromConstruct(construct.children.get(childIndex));
				
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
				ConstructPublisher.getInstance().onConstructModified(construct);				
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
	
	private void propagateHighlightSelection(ConstructEditor parent) { 
		parent.construct.onBranchHighlighted();
		
		// Set the parent as highlighted
		Construct construct = parent.construct.parent;
		if(construct != null) { 
			ConstructEditor editor = mDocument.editorsFromConstruct(construct);
			if(editor != null) { 	
				propagateHighlightSelection(editor);
			}
		}
	}
	
	protected String getBreadcrumbs(Construct construct) {
		String parentcrumbs = "";
		if(construct.parent != null) { 
			parentcrumbs = getBreadcrumbs(construct.parent);
		}
		
		return parentcrumbs.concat(" > " + construct.type);
	}
	
	@Override
	public void setSelected(Construct.SelectionCause cause, ConstructEditor currentOrNewlySelected, boolean bSelect) {
		if(bSelect == true) {
			propagateHighlightSelection(this);
			
			update();
			
			text_area.setBackground(new Color(230, 230, 230));
			textListener = new TextListener();
			
			text_area.addKeyListener(textListener);

			if(construct != null && construct.screen_text() == null) { // editable
				get_component().requestFocus();
				
				if(cause != Construct.SelectionCause.SelectedDirectlyWithMouse)
					text_area.moveCaretPosition(text_area.getDocument().getLength());
				
				if(cause == Construct.SelectionCause.SelectedAfterDuplicatingSibling ||
						cause == Construct.SelectionCause.SelectedAfterInsert) { 
					text_area.selectAll();
				}
			}
			
			
			String breadcrumbs = getBreadcrumbs(this.construct);
			Application.showDebugMessage("Selected " + breadcrumbs.substring(3));
		} 
		else {
			if(currentOrNewlySelected != null) {
				if(currentOrNewlySelected.construct.parent == null || 
					currentOrNewlySelected.construct.parent.equals(this.construct) == false)
				{
					construct.onBranchUnhighlighted();
				}
			}
			
			update();
			
			text_area.select(0, 0);
			text_area.setForeground(construct.debug_getForegroundColor());
			text_area.setBackground(new Color(0,0,0,0));
			text_area.removeKeyListener(textListener);
			
			requestTopFocus();
			textListener = null;
		}
	}
	
	private void removeEditors(ConstructEditor editor) {
		mDocument.getConstructEditorStore().unregister(editor);
		
		for(Construct child : editor.construct.children) {		
			ConstructEditor remove = mDocument.editorsFromConstruct(child);
			removeEditors(remove);
			mDocument.getConstructEditorStore().unregister(editor);
		}		
	}

	@Override
	public void delete() {
		mDocument.getConstructEditorStore().unregister(this);
		
		if(textListener != null)
			text_area.removeKeyListener(textListener);
		
		RemoveComponents();
	}
	
	// Set focus to topmost monospace editor
	public void requestTopFocus() {
		Component iter = text_area;
		while(iter.getName() != "mono_base") {
			iter = iter.getParent();
			if(iter == null)
				return;
		}
		
		iter.requestFocus();
	}
	
	private boolean isPerformingUpdate = false;
	
	
	@Override
	public void onAutoCompleteCreateReplacement(BaseController controller, SimpleAutoCompleteEntry entry) {
		Construct construct = entry.create(mDocument, this.construct.parent);
		ConstructEditor editor = mDocument.editorsFromConstruct(construct);
		WeakReference<ConstructEditor> weakParentEditor = mBoundEditorStore.get(construct.parent);
		if(weakParentEditor != null) { 
			ConstructEditor parentEditor = weakParentEditor.get();
			if(parentEditor != null)  {
				
				switch(this.construct.getAutoCompleteStyle()) { 
					case Replace:
						if(construct.parent.replaceChild(this.construct, construct)) {
							controller.mConstructSelector.Select(SelectionCause.Selected, editor);
						}
						break;
					
					default:
						System.err.println("Unsupported AutoCompleteStyle." + this.construct.getAutoCompleteStyle().toString() + " in MonospaceConstructEditor");
						break;
				}				
			}
		}
		
		Application.getApplication().hideAutoComplete(true);
	}
}
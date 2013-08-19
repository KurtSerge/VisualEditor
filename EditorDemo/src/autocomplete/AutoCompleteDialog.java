package autocomplete;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIDefaults;

import clojure.constructs.meta.UnknownConstruct;
import construct.Construct;
import editor.ConstructEditor;
import editor.InterfaceController;
import editor.InterfaceController.EInterfaceAction;
import editor.document.ConstructDocument;

public class AutoCompleteDialog extends JDialog {
	
	private AutoCompletePanel mPanel = null;
	private JList mList = null;
	private JLabel mTextArea;
	
	private final IAutoCompleteListener mListener;
	private final ConstructEditor mEditor;
	private final InterfaceController mController;

	private Collection<Class<?>> mClassRestrictions;
	private List<SimpleAutoCompleteEntry> mAllEntries;
	private List<SimpleAutoCompleteEntry> mFilteredEntries;
	private DefaultListModel mModel = new DefaultListModel();
	private String mFilterString = "";
	
	public static class DisplayPair { 
		public String title;
		public String description;
	}
	
	public static class SimpleAutoCompleteEntry implements Comparable<SimpleAutoCompleteEntry> { 
		public SimpleAutoCompleteEntry(String title, String description, Class<?> instance) { 
			mTitle = title;
			mDescription = description;
			mClass = instance;			
		}
		
		public boolean isUsableForFilterString(String filter) { 
			return mTitle.startsWith(filter);
		}
		
		public boolean isExactForFilterString(String filter) { 
			return mTitle.equalsIgnoreCase(filter);
		}
		
		public String getTitle() { 
			return mTitle;
		}
		
		public String getDescription() { 
			return mDescription;
		}
		
		public Construct create(ConstructDocument document, Construct parent) {  
			try {
				Constructor<?> constructor = mClass.getConstructor(ConstructDocument.class, Construct.class, String.class);
				return (Construct) constructor.newInstance(document, parent, null);
			} catch (Exception ex) { 
			}
			
			try {
				Constructor<?> constructor = mClass.getConstructor(ConstructDocument.class, Construct.class);
				return (Construct) constructor.newInstance(document, parent);
			} catch (Exception ex) { 
			}
			
			return new UnknownConstruct(document, parent);
		}
		
		public Class<?> getInstanceClass() { 
			return mClass;
		}
		
		private String mTitle;
		private String mDescription;
		private Class<?> mClass;
		
		@Override
		public int compareTo(SimpleAutoCompleteEntry arg0) {
			return mTitle.compareTo(arg0.getTitle());
		}		
	}
	
	public AutoCompleteDialog(InterfaceController controller, ConstructEditor editor, IAutoCompleteListener listener, EInterfaceAction binding) {
		mListener = listener;
		mEditor = editor;
		mController = controller;
		
		mClassRestrictions = editor.construct.getParentForBinding(binding).getAutoCompleteClasses();
		mAllEntries = new LinkedList<SimpleAutoCompleteEntry>();
		mFilteredEntries = new LinkedList<SimpleAutoCompleteEntry>();
		
		// Basic types
		addEntry(new SimpleAutoCompleteEntry("integer", "", clojure.constructs.IntegerConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("string", "", clojure.constructs.StringConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("symbol", "", clojure.constructs.SymbolConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("double", "", clojure.constructs.DoubleConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("boolean", "", clojure.constructs.BooleanConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("keyword", "", clojure.constructs.KeywordConstruct.class));
		
		// Containers
		addEntry(new SimpleAutoCompleteEntry("list", "( ... )", clojure.constructs.containers.ListConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("map", "{ ... }", clojure.constructs.containers.MapConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("vector", "[ ... ]", clojure.constructs.containers.VectorConstruct.class));
		
		// Placeholder types
		addEntry(new SimpleAutoCompleteEntry("def", "(def symbol doc-string? init?)", clojure.constructs.meta.DefConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("defn", "(defn name doc-string? attr-map? [params*] exprs*)", clojure.constructs.meta.DefineFunctionConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("defmacro", "(defn name doc-string? attr-map? [params*] exprs*)", clojure.constructs.meta.defmacro.class));
		addEntry(new SimpleAutoCompleteEntry("do", "(do exprs*)", clojure.constructs.meta.DoConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("case", "(case test conditions*)", clojure.constructs.meta.CaseConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("fn", "(fn name [param*] exprs*)", clojure.constructs.meta.FunctionConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("if then else", "(if test then else?)", clojure.constructs.meta.IfThenElseConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("keyword pair", ":key expr", clojure.constructs.special.KeywordExpressionPairConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("key value pair", "test-constant result-expr", clojure.constructs.meta.KeyValuePairConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("let", "(let [bindings] exprs*)", clojure.constructs.meta.LetConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("loop", "(loop [bindings*] exprs*)", clojure.constructs.meta.LoopConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("recur", "(recur exprs*)", clojure.constructs.meta.RecurConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("rand", "(rand n?)", clojure.constructs.meta.randConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("rand-nth", "(rand-nth collection)", clojure.constructs.meta.rand_nthConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("function", "(symbol exprs*)", clojure.constructs.meta.SymbolList.class));
		
		
		Collections.sort(mAllEntries);
		
		int width = 400;
		
		try {
			setUndecorated(true);			
			
			getRootPane().setOpaque (true);
			getContentPane().setBackground (new Color (0, 0, 0));
			setBackground(new Color (0, 0, 0));
			getRootPane().setOpaque(true);
			setAlwaysOnTop(true);
			setFocusable(false);
			setResizable(true);
			setFocusable(true);
			setFocusableWindowState(false);
		} catch(Exception ex) { 
			ex.printStackTrace();
		}

		mPanel = new AutoCompletePanel(); 
		mPanel.setSize(width, 200);
		add(mPanel);
		
		mList = new JList(mModel);
		mList.setCellRenderer(new ListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean hasFocus)
	        {

	            final DisplayPair text = (DisplayPair) value;

	            //create panel
	            final JPanel p = new JPanel();
	            p.setLayout(new BorderLayout());

	            //text
	            final JTextArea ta = new JTextArea();
	            ta.setText(" " + text.title);
	            p.add(ta, BorderLayout.WEST);
	            
	            final JTextArea syntax = new JTextArea();
	            syntax.setText(" " + text.description);
	            syntax.setForeground(Color.LIGHT_GRAY);
	            p.add(syntax, BorderLayout.CENTER);	            
	            
	            if(isSelected) { 
	            	ta.setBackground(list.getSelectionBackground());
	            	ta.setForeground(list.getSelectionForeground());
	            	syntax.setBackground(list.getSelectionBackground());
	            	syntax.setForeground(list.getSelectionForeground());
	            }

	            return p;
	        }
		});
		mList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mList.setLayoutOrientation(JList.VERTICAL);
		
		mTextArea = new JLabel();
		mTextArea.setPreferredSize(new Dimension(width-6, 20));
		mTextArea.setText("");
		mTextArea.setBackground(Color.white);
		mTextArea.setOpaque(true);
		mPanel.add(mTextArea);
		
		JScrollPane listScroller = new JScrollPane(mList);
		listScroller.setPreferredSize(new Dimension(width-6, 167));
		listScroller.setBorder(null);
		mPanel.add(listScroller);
		setSize(width, 200);
		
		mFilterString = "";
		updateFilter(mFilterString);
	}
	
	protected void addEntry(SimpleAutoCompleteEntry entry) { 
		if(mClassRestrictions != null) { 
			Class<?> thisEntryClass = entry.getInstanceClass();
			for(Class<?> restriction : mClassRestrictions) {
				if(restriction.isAssignableFrom(thisEntryClass)) { 
					mAllEntries.add(entry);
					break;
				}
			}
		} else { 
			mAllEntries.add(entry);	
		}
	}
	
	public JLabel getEntryField() { 
		return mTextArea;
	}
	
	public void onKeyPressed(KeyEvent event) { 
		switch(event.getKeyCode()) { 
			case KeyEvent.VK_BACK_SPACE:
				if(mFilterString.length() > 0) { 
					mFilterString = mFilterString.substring(0, mFilterString.length() - 1);
				}
				
				mTextArea.setText(mFilterString);
				updateFilter(mFilterString);
				break;
				
			case KeyEvent.VK_ENTER:
				// Submit the current selection
				int selectedIndex = mList.getSelectedIndex();
				if(selectedIndex >= 0) { 
					SimpleAutoCompleteEntry entry = mFilteredEntries.get(selectedIndex);
					mListener.onAutoCompleteCreateReplacement(mController, entry);
				}
				break;
				
			case KeyEvent.VK_UP:
				// Move the selection index upwards
				mList.setSelectedIndex(mList.getSelectedIndex() - 1);
				mList.ensureIndexIsVisible(mList.getSelectedIndex());
				break;
				
			case KeyEvent.VK_DOWN:
				// Move the selection index downwards
				mList.setSelectedIndex(mList.getSelectedIndex() + 1);
				mList.ensureIndexIsVisible(mList.getSelectedIndex());
				break;
				
			default:
				if(event.getKeyChar() != KeyEvent.CHAR_UNDEFINED) { 
					mFilterString = mFilterString + event.getKeyChar();
					mTextArea.setText(mFilterString);
					updateFilter(mFilterString);	
				}
				break;
		}
	}
	
	private void updateFilter(String filterText) { 
		mModel.removeAllElements();
		mFilteredEntries.clear();
		
		for(SimpleAutoCompleteEntry entry : mAllEntries) { 
			if(entry.isUsableForFilterString(filterText)) { 
				DisplayPair pair = new DisplayPair();
				pair.title = entry.getTitle();
				pair.description = entry.getDescription();
				
				if(entry.isExactForFilterString(filterText)) { 
					// Add to the beginning of the filtered list
					mModel.add(0, pair);
					mFilteredEntries.add(0, entry);
				} else { 
					// Add to the end of the filtered list
					mModel.addElement(pair);				
					mFilteredEntries.add(entry);
				}
			}
		}
		
		mList.setSelectedIndex(0);

		if(filterText.length() == 0) {
			mTextArea.setText("enter filter text");
			mTextArea.setForeground(Color.LIGHT_GRAY);
		} else {
			mTextArea.setText(filterText);
			mTextArea.setForeground(Color.BLACK);
		} 
	}
}

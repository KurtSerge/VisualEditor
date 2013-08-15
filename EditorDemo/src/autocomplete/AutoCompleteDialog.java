package autocomplete;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import clojure.constructs.meta.UnknownConstruct;
import construct.Construct;
import editor.ConstructEditor;
import editor.InterfaceController;
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
	
	public static class SimpleAutoCompleteEntry { 
		public SimpleAutoCompleteEntry(String title, Class<?> instance) { 
			mTitle = title;
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
		private Class<?> mClass;		
	}
	
	public AutoCompleteDialog(InterfaceController controller, ConstructEditor editor, IAutoCompleteListener listener) {
		mListener = listener;
		mEditor = editor;
		mController = controller;
		
		mClassRestrictions = editor.construct.getAutoCompleteClasses();
		mAllEntries = new LinkedList<SimpleAutoCompleteEntry>();
		mFilteredEntries = new LinkedList<SimpleAutoCompleteEntry>();
		
		// Basic types
		addEntry(new SimpleAutoCompleteEntry("integer", clojure.constructs.IntegerConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("string", clojure.constructs.StringConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("symbol", clojure.constructs.SymbolConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("double", clojure.constructs.DoubleConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("boolean", clojure.constructs.BooleanConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("keyword", clojure.constructs.KeywordConstruct.class));
		
		// Containers
		addEntry(new SimpleAutoCompleteEntry("list", clojure.constructs.containers.ListConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("map", clojure.constructs.containers.MapConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("vector", clojure.constructs.containers.VectorConstruct.class));
		
		// Placeholder types
		addEntry(new SimpleAutoCompleteEntry("def", clojure.constructs.meta.DefConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("defn", clojure.constructs.meta.DefineFunctionConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("do", clojure.constructs.meta.DoConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("case", clojure.constructs.meta.CaseConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("fn", clojure.constructs.meta.FunctionConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("if then else", clojure.constructs.meta.IfThenElseConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("keyword pair", clojure.constructs.meta.KeywordExpressionPairConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("let", clojure.constructs.meta.LetConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("loop", clojure.constructs.meta.LoopConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("recur", clojure.constructs.meta.RecurConstruct.class));
		addEntry(new SimpleAutoCompleteEntry("function", clojure.constructs.meta.SymbolList.class));
		
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
		mPanel.setSize(300, 200);
		add(mPanel);
		
		mList = new JList(mModel);
		mList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mList.setLayoutOrientation(JList.VERTICAL);
		
		mTextArea = new JLabel();
		mTextArea.setPreferredSize(new Dimension(294, 20));
		mTextArea.setText("");
		mTextArea.setBackground(Color.white);
		mTextArea.setOpaque(true);
		mPanel.add(mTextArea);
		
		JScrollPane listScroller = new JScrollPane(mList);
		listScroller.setPreferredSize(new Dimension(294, 167));
		listScroller.setBorder(null);
		mPanel.add(listScroller);
		setSize(300, 200);
		
		mFilterString = "";
		updateFilter(mFilterString);
	}
	
	protected void addEntry(SimpleAutoCompleteEntry entry) { 
		if(mClassRestrictions != null) { 
			if(mClassRestrictions.contains(entry.getInstanceClass())) {
				mAllEntries.add(entry);
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
				mFilterString = mFilterString + event.getKeyChar();
				mTextArea.setText(mFilterString);
				updateFilter(mFilterString);
				break;
		}
	}
	
	private void updateFilter(String filterText) { 
		mModel.removeAllElements();
		mFilteredEntries.clear();
		
		for(SimpleAutoCompleteEntry entry : mAllEntries) { 
			if(entry.isUsableForFilterString(filterText)) { 
				if(entry.isExactForFilterString(filterText)) { 
					// Add to the beginning of the filtered list
					mModel.add(0, entry.getTitle());
					mFilteredEntries.add(0, entry);
				} else { 
					// Add to the end of the filtered list
					mModel.addElement(entry.getTitle());				
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

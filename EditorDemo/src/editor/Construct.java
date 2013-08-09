package editor;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import json.JSONController;


public abstract class Construct
{
	protected static final String BREAKING_SPACE = "$(break)";
	
	public final String type;
	public final Construct parent;
	public UUID workspace;
	public final UUID instance;	
	
	/**
	 * When calling getConstructForSelection(), specify a purpose for
	 * selection. This allows constructs to override which construct 
	 * should get selected during certain selection causes.
	 */
	public static enum SelectionCause { 
		SelectedReplacementDiscoveredDuringKeyEvent,
		SelectedDuringFind,
		SelectedDirectlyWithMouse,
		SelectedAfterInsert,
		SelectedRandomly,
		SelectedParent,
		SelectedFirstChild,
		SelectedAdjacentConstruct,
		SelectedInPlaceOfDeletedConstruct,	
		SelectedAfterDeletingChild,
		
		Selected, // Generic
	}

	
	protected Construct(String type, Construct parent)
	{
		this.type = type;
		this.parent = parent;
		instance = UUID.randomUUID();
	}

	
	/**
	 * @return null if the literal should be used instead
	 */
	public abstract String screen_text();
	
	public abstract boolean validate();
	
	public int nesting_level()
	{
		int ret = 0;
		
		for(Construct c = this.parent;c != null;c = c.parent, ++ret);
		
		return ret;
	}
	
	public String literal = null;
	protected List<Construct> children = new LinkedList<Construct>();
	
	
	public void debugPrint() {
		debugPrintNode(this, 0);
	}
	
	public void debugPrintNode(Construct con, int depth) {
		for(int i = 0; i < depth; i++)
			System.out.print("\t");
		if(con == null)  {
			System.out.println("null");
			return;
		}
		
		String nodeStr = "Type:" + con.type + ", Literal:" + con.literal + ", Children:" + con.children.size();
		System.out.println(nodeStr);
		
		for(Construct child : con.children) {
			debugPrintNode(child, depth+1);
		}
	}
	
	

	public boolean delete(boolean shouldValidate, boolean isUser) { 
		if(parent != null)  {
			// Validate the deletion of this child
			int index = parent.children.indexOf(this);
			
			boolean canDeleteChild = true;
			if(shouldValidate == true) { 
				canDeleteChild = parent.canDeleteChild(index, this, isUser);
			}
			
			if(canDeleteChild) {
				parent.children.remove(index);
				parent.onChildDeleted(index, this);
				AddToUndoBuffer();		
				return true;
			}

			return false;
		}
		else {
			// FIXME: how to delete top from inside?
		}
	
		return true; 		
	}

	public boolean delete() {
		return delete(true, false);
	}

	final public boolean addChild(Construct child) { 
		return addChild(this.children.size(), child);
	}
	
	/**
	 * Adds a child to this construct allowing for multiple children to
	 * be added as a side-along effect. 
	 *
	 * @return True if child was sucessfully added
	 */
	final public boolean addChild(int index, Construct child) {
		if(canAddChild(index, child) == true)  {
			List<Construct> previousChildren = new ArrayList<Construct>(this.children);
			Collections.copy(previousChildren, this.children);

			// Perform basic add, and do callback
			children.add(index, child);
			onChildAdded(index, child);
			AddToUndoBuffer();
			
			// Check to see how many children were actually added as the 
			// callback may, in some cases, add additional children
			ConstructPublisher publisher = ConstructPublisher.getInstance();
			if(previousChildren.size() + 1 == children.size()) { 
				publisher.onConstructAddedChild(this, child, index);
			} else { 
				// Keep a map of new children and their index
				Map<Construct, Integer> newChildren = new HashMap<Construct, Integer>();			
				for(int i = 0; i < this.children.size(); i++) { 
					if(previousChildren.contains(this.children.get(i)) == false) { 
						newChildren.put(children.get(i), i);
					}
				}

				// Publish each of the added children
				Set<Construct> set = newChildren.keySet();
				for(Construct key : set) { 
					publisher.onConstructAddedChild(this, key, newChildren.get(key));				
				}
			}

			return true;
		}

		return false;
	}
	
	// editor should call this
	public boolean replaceChild(Construct replaceMe, Construct newCon) {
		// FIXME: really you need to make the replacement first, then validate, then rollback if the replace is invalid
		int newIndex = children.indexOf(replaceMe);
		boolean success = addChild(newIndex, newCon);
		if(success == true) {
			if(children.remove(replaceMe) == false) {
				//assert(1==0);
			}
		}
		return success;
	}
	
	public List<Construct> getChildren() { 
		return children;
	}

	// Each subclass must implement this.  
	public Construct deepCopy(Construct parent) {
		for( Construct child : this.children) {
			parent.children.add(child.deepCopy(parent));
		}

		return null;
	}

	public Color debug_getForegroundColor() {
		return Color.BLACK;
	}
	
	// For undo/redo buffers
	protected static List<Construct> treeChanges = new ArrayList<Construct>();
	protected static int treeChangeIndex = 0;
	protected static Construct last = null;
	public void AddToUndoBuffer() {
		// Find top of tree
		Construct top = this;
		while(top.parent != null) {
			top = top.parent;
		}
		// TODO: What to do if we add a change, but thetreechangeindex is not at the end? handle this
		treeChanges.add(top.deepCopy(null));
		treeChangeIndex = treeChanges.size()-1;
	}
	
	public static Construct getUndo() {
		if(treeChanges.size() == 0 )
			return null;
		
		treeChangeIndex = Math.max(treeChangeIndex-1, 0);
		Construct ret = treeChanges.get(treeChangeIndex);

		if(last != ret) {
			last = ret;
			return ret;
		}
		else 
			return null;
	}
	
	public static Construct getRedo() {
		if(treeChanges.size() == 0 || treeChangeIndex == treeChanges.size()-1)
			return null;

		treeChangeIndex = Math.min(treeChangeIndex+1, treeChanges.size()-1);
		Construct ret = treeChanges.get(treeChangeIndex);

		if(last != ret) {
			last = ret;
			return ret;
		}
		else
			return null;
	}
	
	private boolean mIsMultilined;

	public void setMultilined(boolean isMultilined) {
		mIsMultilined = isMultilined;
	}
	
	public boolean getIsMultilined() { 
		return mIsMultilined;
	}
	
	/**
	 * @return Adds support for breaking space in screen_text()
	 */
	public String layout(String string) {
		if(this.getIsMultilined()) { 
			return string.replace(BREAKING_SPACE, "\n");
		} else { 
			return string.replace(BREAKING_SPACE, " ");
		}
	}
	
	/* ----- DERRIVED FUNCTIONALITY ----- */

	// Override this to set special conditions for when a child can be deleted
	protected boolean canDeleteChild(int index, Construct child, boolean isUser) {
		return true;
	}

	// Check that child being added at the specified index is valid
	protected boolean canAddChild(int index, Construct child) {
		return true;
	}

	/* ----- NOTIFICATIONS ----- */
	
	public void onChildAdded(int index, Construct child) { 
	}
	
	protected void onChildDeleted(int index, Construct deleted) {
		ConstructPublisher.getInstance().onConstructRemovedChild(this, deleted, index);
	}	
	
	/**
	 * When this becomes a part of the branch selection. For instance,
	 * it may be called while manually descending the tree or when a
	 * descendant of this construct is selected. Meaning, this construct
	 * *may* be the selected node, but it is at least in the selected 
	 * branch of the tree.
	 */
	public void onBranchHighlighted() {
	}
	
	/**
	 * This is called when this construct is no longer part of the
	 * branches selection (ie, the parent is now selected).
	 */
	public void onBranchUnhighlighted() { 
	}
	
	/**
	 * An unhandled key was typed whilst this construct was selected.
	 * Give this construct a chance to handle the keystroke.
	 * 
	 * @param e The KeyEvent causing the trigger
	 * @param isTyping True if editing this.literal
	 * @return True to consume the event ( can also call e.consume() )
	 */
	public boolean onReceivedKeyEvent(KeyEvent e, boolean isTyping) {
		return false;
	}
	
	/**
	 * Specify the construct that should be selected in a
	 * specific type of selection situation.
	 */
	public Construct getConstructForSelection(SelectionCause selection) { 
		return this;
	}
	
	/**
	 * Indicate if the parent should be used when considering
	 * core actions such as selection movement / deletion.
	 */
	public boolean isSoleDependantConstruct() { 
		return false;
	}
}

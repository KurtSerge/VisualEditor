package editor;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import json.JSONController;


public abstract class Construct
{
	protected Construct(String type, Construct parent)
	{
		this.type = type;
		this.parent = parent;
		instance = UUID.randomUUID();
	}
	
	public final String type;
	public final Construct parent;
	public UUID workspace; // Each construct will have user-defined rules for formatting.  example: a function with too many parameters goes onto a new line
	public UUID instance;
	
	//@Override
	//public boolean equals(Object o) {
		//Construct compare = (Construct)o;
		//boolean uuidcompare = this.instance.compareTo(compare.instance) == 0;
		//return (uuidcompare);
	//}
	
	/**
	 * @return null if the literal should be used instead
	 */
	public abstract String screen_text();
	
	public int nesting_level()
	{
		int ret = 0;
		
		for(Construct c = this.parent;c != null;c = c.parent, ++ret);
		
		return ret;
	}
	
	public String literal = null;
	public List<Construct> children = new LinkedList<Construct>();
	
	
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
	
	public abstract boolean validate();

	// Returns true if deleted
	final public boolean delete() {
		if(parent != null)  {
			int index = parent.children.indexOf(this);
			if(parent.canDeleteChild(index, this) == false)
				return false;
			parent.children.remove(this);
			parent.handleDeleteChild();
			AddToUndoBuffer();
		}
		else {
			// FIXME: how to delete top from inside?
		}
	
		return true; 
	}

	
	// Override this for special rules, example: KV-Pair must have at least 2 children
	public void handleDeleteChild() {
	}
	
	// Override this to set special conditions for when a child can be deleted
	public boolean canDeleteChild(int index, Construct child) {
		return true;
	}

	
	// Check that child being added at the specified index is valid
	public /*FIXME:abstract*/ boolean validateAddChild(int index, Construct child) {
		return true;
	}
	
	// TODO: How to force contract to use addChild instead of this.children.add
	final public boolean addChild(int index, Construct child) {
		if(validateAddChild(index, child) == true)  {
			children.add(index, child);
			AddToUndoBuffer();
			return true;
		}
		// Else consider making empty TODO:
		return false;
	}
	
	// editor should call this
	final public boolean replaceChild(Construct replaceMe, Construct newCon) {
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

	// Each subclass must implement this.  
	public Construct deepCopy(Construct parent) {
		for( Construct child : this.children) {
			parent.children.add(child.deepCopy(parent));
		}

		return null;
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
}

package editor;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public abstract class Construct
{
	protected Construct(String type, Construct parent)
	{
		this.type = type;
		this.parent = parent;
	}
	
	public final String type;
	public final Construct parent;
	public UUID workspace; // Each construct will have user-defined rules for formatting.  example: a function with too many parameters goes onto a new line

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

	final public void delete() {
		if(this.parent != null)  {
			this.parent.children.remove(this);
		}
	}
	
	// Override this for special rules, example: KV-Pair must have at least 2 children
	public boolean deleteChild(Construct child) {
		int childIndex = this.children.indexOf(child);
		this.children.remove(childIndex);
		return true;
	}
	
	// Set child leaf to an empty construct (deletes all children)
	final public void setEmpty(Construct child) {
		int index = children.indexOf(child);
		child.delete();
		children.add(index, new EmptyConstruct(this));
	}
	
	// Check that child being added at the specified index is valid
	public /*FIXME:abstract*/ boolean validateAddChild(int index, Construct child) {
		return true;
	}
	
	// TODO: How to force contract to use addChild instead of this.children.add
	final public boolean addChild(int index, Construct child) {
		if(validateAddChild(index, child) == true)  {
			children.add(index, child);
			return true;
		}
		// Else consider making empty TODO:
		return false;
	}
	
	final public boolean replaceChild(Construct replaceMe, Construct newCon) {
		// FIXME: really you need to make the replacement first, then validate, then rollback if the replace is invalid
		int newIndex = replaceMe.parent.children.indexOf(replaceMe);
		boolean success = addChild(newIndex, newCon);
		if(success == true) {
			replaceMe.parent.children.remove(replaceMe);
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
}

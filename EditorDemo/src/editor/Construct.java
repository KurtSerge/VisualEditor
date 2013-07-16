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

	public void delete() {
		if(this.parent != null)  {
			this.parent.children.remove(this);
			
			//int myIndex = deleteMeCon.parent.children.indexOf(deleteMeCon);
			//deleteMeCon.parent.children.set(myIndex, null);
		}
	}
}

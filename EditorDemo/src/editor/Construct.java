package editor;
import java.util.LinkedList;
import java.util.List;


public abstract class Construct
{
	protected Construct(String type, Construct parent)
	{
		this.type = type;
		this.parent = parent;
	}
	
	public final String type;
	public final Construct parent;

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
}

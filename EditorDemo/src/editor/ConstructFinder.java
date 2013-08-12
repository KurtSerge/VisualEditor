package editor;

import construct.Construct;

// FIXME: Could probably be more efficient.  Starts search from beginning of tree instead of continuing
public class ConstructFinder {
	private final Construct head;
	private final String lit;
	private int found;
	private int foundLast;
	
	public ConstructFinder(Construct head, String lit) {
		this.head = head;
		this.lit = lit;
		found = 0;
		foundLast = 0;
	}
	
	public Construct nextLiteral() {
		found = 0;
		return nextLiteral(head);
	}
	
	private Construct nextLiteral(Construct search) {
		if(search.literal != null && search.literal.contains(lit)) {
			found++;
			if(found > foundLast) {
				foundLast = found;
				return search;
			}
		}
		
		for(Construct child : search.children) {
			Construct found = nextLiteral(child);
			if(found != null)
				return found; 
		}
		
		if(search.parent == null)
			foundLast = 0;
		return null;
	}
}

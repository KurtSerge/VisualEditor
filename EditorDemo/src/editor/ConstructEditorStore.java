package editor;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConstructEditorStore {
	
	private Map<Construct, WeakReference<ConstructEditor>> mStore;
	
	public ConstructEditorStore() { 
		mStore = Collections.synchronizedMap(new HashMap<Construct, WeakReference<ConstructEditor>>());
	}

	public void register(ConstructEditor editor) { 
		if(editor != null) { 
			mStore.put(editor.construct, new WeakReference<ConstructEditor>(editor));
		}
		
//		System.out.println("Size after insertion: " + mStore.keySet().size());
	}
	
	public void unregister(ConstructEditor editor) {
		if(editor != null && editor.construct != null) { 
			System.out.println("Removing " + editor.construct.type);
			mStore.remove(editor.construct);
		}
		
//		System.out.println("Size after removal: " + mStore.keySet().size());
		
		for(WeakReference<ConstructEditor> stillAround : getEditors()) { 
			if(stillAround.get() != null) { 
//				System.out.println(stillAround.get().construct.type);
			}
		}
	}
	
	public WeakReference<ConstructEditor> get(Construct construct) { 
		return mStore.get(construct);
	}
	
	public List<WeakReference<ConstructEditor>> getEditors() { 
		return new LinkedList<WeakReference<ConstructEditor>>(mStore.values());
	}
}

package editor;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import construct.Construct;

public class ConstructEditorStore {
	
	private Map<Construct, WeakReference<ConstructEditor>> mStore;
	
	public ConstructEditorStore() { 
		mStore = Collections.synchronizedMap(new HashMap<Construct, WeakReference<ConstructEditor>>());
	}

	public void register(ConstructEditor editor) { 
		if(editor != null) { 
			mStore.put(editor.construct, new WeakReference<ConstructEditor>(editor));
		}
	}
	
	public void unregister(ConstructEditor editor) {
		if(editor != null && editor.construct != null) { 
			mStore.remove(editor.construct);			
		}
	}
	
	public WeakReference<ConstructEditor> get(Construct construct) { 
		return mStore.get(construct);
	}
	
	public List<WeakReference<ConstructEditor>> getEditors() { 
		return new LinkedList<WeakReference<ConstructEditor>>(mStore.values());
	}
}

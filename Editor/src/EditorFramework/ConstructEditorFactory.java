package EditorFramework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class ConstructEditorFactory {
	// Map of URI's to ConstructEditor
	private final Map<String, Class> m_RegisteredConstructEditors;

	public ConstructEditorFactory() {
		m_RegisteredConstructEditors = new HashMap<String, Class>();
	}
	
	public void registerConstructEditor (String URI, Class constructEditorClass) {
		m_RegisteredConstructEditors.put(URI, constructEditorClass);
	}
	
	// URI can be null, will return default Editor (monospace)
	// Add preferences for default editor later
	public ConstructEditor createConstructEditor(String URI, Construct construct)
	{
		Class constructClass = (Class)m_RegisteredConstructEditors.get(URI);
		Constructor constructConstructor = null;
		try {
			constructConstructor = constructClass.getDeclaredConstructor(Construct.class);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			return (ConstructEditor) constructConstructor.newInstance(construct);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
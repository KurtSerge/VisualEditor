package EditorFramework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import XML.URIConfig;


public class ConstructEditorFactory {
	// Map of URI's to ConstructEditor
	private final Map<String, Class> m_RegisteredConstructEditors;
	private final URIConfig config;
	
	public ConstructEditorFactory(String configFilename) {
		m_RegisteredConstructEditors = new HashMap<String, Class>();
    	config = new URIConfig(configFilename);
	}
	
	/*
	private void registerConstructEditor (String URI) {
		String className = config.getClassName(URI);
		Class constructEditorClass = null;
		if(className.length() != 0) {
			try {
				constructEditorClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			m_RegisteredConstructEditors.put(URI, constructEditorClass);
		}
	}*/
	
	// URI can be null, will return default Editor (monospace)
	// Add preferences for default editor later
	public ConstructEditor createConstructEditor(String uri)
	{
		String className = config.getClassName(uri);
		if(className == null) {
			return null;
		}

		Class constructClass;
		try {
			constructClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		
		try {
			return (ConstructEditor) constructClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		// FIXME
/*
		try {
			constructConstructor = constructClass.getDeclaredConstructor(Construct.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		try {
			return (ConstructEditor) constructConstructor.newInstance(construct);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}*/
	}
}
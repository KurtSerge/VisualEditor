package EditorFramework;


import java.util.Map;


public class ConstructEditorFactory {
	// Map of URI's to ConstructEditor
	private Map<String, Class> m_RegisteredConstructEditors;

	public void registerConstructEditor (String URI, Class constructEditorClass) {
	}

	public ConstructEditor createConstructEditor(String URI, Construct construct)
	{
		return null;
	}
}


	
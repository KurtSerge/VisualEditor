package EditorFramework;

import java.util.Map;


public class ConstructEditorFactory {
	// Map of URI's to ConstructEditor
	private Map<String, Class> m_RegisteredConstructEditors;

	public void registerConstructEditor (String URI, Class constructEditorClass) {
	}

	public ConstructEditor createConstructEditor(String URI, Construct construct)
	{
		// URI can be null, will return default Editor (monospace)
		// Add preferences for default editor later
		return null;
	}
}
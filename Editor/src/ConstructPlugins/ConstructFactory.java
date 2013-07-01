package ConstructPlugins;

import java.util.Map;

import EditorFramework.Construct;


public class ConstructFactory {
	// Map of URI's to Construct
	private Map<String, Class> m_RegisteredConstructs;

	public void registerConstruct (String URI, Class constructClass) {
	}

	public Construct createConstruct(String URI)
	{
		return null;
	}
}


	
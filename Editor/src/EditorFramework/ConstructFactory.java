package EditorFramework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import GenericTree.GenericTreeNode;
import XML.URIConfig;


public class ConstructFactory {
	// Map of URI's to ConstructEditor
	private final URIConfig config;
	
	public ConstructFactory(String configFilename) {
    	config = new URIConfig(configFilename);
	}
	
	// URI can be null, will return default Editor (monospace)
	// Add preferences for default editor later
	public ConstructEditor createConstructEditor(String uri, Construct construct)
	{
		String className = config.getEditorClassName(uri);
		if(className == null) {
			return null;
		}

		Class editorClass;
		Constructor constructor;
		try {
			editorClass = Class.forName(className);
			constructor = editorClass.getDeclaredConstructor(Construct.class);
		} catch (ClassNotFoundException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
		
		
		try {
			return (ConstructEditor) constructor.newInstance(construct);
		} catch (InstantiationException e) { 
			e.printStackTrace();
			return null;
		} catch(IllegalAccessException e) { 
			e.printStackTrace();
			return null;
		} catch(IllegalArgumentException e) { 
			e.printStackTrace();
			return null;
		} catch(InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public Construct createConstruct(String uri, GenericTreeNode<SyntaxTreeElement> node)
	{
		String className = config.getConstructClassName(uri);
		if(className == null) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		Class constructClass;
		Constructor constructor;
		try {
			constructClass = Class.forName(className);
			constructor = constructClass.getDeclaredConstructor(GenericTreeNode.class);
		} catch (ClassNotFoundException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
		
		try {
			return (Construct) constructor.newInstance(node);
		} catch (InstantiationException e) { 
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
}
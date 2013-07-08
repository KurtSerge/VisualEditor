
package json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;


import editor.Construct;
import editor.ConstructEditor;
import editor.MonospaceConstructEditor;

public class JSONController
{
	public static ArrayList<ConstructEditor> editors;
	
	// TODO: This belongs in a utility function elsewhere
	static public ConstructEditor editors_from_constructs(Construct top)
	{
		for(Construct child : top.children)
			editors_from_constructs(child);
		
		MonospaceConstructEditor my_editor = new MonospaceConstructEditor(top);
		
		if(editors == null)
			editors = new ArrayList<ConstructEditor>();
		editors.add(my_editor);
		
		return my_editor;
	}

	static public Construct construct_for_json(Object object, Construct parent)
	{
		if(object.getClass().equals(JSONObject.class))
		{
			json.ObjectConstruct object_construct = new json.ObjectConstruct(parent);
			
			String[] keys = JSONObject.getNames((JSONObject)object);
			
			for(String key : keys)
			{
				Object child = ((JSONObject)object).get(key);
				//if(child.getClass() == String.class) {
						json.KeyValueConstruct key_value_construct = new json.KeyValueConstruct(object_construct);
						key_value_construct.children.add(construct_for_json(key, key_value_construct));
						key_value_construct.children.add(construct_for_json(child, key_value_construct));
						object_construct.children.add(key_value_construct);
				//}
				//else {
				//	object_construct.children.add(construct_for_json(key, object_construct));
				//	object_construct.children.add(construct_for_json(child, object_construct));
				//}
			}
			
			return object_construct;
		}
		if(object.getClass().equals(JSONArray.class))
		{
			json.ArrayConstruct object_construct = new json.ArrayConstruct(parent);
			
			JSONArray json_array = (JSONArray)object;
			
			for(int i=0;i<json_array.length();++i)
			{
				Object child = json_array.get(i);
				
				object_construct.children.add(construct_for_json(child, object_construct));
			}
			
			return object_construct;
		}
		if(object.getClass().equals(String.class))
		{
			json.StringConstruct string_construct = new json.StringConstruct(parent);
			
			String json_string = (String)object;
			
			json.StringLiteralConstruct string_literal_construct = new json.StringLiteralConstruct(string_construct, json_string);
			
			string_construct.children.add(string_literal_construct);
			
			return string_construct;
		}
		else
			throw new RuntimeException("Unknown JSON type, this should never happen");
	}
	
	static public Construct load_json(InputStream in)
	{
		JSONObject json = new JSONObject(new JSONTokener(in));
	
		return construct_for_json(json, null);
	}
}

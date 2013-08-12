
package json;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import construct.Construct;
import editor.EmptyConstruct;

public class JSONController 
{
//	public static ArrayList<ConstructEditor> editors;
	
//	// TODO: This belongs in a utility function elsewhere
//	static public ConstructEditor editors_from_constructs(Construct top)
//	{
//		for(Construct child : top.children)
//			editors_from_constructs(child);
//		
//		MonospaceConstructEditor my_editor = new MonospaceConstructEditor(top);
//
//		if(editors == null)
//			editors = new ArrayList<ConstructEditor>();
//		editors.add(my_editor);
//		
//		return my_editor;
//	}


	static public Construct get_empty_kvp(Construct parent)  {
		if(parent.getClass() != ObjectConstruct.class)
			return null;
		
		json.KeyValueConstruct key_value_construct = new json.KeyValueConstruct(parent.getDocument(), parent);
		key_value_construct.addChild(construct_for_json("EmptyString", key_value_construct));
		key_value_construct.addChild(new EmptyConstruct(parent.getDocument(), key_value_construct));
		return key_value_construct;
	}

	public static void save_json(Construct saveme, String filename, int indent) {
		JSONObject saved = (JSONObject)JSONController.json_for_construct(saveme, null);

	
		PrintWriter out = null;
		try {
			out = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String outStr = saved.toString(indent);
		outStr = outStr.replace("\n", "\r\n");
		//System.out.print(outStr);
		
		out.println(outStr);
		
		out.close();
	}
	
	public static Object json_for_construct(Construct con, JSONObject parent) {
		Object ret = null; 
	
		if(con.type == "object") {
			ret = new JSONObject();
			for(Construct child : con.getChildren()) {
				json_for_construct(child, (JSONObject)ret);
			}
		}
		else if(con.type == "key_value_pair") {
			assert(con.getChildren().get(0).type == "string");
			parent.put((String)json_for_construct(con.getChildren().get(0), parent), json_for_construct(con.getChildren().get(1), parent));
		}
		else if(con.type == "string") {
			ret = json_for_construct(con.getChildren().get(0), parent);
		}
		else if(con.type == "integer") {
			ret = Integer.valueOf(con.literal);
		}
		else if(con.type == "float") {
			ret = Float.valueOf(con.literal);
		}
		else if(con.type == "null") {
			ret = JSONObject.NULL;
		}
		else if(con.type == "boolean") {
			ret = Boolean.valueOf(con.literal);
		}
		else if(con.type == "string_literal") {
			ret = con.literal;
		}
		else if(con.type == "array") {
			JSONArray  array = new JSONArray();
			
			for(Construct child : con.getChildren()) 
				array.put(json_for_construct(child, parent));
			ret = array;
		}
		else
			throw new RuntimeException("Unknown JSON type, this should never happen");

		return ret;
	}
	
	static public Construct construct_for_json(Object object, Construct parent)
	{
		if(object.getClass().equals(JSONObject.class))
	    {
	      json.ObjectConstruct object_construct = new json.ObjectConstruct(parent.getDocument(), parent);
	      
	      String[] keys = JSONObject.getNames((JSONObject)object);
	      
	      if(keys != null) {
		      for(String key : keys)
		      {
				Object child = ((JSONObject)object).get(key);
				
				json.KeyValueConstruct key_value_construct = new json.KeyValueConstruct(parent.getDocument(), object_construct);
				key_value_construct.addChild(construct_for_json(key, key_value_construct));
				key_value_construct.addChild(construct_for_json(child, key_value_construct));
				
				object_construct.addChild(key_value_construct);
		      }
	      }
	      return object_construct;
		} 
		if(object.getClass().equals(JSONArray.class))
		{
			json.ArrayConstruct object_construct = new json.ArrayConstruct(parent.getDocument(), parent);
			
			JSONArray json_array = (JSONArray)object;
			
			for(int i=0;i<json_array.length();++i)
			{
				Object child = json_array.get(i);
				
				object_construct.addChild(construct_for_json(child, object_construct));
			}
			
			return object_construct;
		}
		if(object.getClass().equals(String.class))
		{
			json.StringConstruct string_construct = new json.StringConstruct(parent.getDocument(), parent);
			
			String json_string = (String)object;
			
			json.StringLiteralConstruct string_literal_construct = new json.StringLiteralConstruct(parent.getDocument(), string_construct, json_string);
			
			string_construct.addChild(string_literal_construct);
			
			return string_construct;
		}
		if(object.getClass().equals(Double.class))
		{
			String json_string = object.toString();
			json.FloatConstruct float_construct = new json.FloatConstruct(parent.getDocument(), parent, json_string);

			return float_construct;
		}
		if(object.getClass().equals(Integer.class))
		{
			String json_string = object.toString();
			json.IntegerConstruct integer_construct = new json.IntegerConstruct(parent.getDocument(), parent, json_string);

			return integer_construct;
		}
		if(object.getClass().equals(Boolean.class))
		{
			String json_string = object.toString();
			json.BooleanConstruct boolean_construct = new json.BooleanConstruct(parent.getDocument(), parent, json_string);

			return boolean_construct;
		}
		if(object==(org.json.JSONObject.NULL))
		{
			String json_string = object.toString();
			json.NullConstruct null_construct = new json.NullConstruct(parent.getDocument(), parent, json_string);

			return null_construct;
		}
		else
			throw new RuntimeException("Unknown JSON type, this should never happen");
	}
}

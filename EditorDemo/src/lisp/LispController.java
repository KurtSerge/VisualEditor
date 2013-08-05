
package lisp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;


import editor.Construct;
import editor.ConstructEditor;
import editor.MonospaceConstructEditor;

public class LispController
{
	static public Construct construct_for_json(Object object, Construct parent)
	{
		if(object.getClass().equals(JSONArray.class))
		{
			SExpressionConstruct sexp_construct = new SExpressionConstruct(parent);
			
			JSONArray json_array = (JSONArray)object;
			
			for(int i=0;i<json_array.length();++i)
			{
				Object child = json_array.get(i);
				
				sexp_construct.addChild(construct_for_json(child, sexp_construct));
			}
			
			return sexp_construct;
		}
		else if(object.getClass().equals(String.class))
		{
			String s = (String)object;
			
			StringConstruct string_construct = new StringConstruct(parent);
			
			String json_string = (String)object;
			
			StringLiteralConstruct string_literal_construct = new StringLiteralConstruct(string_construct, json_string);
			
			string_construct.addChild(string_literal_construct);
			
			return string_construct;
		}
		else
			throw new RuntimeException("Unknown JSON type");
	}
	
	static public Construct load_json(InputStream in)
	{
		JSONObject json = new JSONObject(new JSONTokener(in));
	
		return construct_for_json(json.get("top"), null);
	}
}

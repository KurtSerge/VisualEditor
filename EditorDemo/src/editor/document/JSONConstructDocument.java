package editor.document;

import java.io.FileNotFoundException;
import java.io.InputStream;

import json.JSONController;

import org.json.JSONObject;
import org.json.JSONTokener;

import construct.Construct;

public class JSONConstructDocument extends ConstructDocument {

	public JSONConstructDocument(String filename) 
			throws FileNotFoundException
	{
		super(filename);
	}

	@Override
	protected Construct loadConstruct(InputStream in) { 
		JSONObject json = new JSONObject(new JSONTokener(in));
		
		return JSONController.construct_for_json(json, null);
	}
}

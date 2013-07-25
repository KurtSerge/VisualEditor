package editor.document;

import java.io.FileNotFoundException;
import java.io.InputStream;

import json.JSONController;

import org.json.JSONObject;
import org.json.JSONTokener;

import editor.Construct;
import editor.document.Document;

public class JSONDocument extends Document {

	public JSONDocument(String filename) 
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

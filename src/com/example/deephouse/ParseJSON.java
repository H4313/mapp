package com.example.deephouse;

import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;

public class ParseJSON
{
	private String json = "";

	public ParseJSON(String url)
	{    
		AsyncTask<String, Integer, String> jsonTask = new ReadJSONTask().execute(url);
		
		try	{
			json = jsonTask.get();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public String getJson()
	{
		return json;
	}
	
	public JSONArray getJSONArray()
	{
		JSONArray array = null;
		
		try {
			array = new JSONArray(this.json);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return array;
	}
	
	public JSONObject getJSONObject()
	{
		JSONObject object = null;
		
		try {
			object = new JSONObject(this.json);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}
}
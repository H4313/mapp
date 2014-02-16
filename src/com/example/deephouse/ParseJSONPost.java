package com.example.deephouse;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class ParseJSONPost
{
	private String json = "";

	public ParseJSONPost(String url, List<NameValuePair> argumentsList)
	{    
		String[] urls={};
		urls[0] = url;
		for (int i=0;i<=argumentsList.size();i++){
			urls[2*i+1] =argumentsList.get(i).getName();
			urls[2*(i+1)] = argumentsList.get(i).getValue();
		}
		AsyncTask<String, Integer, String> jsonTask = new ReadJSONPost().execute(urls);
		
		try
		{
			json = jsonTask.get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
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
		try
		{
			array = new JSONArray(this.json);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return array;
	}
	
	public JSONObject getJSONObject()
	{
		JSONObject object = null;
		try
		{
			object = new JSONObject(this.json);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return object;
	}
}
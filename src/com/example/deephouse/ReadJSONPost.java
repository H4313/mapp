package com.example.deephouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class ReadJSONPost extends AsyncTask<String, Integer, String>
{
    // Expected : URL + key value
    protected String doInBackground(String... urls)
    {
    	//Getting post arguments back from string tab
    	List<NameValuePair> argumentsList = new ArrayList<NameValuePair>((urls.length-1)/2);
    	String url = urls[0];
    	
    	for (int i=0;i<(urls.length-1)/2;i++)
    		argumentsList.add(new BasicNameValuePair(urls[2*i+1],urls[2*(i+1)]));
  
    	StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		try	{
			httpPost.setEntity(new UrlEncodedFormEntity(argumentsList));
			HttpResponse response = client.execute(httpPost);
			
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if (statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				
				String line;
				while ((line = reader.readLine()) != null)
					builder.append(line);
			}
			else
			{
				Log.e(ReadJSONPost.class.toString(), "Failed to download json file");
			}
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
    }

    protected void onprogressUpdate(Integer... progress)
    {
    	
    }

    protected void onPostExecute(String result)
    {
    	
    }
}

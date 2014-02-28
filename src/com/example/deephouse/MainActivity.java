package com.example.deephouse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.deephouse.MESSAGE";
	public static final String PREFS_NAME = "DeepHousePrefs";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null){
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment())
			.commit();
		}
		restoreLoginInfos();
		getActionBar().hide();
	}

	private void restoreLoginInfos(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		String ip = settings.getString("ip","");
		String login = settings.getString("login", "");
		String password = settings.getString("password","");
		final TextView textViewIp = (TextView) findViewById(R.id.editTextIP);
		final TextView textViewLogin = (TextView) findViewById(R.id.editTextLogin);
		final TextView textViewPassword = (TextView) findViewById(R.id.editTextPassword);
		if (!ip.equals("") || !login.equals("") || !password.equals("")){
			textViewIp.setText(ip);
			textViewLogin.setText(login);
			textViewPassword.setText(password);
			//Try connecting with the infos
			connexion(findViewById(0));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

	/** Called when the user clicks the connection button */
	public void connexion(View view) {
		EditText editTextLogin = (EditText) findViewById(R.id.editTextLogin); 
		String login = editTextLogin.getText().toString();
		EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword); 
		String password = editTextPassword.getText().toString();
		EditText editTextIP = (EditText) findViewById(R.id.editTextIP);      
		String ip = editTextIP.getText().toString();
		EchangesModeleMaison.setIp(ip);
		if (login.equals("admin") && password.equals("root")){
			//Checking connectivity
			try {
				String retour = EchangesModeleMaison.recupererMaison();
				if (retour.contains("true")){
					Intent intent = new Intent(this, HouseActivity.class); //creates next activity
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("ip", ip)
					.commit();
					startActivity(intent);}
				else{
					displayError("IP invalide 1");
				}
			} catch(Exception e){
				displayError("IP invalide 2"); //seems to never be used
			}
		}
		else{
			displayError("Adresse IP, nom d'utilisateur ou mot de passe invalide.");
		};
	}

	public boolean isReachable(String ip){
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
					new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		try {
			return InetAddress.getByName(ip).isReachable(100);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public void displayError(String error){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(error)
		.setCancelable(true)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do things
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onStop(){
		super.onStop();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		final CheckBox checkBoxInfos = (CheckBox) findViewById(R.id.checkBoxSaveInfo);
		if (checkBoxInfos.isChecked()){
			//Retrieving infos
			final TextView textViewLogin = (TextView) findViewById(R.id.editTextLogin);
			final TextView textViewPassword = (TextView) findViewById(R.id.editTextPassword);
			String login = textViewLogin.getText().toString();
			String password = textViewPassword.getText().toString();
			//Saving infos
			editor.putString("password", password)
			.putString("login", login)
			// Commit the edits!
			.commit();
		}
	}

}

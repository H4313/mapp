package com.example.deephouse;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.housemodel.RoomConstants;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.Constant;

public class HouseActivity extends Activity
{
	public final static String EXTRA_MESSAGE = "com.example.deephouse.MESSAGE";
	public static final String PREFS_NAME = "DeepHousePrefs";
	public Handler handler;
	public volatile Boolean stop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_house);

		//Getting intent content
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment())
			.commit();
		}

		//View updates
		handler = new Handler();
		handler.post(refresh); //automatic refresh
	}

	private final Runnable refresh = new Runnable()
	{
		@Override
		public void run()
		{
			if (!stop)
			{
				EchangesModeleMaison.updateHouse(); // Recuperation du Json House (modele a jour de la maison) sur le serveur
				updateView(); // mise a jour de la vue avec ce nouveau modele.
				updateTime();
				handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);
			}         
		}
	};

	@Override
	protected void onDestroy()
	{
		stop = true;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.house, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId())
		{
			case R.id.action_settings:
				return true;
			case R.id.action_deconnexion:
				//Clear login informations
				SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
				SharedPreferences.Editor editor = settings.edit();
				editor.remove("ip")
				.remove("login")
				.remove("password")
				.commit();
				// Application automatic launch
				Intent homeIntent = new Intent(this, MainActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		public PlaceholderFragment()
		{
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_house, container, false);
			return rootView;
		}
	}

	/** Called when the user clicks on a room of the house */
	public void openConfigurationL(View view){
		openConfiguration(view,0);
	}
	public void openConfigurationK(View view){
		openConfiguration(view,1);
	}
	public void openConfigurationWC(View view){
		openConfiguration(view,2);
	}
	public void openConfigurationB1(View view){
		openConfiguration(view,3);
	}
	public void openConfigurationB2(View view){
		openConfiguration(view,4);
	}
	public void openConfigurationC(View view){
		openConfiguration(view,5);
	}
	public void openConfiguration(View view, Integer position)
	{
		Intent intent = new Intent(this, HouseConfigActivity.class); //creates configuration activity
		//Putting text field content in intent
		intent.putExtra(EXTRA_MESSAGE, position);
		startActivity(intent);
	}

	private void updateTime()
	{
		TextView textView = (TextView) findViewById(R.id.textViewHeure);
		
		try
		{
			String timeMil = EchangesModeleMaison.getCurrentDate();
			Date date = new Date(Long.parseLong(timeMil));
			SimpleDateFormat formatedDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatedHours = new SimpleDateFormat("HH:mm:ss");
			textView.setText(formatedDate.format(date)+"\n"+formatedHours.format(date));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// Updates temperature and human presence in every room.
	private void updateView()
	{
		House house = House.getInstance();

		// Getting TextView's displaying temperature and presence in each room.
		for(Room r : house.getRooms())
		{
			int textViewTemperature;
			int idImagePerson;

			switch(r.getIdRoom())
			{
				case RoomConstants.ID_LIVING_ROOM : 
					textViewTemperature = R.id.textViewLivingRoomTemperature;
					idImagePerson = R.id.ImagePersonLivingroom;
					break;
				case RoomConstants.ID_KITCHEN : 
					textViewTemperature = R.id.textViewKitchenTemperature;
					idImagePerson = R.id.ImagePersonKitchen;
					break;
				case RoomConstants.ID_BATHROOM : 
					textViewTemperature = R.id.textViewBathroomTemperature;
					idImagePerson = R.id.ImagePersonBathroom;
					break;
				case RoomConstants.ID_BEDROOM : 
					textViewTemperature = R.id.textViewBedroomTemperature;
					idImagePerson = R.id.ImagePersonBedroom;
					break;
				case RoomConstants.ID_OFFICE : 
					textViewTemperature = R.id.textViewOfficeTemperature;
					idImagePerson = R.id.ImagePersonOffice;
					break;
				case RoomConstants.ID_CORRIDOR : 
					textViewTemperature = R.id.textViewCorridorTemperature;
					idImagePerson = R.id.ImagePersonCorridor;
					break;
				default :
					textViewTemperature = -1;
					idImagePerson = -1;
					System.out.println("Erreur rencontree : tentative de MAJ de la piece n" + r.getIdRoom() + " inexistante.");
					return;
			}

			// Updates if the sensor exists
			for(String key : r.getSensors().keySet())
			{
				if(r.getSensors().get(key).getType() == SensorType.TEMPERATURE)
				{
					TextView textView = (TextView) findViewById(textViewTemperature);
					String temperatureValue = r.getSensors().get(key).getLastValue().toString();
					
					if (temperatureValue.length() >4)
						temperatureValue = temperatureValue.substring(0,4);
					
					textView.setText(temperatureValue + " C");
				}
				else if(r.getSensors().get(key).getType() == SensorType.PRESENCE)
				{
					ImageView imageView = (ImageView) findViewById(idImagePerson);
					
					if((Boolean)r.getSensors().get(key).getLastValue())
						imageView.setVisibility(ImageView.VISIBLE);
					else
						imageView.setVisibility(ImageView.INVISIBLE);
				}
			}
		}
	}

	public void displayStats(View view)
	{
		//Launching stats intent
		Intent statsIntent = new Intent(this, StatsActivity.class);
		statsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(statsIntent);
	}
}
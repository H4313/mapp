package com.example.deephouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.housemodel.RoomConstants;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.Constant;

public class HouseConfigActivity extends FragmentActivity implements
ActionBar.TabListener {

	public final static String EXTRA_MESSAGE = "com.example.deephouse.MESSAGE";
	public Integer currentTab = 1;
	public Handler handler = new Handler();
	public List<Integer> toEnable = new ArrayList<Integer>(); //ordered list of tabs where temperature buttons will be re-enabled
	public volatile Boolean stop = false; //to disable handlers when the activity is destroyed
	public static Boolean temperatureOrder0 = false;
	public static Boolean temperatureOrder1 = false;
	public static Boolean temperatureOrder2 = false;
	public static Boolean temperatureOrder3 = false;
	public static Boolean temperatureOrder4 = false;
	public static Boolean temperatureOrder5 = false;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_houseconfig);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of rooms
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		//Enable back button
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.home);
		actionBar.setDisplayShowTitleEnabled(false);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the application, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
			;
		}

		//Getting the clicked room (from the intent)
		Intent intent = getIntent();
		Integer position = intent.getIntExtra(MainActivity.EXTRA_MESSAGE,0);
		mViewPager.setCurrentItem(position);
		currentTab = position;

		//Views update
		handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);
	}

	private final Runnable refresh = new Runnable()
	{
		@Override
		public void run()
		{
			//NB : House model is already updated by parent view HouseActivity
			if (!stop){
				//Update the view
				FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
				adapter.notifyDataSetChanged();
				handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);
			}
		}
	};

	public void addSensor(View view){
		Intent intent = new Intent(this, AddActivity.class);
		//Putting current room number in intent
		intent.putExtra(EXTRA_MESSAGE, currentTab);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.house_config_activity2, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		currentTab = tab.getPosition();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case android.R.id.home:
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position+1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override //To force refresh. Avoid it if there are performance issues.
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return RoomConstants.NB_PIECES;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			case 5:
				return getString(R.string.title_section6).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the application
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_houseconfig, container,
					false);
			//Setting tab title
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			int idPiece = getArguments().getInt(ARG_SECTION_NUMBER)-1;
			dummyTextView.setText("				Configuration de la piece " + getRoomNameById(idPiece).toLowerCase(Locale.FRANCE));	
			updateFragment(idPiece,inflater,container, rootView);
			return rootView;
		}

		public void updateFragment(int position, LayoutInflater inflater, ViewGroup container, View rootView)
		{
			House house = House.getInstance();
			Room r = house.getRooms().get(position);

			for(String key : r.getSensors().keySet())
			{
				SensorType sensorType = r.getSensors().get(key).getType();

				if(sensorType == SensorType.PRESENCE)
				{
					TextView presence = (TextView) rootView.findViewById(R.id.TextViewPresence);
					TextView presenceValue = (TextView) rootView.findViewById(R.id.TextViewPresenceValue);
					ImageView imgPerson = (ImageView) rootView.findViewById(R.id.imagePerson);

					//set visibility
					presence.setVisibility(TextView.VISIBLE);
					if((Boolean) r.getSensors().get(key).getLastValue())
					{
						imgPerson.setVisibility(TextView.VISIBLE);
					}
					else
					{
						presenceValue.setVisibility(TextView.VISIBLE);
						presenceValue.setText("Aucune");
					}
				}
				else if(sensorType == SensorType.TEMPERATURE)
				{
					TextView temperature = (TextView) rootView.findViewById(R.id.textViewTemperature);
					TextView temperatureValue = (TextView) rootView.findViewById(R.id.TextViewTemperatureValue);
					ImageView imgTemperature = (ImageView) rootView.findViewById(R.id.imageTemperature);
					Button increaseTemperatureButton = (Button) rootView.findViewById(R.id.increaseTemperatureButton);
					Button lowerTemperatureButton = (Button) rootView.findViewById(R.id.lowerTemperatureButton);

					//set visibility
					imgTemperature.setVisibility(TextView.VISIBLE);
					temperature.setVisibility(TextView.VISIBLE);
					temperatureValue.setVisibility(TextView.VISIBLE);
					increaseTemperatureButton.setVisibility(TextView.VISIBLE);
					lowerTemperatureButton.setVisibility(TextView.VISIBLE);

					//update value
					String valueTemperature = r.getSensors().get(key).getLastValue().toString();
					if (valueTemperature.length()>4){
						valueTemperature = valueTemperature.substring(0,4);
					}
					temperatureValue.setText(valueTemperature);
				}
				else
				{
					Switch capteurSwitch;
					TextView capteurText;
					ImageView capteurImg;

					if(sensorType == SensorType.DOOR)
					{
						capteurSwitch = (Switch) rootView.findViewById(R.id.switchPorte);
						capteurText = (TextView) rootView.findViewById(R.id.TextViewPorte);
						capteurImg = (ImageView) rootView.findViewById(R.id.imageDoor);
					}
					else if(sensorType == SensorType.FLAP)
					{
						capteurSwitch = (Switch) rootView.findViewById(R.id.switchVolets);
						capteurText = (TextView) rootView.findViewById(R.id.TextViewVolets);
						capteurImg = (ImageView) rootView.findViewById(R.id.imageVolets);
					}
					else if(sensorType == SensorType.LIGHT)
					{
						capteurSwitch = (Switch) rootView.findViewById(R.id.switchLumiere);
						capteurText = (TextView) rootView.findViewById(R.id.TextViewLumiere);
						capteurImg = (ImageView) rootView.findViewById(R.id.imageLight);
					}
					else //(sensorType == SensorType.WINDOW)
					{
						capteurSwitch = (Switch) rootView.findViewById(R.id.switchFenetre);
						capteurText = (TextView) rootView.findViewById(R.id.TextViewFenetre);
						capteurImg = (ImageView) rootView.findViewById(R.id.imageWindow);
					}

					//set visibility
					capteurSwitch.setVisibility(TextView.VISIBLE);
					capteurText.setVisibility(TextView.VISIBLE);
					capteurImg.setVisibility(TextView.VISIBLE);

					//update value
					capteurSwitch.setChecked((Boolean) r.getSensors().get(key).getLastValue());

					//Temperature buttons visibility
					if (temperatureOrderGiven(position)){
						Button increaseTemperatureButton = (Button) rootView.findViewById(R.id.increaseTemperatureButton);
						Button lowerTemperatureButton = (Button) rootView.findViewById(R.id.lowerTemperatureButton);
						TextView temperatureValueTextView = (TextView) rootView.findViewById(R.id.TextViewTemperatureValue);

						increaseTemperatureButton.setClickable(false);
						lowerTemperatureButton.setClickable(false);

						increaseTemperatureButton.setBackgroundColor(getResources().getColor(R.color.Lavender));
						lowerTemperatureButton.setBackgroundColor(getResources().getColor(R.color.Lavender));
						if(rootView.getId() == R.id.increaseTemperatureButton)
							temperatureValueTextView.setTextColor(getResources().getColor(R.color.Coral));
						else // (v.getId() == R.id.lowerTemperatureButton)
							temperatureValueTextView.setTextColor(getResources().getColor(R.color.LightSlateGray));
					}
				}
			}
		}



		public String getRoomNameById(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1);
			case 1:
				return getString(R.string.title_section2);
			case 2:
				return getString(R.string.title_section3);
			case 3:
				return getString(R.string.title_section4);
			case 4:
				return getString(R.string.title_section5);
			case 5:
				return getString(R.string.title_section6);
			}
			return null;
		}
	}

	public static Boolean temperatureOrderGiven(Integer position){
		Boolean value = false;
		switch (position){
		case 0: 
			value = temperatureOrder0;
			break;
		case 1: 
			value = temperatureOrder1;
			break;
		case 2: 
			value = temperatureOrder2;
			break;
		case 3: 
			value = temperatureOrder3;
			break;
		case 4: 
			value = temperatureOrder4;
			break;
		case 5: 
			value = temperatureOrder5;
			break;
		}
		return value;
	}

	public static void giveTemperatureOrder(Integer position){
		switch(position){
		case 0: 
			temperatureOrder0 = true;
			break;
		case 1: 
			temperatureOrder1 = true;
			break;
		case 2: 
			temperatureOrder2 = true;
			break;
		case 3: 
			temperatureOrder3 = true;
			break;
		case 4: 
			temperatureOrder4 = true;
			break;
		case 5: 
			temperatureOrder5 = true;
			break;
		}	
	}

	public static void withdrawTemperatureOrder(Integer position){
		switch(position){
		case 0: 
			temperatureOrder0 = false;
			break;
		case 1: 
			temperatureOrder1 = false;
			break;
		case 2: 
			temperatureOrder2 = false;
			break;
		case 3: 
			temperatureOrder3 = false;
			break;
		case 4: 
			temperatureOrder4 = false;
			break;
		case 5: 
			temperatureOrder5 = false;
			break;
		}	
	}

	public void switchAction(View view){
		Switch s = (Switch) view;
		try {
			if (view.getId() ==R.id.switchFenetre)
				EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.windAction, s.isChecked());
			if (view.getId() ==R.id.switchLumiere)
				EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.lightAction, s.isChecked());	
			if (view.getId() ==R.id.switchPorte)
				EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.doorAction, s.isChecked());			
			if (view.getId() ==R.id.switchVolets)
				EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.flapAction, s.isChecked());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void temperatureButton(View v)
	{
		giveTemperatureOrder(currentTab);
		//Sending the server the order to change temperature
		if(v.getId() == R.id.increaseTemperatureButton)
			temperatureChangeRequest(true);
		else //v.getId() == R.id.lowerTemperatureButton
			temperatureChangeRequest(false);
		//Update the view
		FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
		adapter.notifyDataSetChanged();
		//Re-enable buttons again after a given time
		toEnable.add(currentTab);
		handler.postDelayed(unmuteTemperatureButtons,
				Constant.MILLISECONDS_TILL_UNMUTING_TEMPERATURE_BUTTONS);
	}

	private final Runnable unmuteTemperatureButtons = new Runnable()
	{
		@Override
		public void run()
		{
			if (!stop){
				Integer position = toEnable.get(0);
				toEnable.remove(0);
				withdrawTemperatureOrder(position);
				//Update the view
				FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
				adapter.notifyDataSetChanged();	
			}
		}
	};

	private void temperatureChangeRequest(boolean isIncrease)
	{
		House house = House.getInstance();
		Room r = house.getRooms().get(currentTab);

		for(String key : r.getSensors().keySet())
		{
			SensorType sensorType = r.getSensors().get(key).getType();

			if(sensorType == SensorType.TEMPERATURE)
			{
				//update value
				double lastValue = (Double) r.getSensors().get(key).getLastValue();
				double variationValue = isIncrease? 
						Constant.RELATIVE_TEMPERATURE_INCREASE_ON_USER_RQST :
							Constant.RELATIVE_TEMPERATURE_DECREASE_ON_USER_RQST ;
				double newValue = lastValue + variationValue;

				try
				{
					EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.tempAction, newValue);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDestroy(){
		stop = true; 
		super.onDestroy();
	}
} 

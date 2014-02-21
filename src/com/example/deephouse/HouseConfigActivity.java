package com.example.deephouse;

import java.util.Locale;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
    public Handler handler;
    public Handler handlerTemperatureButtonMuting;
    public Integer cst = 0;
    
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
		}
		
        //Getting the clicked room (from the intent)
        Intent intent = getIntent();
        Integer position = intent.getIntExtra(MainActivity.EXTRA_MESSAGE,0);
        mViewPager.setCurrentItem(position);
        currentTab = position;
        
        //Views update
        handler = new Handler();
        handler.postAtTime(init, SystemClock.uptimeMillis()+100); //first refresh (delayed because waiting for instantiation)
        //handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH); //TODO : Refresh management
	}
	
	private final Runnable init = new Runnable()
	{
		@Override
	    public void run()
	    {
	        updateView();
	    }
	};
	
	private final Runnable refresh = new Runnable()
	{
		@Override
	    public void run()
	    {
			//House model is already updated by parent view HouseActivity
	        updateView();
			handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);
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
        if (cst > 2){
        handler.post(init);}
        cst++;
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
	 * A dummy fragment representing a section of the app
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
			//Editing the TextViews
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			int idPiece = getArguments().getInt(ARG_SECTION_NUMBER)-1;
			System.out.println(idPiece);
			dummyTextView.setText("				Configuration de la piece " + getRoomNameById(idPiece).toLowerCase(Locale.FRANCE));			return rootView;
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
	
	public void updateView()
	{
        House house = House.getInstance();
        Room r = house.getRooms().get(currentTab);
        
		for(String key : r.getSensors().keySet())
		{
			SensorType sensorType = r.getSensors().get(key).getType();

			if(sensorType == SensorType.PRESENCE)
			{
				//System.out.println("Presence yeah");
				TextView presence = (TextView) findViewById(R.id.TextViewPresence);
				TextView presenceValue = (TextView) findViewById(R.id.TextViewPresenceValue);
				ImageView imgPerson = (ImageView) findViewById(R.id.imagePerson);
				
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
				//System.out.println("Temperature yeah");
				TextView temperature = (TextView) findViewById(R.id.textViewTemperature);
				TextView temperatureValue = (TextView) findViewById(R.id.TextViewTemperatureValue);
				ImageView imgTemperature = (ImageView) findViewById(R.id.imageTemperature);
				Button increaseTemperatureButton = (Button) findViewById(R.id.increaseTemperatureButton);
				Button lowerTemperatureButton = (Button) findViewById(R.id.lowerTemperatureButton);
				
				//set visibility
				imgTemperature.setVisibility(TextView.VISIBLE);
				temperature.setVisibility(TextView.VISIBLE);
				temperatureValue.setVisibility(TextView.VISIBLE);
				increaseTemperatureButton.setVisibility(TextView.VISIBLE);
				lowerTemperatureButton.setVisibility(TextView.VISIBLE);
				increaseTemperatureButton.setOnClickListener((OnClickListener) onTemperatureChangeRequest);
				lowerTemperatureButton.setOnClickListener((OnClickListener) onTemperatureChangeRequest);
				
				//update value
				temperatureValue.setText(r.getSensors().get(key).getLastValue().toString());
			}
			else
			{
				Switch capteurSwitch;
				TextView capteurText;
				ImageView capteurImg;
				
				if(sensorType == SensorType.DOOR)
				{
					capteurSwitch = (Switch) findViewById(R.id.switchPorte);
					capteurText = (TextView) findViewById(R.id.TextViewPorte);
					capteurImg = (ImageView) findViewById(R.id.imageDoor);
				}
				else if(sensorType == SensorType.FLAP)
				{
					capteurSwitch = (Switch) findViewById(R.id.switchVolets);
					capteurText = (TextView) findViewById(R.id.TextViewVolets);
					capteurImg = (ImageView) findViewById(R.id.imageVolets);
				}
				else if(sensorType == SensorType.LIGHT)
				{
					capteurSwitch = (Switch) findViewById(R.id.switchLumiere);
					capteurText = (TextView) findViewById(R.id.TextViewLumiere);
					capteurImg = (ImageView) findViewById(R.id.imageLight);
				}
				else //(sensorType == SensorType.WINDOW)
				{
					capteurSwitch = (Switch) findViewById(R.id.switchFenetre);
					capteurText = (TextView) findViewById(R.id.TextViewFenetre);
					capteurImg = (ImageView) findViewById(R.id.imageWindow);
				}

				//set visibility
				capteurSwitch.setVisibility(TextView.VISIBLE);
				capteurText.setVisibility(TextView.VISIBLE);
				capteurImg.setVisibility(TextView.VISIBLE);
				capteurSwitch.setOnClickListener((OnClickListener) onSwitchClick);
				
				//update value
				capteurSwitch.setChecked((Boolean) r.getSensors().get(key).getLastValue());
			}
		}
	}
	
	private OnClickListener onSwitchClick = new OnClickListener()
	{
		public void onClick(View v)
		{
			Switch s = (Switch) v;
			try
			{
				switch(v.getId())
				// the ID of a room (from 0 to 5) is directly given by its currentTab (from 0 to 5) - according to com.h4313.deephouse.housemodel.room.RoomConstants
				{	
					case R.id.switchFenetre :
						EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.windAction, s.isChecked());
						break;
					case R.id.switchLumiere :
						EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.lightAction, s.isChecked());
						break;
					case R.id.switchVolets :
						EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.flapAction, s.isChecked());
						break;
					case R.id.switchPorte :
						EchangesModeleMaison.actionUtilisateur(currentTab, RoomConstants.doorAction, s.isChecked());
						break;
				}
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
	};
	
	private OnClickListener onTemperatureChangeRequest = new OnClickListener()
	{
		public void onClick(View v)
		{
			//muting buttons for a given time
			Button increaseTemperatureButton = (Button) findViewById(R.id.increaseTemperatureButton);
			Button lowerTemperatureButton = (Button) findViewById(R.id.lowerTemperatureButton);
			TextView temperatureValueTextView = (TextView) findViewById(R.id.TextViewTemperatureValue);
			
			increaseTemperatureButton.setClickable(false);
			lowerTemperatureButton.setClickable(false);
			
			increaseTemperatureButton.setBackgroundColor(getResources().getColor(R.color.Lavender));
			lowerTemperatureButton.setBackgroundColor(getResources().getColor(R.color.Lavender));
			if(v.getId() == R.id.increaseTemperatureButton)
				temperatureValueTextView.setTextColor(getResources().getColor(R.color.Coral));
			else // (v.getId() == R.id.lowerTemperatureButton)
				temperatureValueTextView.setTextColor(getResources().getColor(R.color.LightSlateGray));
						
			//following is enabling buttons again after a given time
			handlerTemperatureButtonMuting = new Handler();
			handlerTemperatureButtonMuting.postDelayed(unmuteTemperatureButtons,
					Constant.MILLISECONDS_TILL_UNMUTING_TEMPERATURE_BUTTONS);// /Constant.TIME_FACTOR
			
			if(v.getId() == R.id.increaseTemperatureButton)
				temperatureChangeRequest(true);
			else //v.getId() == R.id.lowerTemperatureButton
				temperatureChangeRequest(false);
		}
	};
	
	private final Runnable unmuteTemperatureButtons = new Runnable()
	{
		@SuppressLint("ResourceAsColor")
		@Override
	    public void run()
	    {
			//enable buttons clickable
			Button increaseTemperatureButton = (Button) findViewById(R.id.increaseTemperatureButton);
			Button lowerTemperatureButton = (Button) findViewById(R.id.lowerTemperatureButton);
			
			increaseTemperatureButton.setBackgroundColor(R.color.Coral);
			lowerTemperatureButton.setBackgroundColor(R.color.LightSlateGray);
			
			increaseTemperatureButton.setClickable(true);
			lowerTemperatureButton.setClickable(true);
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
				
				//System.out.println("Temperature change request : from " + lastValue + " to " + newValue);
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
} 

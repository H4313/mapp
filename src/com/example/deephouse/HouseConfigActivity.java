package com.example.deephouse;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Handler;
import android.widget.TableRow.LayoutParams;

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

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_houseconfig);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

        //Enable home button
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

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
        //Selection of the clicked room (from the intent)
        Intent intent = getIntent();
        Integer position = intent.getIntExtra(MainActivity.EXTRA_MESSAGE,0);
        mViewPager.setCurrentItem(position);
        currentTab = position;
        //Activate back button
        actionBar.setHomeButtonEnabled(true);
        
        // Instanciation des TextView        
        HouseActivity.localHouseInstanciation();
        House house = House.getInstance();
        Room r = house.getRooms().get(0);
        
        TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
        
       // MAJ si le capteur de temperature / presence existe
		for(String key : r.getSensors().keySet())
		{
			SensorType sensorType = r.getSensors().get(key).getType();

			if(sensorType == SensorType.PRESENCE) // traitement 1
			{
				System.out.println("Presence yeah");
			
				TextView tx = new TextView(this);
			    tx.setText("Presence = " + r.getSensors().get(key).getLastValue());
			    TableRow tr = new TableRow(this);
		        tr.addView(tx);
		        tl.addView(tr);
			}
			else if(sensorType == SensorType.TEMPERATURE) // traitement 2
			{
				System.out.println("Temperature yeah");
			
				TextView tx = new TextView(this);
			    tx.setText("Temperature = " + r.getSensors().get(key).getLastValue());
			    TableRow tr = new TableRow(this);
		        tr.addView(tx);
		        tl.addView(tr);
			}
			else if(sensorType == SensorType.DOOR 
				 || sensorType == SensorType.FLAP
				 || sensorType == SensorType.LIGHT
				 || sensorType == SensorType.WINDOW) // traitement 3
			{
				System.out.println("ELSE yeah");
			
				TextView tx = new TextView(this);
			    tx.setText(sensorType + " = " + r.getSensors().get(key).getLastValue());
				System.out.println(tx.getText());

			    TableRow tr = new TableRow(this);
		        tr.addView(tx);

		        tl.addView(tr);
		        }
		}
        
        //MAJ vue periodique
        handler = new Handler();
        handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);
	}
	
	private final Runnable refresh = new Runnable()
	{
		@Override
	    public void run()
	    {
	    	//Instantiation de la maison depuis le Json avec Gson
	        EchangesModeleMaison.majHouseModel();
			handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);            
	    }
	};//runnable


    public void addSensor(View view){
        Intent intent = new Intent(this, AddActivity.class);
        //Putting current room in intent
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
			dummyTextView.setText("Nombre de capteurs dans la pièce : " + House.getInstance().getRooms().get(idPiece).getSensors().size());
			return rootView;
		}
	}
} 
